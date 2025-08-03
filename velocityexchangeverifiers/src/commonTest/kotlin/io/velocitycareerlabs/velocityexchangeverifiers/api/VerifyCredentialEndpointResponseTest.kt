/**
 * Created by Michael Avoyan on 25/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api

import io.velocitycareerlabs.velocityexchangeverifiers.api.VerifiersApi.verifyCredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialIssuerMetadata
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialVerifiers
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.ErrorCode
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtHeader
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtPayload
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationError
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import io.velocitycareerlabs.velocityexchangeverifiers.impl.extensions.encodeAsJsonElement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

internal class VerifyCredentialEndpointResponseTest {
    private val baseContext =
        VerificationContext(
            path = emptyList(),
            credentialIssuerMetadata =
                CredentialIssuerMetadata(
                    iss = "did:issuer:example",
                    credentialIssuer = "https://issuer.example.com",
                ),
        )

    private fun mockCredentialJson(): JsonObject =
        buildJsonObject {
            put(
                "header",
                buildJsonObject {
                    put("alg", JsonPrimitive("ES256"))
                    put("kid", JsonPrimitive("did:velocity:v2:abc123"))
                },
            )
            put(
                "payload",
                buildJsonObject {
                    put("iss", JsonPrimitive("did:issuer:example"))
                    put("sub", JsonPrimitive("did:jwk"))
                    put("vc", buildJsonObject { }) // empty VC object
                },
            )
        }

    private fun createMockCredential(): W3CCredentialJwtV1 =
        W3CCredentialJwtV1(
            header =
                JwtHeader(
                    claims =
                        mapOf(
                            "alg" to JsonPrimitive("ES256"),
                            "kid" to JsonPrimitive("did:velocity:v2:abc123"),
                        ),
                ),
            payload =
                JwtPayload(
                    claims =
                        mapOf(
                            "iss" to JsonPrimitive("did:issuer:example"),
                            "sub" to JsonPrimitive("did:jwk"),
                            "vc" to JsonObject(emptyMap()),
                        ),
                ),
        )

    @Test
    fun `returns no errors when credentials are valid`() {
        val mockVerifiers =
            CredentialVerifiers(
                algIsSupported = { _, _ -> null },
                credentialSchema = { _, _ -> null },
                credentialStatus = { _, _ -> null },
                issClaimMatchesEitherMetadataOrCredentialIssuer = { _, _ -> null },
                issClaimMatchesMetadata = { _, _ -> null },
                kidClaimIsVelocityV2 = { _, _ -> null },
                subIsDidJwkOrCnf = { _, _ -> null },
            )

        val response =
            CredentialEndpointResponse(
                claims = mapOf("credentials" to listOf(createMockCredential()).encodeAsJsonElement()),
            )

        val result = verifyCredentialEndpointResponse(response, baseContext, mockVerifiers)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `returns all errors when credentials are invalid`() {
        val expectedError =
            VerificationError(
                code = ErrorCode.INVALID_ALG,
                message = "alg is not supported",
                path = listOf("credentials", 0, "header", "alg"),
            )

        val mockVerifiers =
            CredentialVerifiers(
                algIsSupported = { _, _ -> expectedError },
                credentialSchema = { _, _ -> null },
                credentialStatus = { _, _ -> null },
                issClaimMatchesEitherMetadataOrCredentialIssuer = { _, _ -> null },
                issClaimMatchesMetadata = { _, _ -> null },
                kidClaimIsVelocityV2 = { _, _ -> null },
                subIsDidJwkOrCnf = { _, _ -> null },
            )

        val response =
            CredentialEndpointResponse(
                claims = mapOf("credentials" to listOf(createMockCredential()).encodeAsJsonElement()),
            )

        val result = verifyCredentialEndpointResponse(response, baseContext, mockVerifiers)

        assertEquals(listOf(expectedError), result)
    }

    @Test
    fun `aggregates errors from multiple credentials`() {
        val errorsList =
            listOf(
                VerificationError(
                    code = ErrorCode.INVALID_KID,
                    message = "Invalid kid",
                    path = listOf("credentials", 0, "payload", "kid"),
                ),
                VerificationError(
                    code = ErrorCode.MISSING_CREDENTIAL_STATUS,
                    message = "Missing credentialStatus",
                    path = listOf("credentials", 1, "payload", "vc", "credentialStatus"),
                ),
            )

        var credentialIndex = 0
        val mockVerifiers =
            CredentialVerifiers(
                algIsSupported = { _, _ -> null },
                credentialSchema = { _, _ -> null },
                credentialStatus = { _, _ ->
                    if (credentialIndex == 1) errorsList[1] else null
                },
                issClaimMatchesEitherMetadataOrCredentialIssuer = { _, _ -> null },
                issClaimMatchesMetadata = { _, _ -> null },
                kidClaimIsVelocityV2 = { _, _ ->
                    if (credentialIndex == 0) errorsList[0] else null
                },
                subIsDidJwkOrCnf = { _, _ ->
                    credentialIndex++
                    null
                },
            )

        val credentials = listOf(createMockCredential(), createMockCredential())

        val response =
            CredentialEndpointResponse(
                claims = mapOf("credentials" to credentials.encodeAsJsonElement()),
            )

        val result = verifyCredentialEndpointResponse(response, baseContext, mockVerifiers)

        assertEquals(errorsList, result)
    }

    @Test
    fun `returns empty list when credentials are missing`() {
        val response = CredentialEndpointResponse(claims = emptyMap())
        val result = verifyCredentialEndpointResponse(response, baseContext, erroringVerifiers())

        assertEquals(emptyList(), result)
    }

    @Test
    fun `returns empty list when credentials is an empty array`() {
        val response =
            CredentialEndpointResponse(
                claims = mapOf("credentials" to emptyList<JsonElement>().encodeAsJsonElement()),
            )
        val result = verifyCredentialEndpointResponse(response, baseContext, erroringVerifiers())

        assertEquals(emptyList(), result)
    }

    private fun erroringVerifiers(): CredentialVerifiers =
        CredentialVerifiers(
            algIsSupported = { _, _ -> error("Should not be called") },
            credentialSchema = { _, _ -> error("Should not be called") },
            credentialStatus = { _, _ -> error("Should not be called") },
            issClaimMatchesEitherMetadataOrCredentialIssuer = { _, _ -> error("Should not be called") },
            issClaimMatchesMetadata = { _, _ -> error("Should not be called") },
            kidClaimIsVelocityV2 = { _, _ -> error("Should not be called") },
            subIsDidJwkOrCnf = { _, _ -> error("Should not be called") },
        )
}
