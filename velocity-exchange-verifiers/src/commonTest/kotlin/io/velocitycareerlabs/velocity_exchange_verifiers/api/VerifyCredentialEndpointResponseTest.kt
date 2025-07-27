/**
 * Created by Michael Avoyan on 25/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocity_exchange_verifiers.api

import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.CredentialIssuerMetadata
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.CredentialVerifiers
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.ErrorCode
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.JwtHeader
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.JwtPayload
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VcClaims
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationError
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.W3CCredentialJwtV1
import kotlin.test.Test
import kotlin.test.assertEquals

class VerifyCredentialEndpointResponseTest {

    private val baseContext = VerificationContext(
        path = emptyList(),
        credentialIssuerMetadata = CredentialIssuerMetadata(
            iss = "did:issuer:example",
            credentialIssuer = "https://issuer.example.com"
        )
    )

    private val mockCredential = W3CCredentialJwtV1(
        header = JwtHeader(alg = "ES256", kid = "did:velocity:v2:abc123"),
        payload = JwtPayload(
            iss = "did:issuer:example",
            vc = VcClaims(),
            sub = "did:jwk"
        )
    )

    @Test
    fun `returns no errors when credentials are valid`() {
        val mockVerifiers = CredentialVerifiers(
            algIsSupported = { _, _ -> null },
            credentialSchema = { _, _ -> null },
            credentialStatus = { _, _ -> null },
            issClaimMatchesEitherMetadataOrCredentialIssuer = { _, _ -> null },
            issClaimMatchesMetadata = { _, _ -> null },
            kidClaimIsVelocityV2 = { _, _ -> null },
            subIsDidJwkOrCnf = { _, _ -> null }
        )

        val response = CredentialEndpointResponse(credentials = listOf(mockCredential))
        val result = verifyCredentialEndpointResponse(response, baseContext, mockVerifiers)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `returns all errors when credentials are invalid`() {
        val mockErrors = listOf(
            VerificationError(
                code = ErrorCode.INVALID_ALG,
                message = "alg is not supported",
                path = listOf("credentials", 0, "header", "alg")
            )
        )

        val mockVerifiers = CredentialVerifiers(
            algIsSupported = { _, _ -> mockErrors[0] },
            credentialSchema = { _, _ -> null },
            credentialStatus = { _, _ -> null },
            issClaimMatchesEitherMetadataOrCredentialIssuer = { _, _ -> null },
            issClaimMatchesMetadata = { _, _ -> null },
            kidClaimIsVelocityV2 = { _, _ -> null },
            subIsDidJwkOrCnf = { _, _ -> null }
        )

        val response = CredentialEndpointResponse(credentials = listOf(mockCredential))
        val result = verifyCredentialEndpointResponse(response, baseContext, mockVerifiers)

        assertEquals(mockErrors, result)
    }

    @Test
    fun `aggregates errors from multiple credentials`() {
        val errorsList = listOf(
            listOf(
                VerificationError(
                    code = ErrorCode.INVALID_KID,
                    message = "Invalid kid",
                    path = listOf("credentials", 0, "payload", "kid")
                )
            ),
            listOf(
                VerificationError(
                    code = ErrorCode.MISSING_CREDENTIAL_STATUS,
                    message = "Missing credentialStatus",
                    path = listOf("credentials", 1, "payload", "vc", "credentialStatus")
                )
            )
        )

        var credentialIndex = 0
        val mockVerifiers = CredentialVerifiers(
            algIsSupported = { _, _ -> null },
            credentialSchema = { _, _ -> null },
            credentialStatus = { _, _ ->
                if (credentialIndex == 1) errorsList[1].first() else null
            },
            issClaimMatchesEitherMetadataOrCredentialIssuer = { _, _ -> null },
            issClaimMatchesMetadata = { _, _ -> null },
            kidClaimIsVelocityV2 = { _, _ ->
                if (credentialIndex == 0) errorsList[0].first() else null
            },
            subIsDidJwkOrCnf = { _, _ ->
                credentialIndex++ // increment only once per credential
                null
            }
        )

        val response = CredentialEndpointResponse(credentials = listOf(mockCredential, mockCredential))
        val result = verifyCredentialEndpointResponse(response, baseContext, mockVerifiers)

        assertEquals(errorsList.flatten(), result)
    }

    @Test
    fun `returns empty list when credentials are missing`() {
        val response = CredentialEndpointResponse(credentials = null)
        val result = verifyCredentialEndpointResponse(response, baseContext, erroringVerifiers())

        assertEquals(emptyList(), result)
    }

    @Test
    fun `returns empty list when credentials is an empty array`() {
        val response = CredentialEndpointResponse(credentials = emptyList())
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
            subIsDidJwkOrCnf = { _, _ -> error("Should not be called") }
        )
}
