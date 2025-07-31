/**
 * Created by Michael Avoyan on 28/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialIssuerMetadata
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.ErrorCode
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtHeader
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtPayload
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class CredentialStatusVerifierTest {
    private val baseContext =
        VerificationContext(
            path = emptyList(),
            credentialIssuerMetadata =
                CredentialIssuerMetadata(
                    iss = "did:issuer:example",
                    credentialIssuer = "https://issuer.example.com",
                ),
        )

    private fun buildCredential(
        statusValue: JsonElement? = JsonObject(mapOf("id" to JsonPrimitive("https://status.example.com"))),
    ): W3CCredentialJwtV1 {
        val vcClaims =
            buildMap {
                statusValue?.let { put("credentialStatus", it) }
            }

        return W3CCredentialJwtV1(
            header = JwtHeader(mapOf("alg" to JsonPrimitive("ES256"))),
            payload =
                JwtPayload(
                    claims =
                        mapOf(
                            "iss" to JsonPrimitive("did:example"),
                            "vc" to JsonObject(vcClaims),
                        ),
                ),
        )
    }

    @Test
    fun `should pass when credentialStatus is present`() {
        val credential = buildCredential()

        val result = credentialStatusVerifier(credential, baseContext)

        assertNull(result)
    }

    @Test
    fun `should fail when credentialStatus is undefined`() {
        val vcClaims = JsonObject(emptyMap())

        val credential =
            W3CCredentialJwtV1(
                header = JwtHeader(mapOf("alg" to JsonPrimitive("ES256"))),
                payload =
                    JwtPayload(
                        claims =
                            mapOf(
                                "iss" to JsonPrimitive("did:example"),
                                "vc" to vcClaims,
                            ),
                    ),
            )

        val result = credentialStatusVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.MISSING_CREDENTIAL_STATUS, result.code)
        assertEquals(listOf("payload", "vc", "credentialStatus"), result.path)
    }

    @Test
    fun `should fail when vc is missing all fields`() {
        val credential =
            W3CCredentialJwtV1(
                header = JwtHeader(mapOf("alg" to JsonPrimitive("ES256"))),
                payload =
                    JwtPayload(
                        claims =
                            mapOf(
                                "iss" to JsonPrimitive("did:example"),
                                "vc" to JsonObject(emptyMap()),
                            ),
                    ),
            )

        val result = credentialStatusVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.MISSING_CREDENTIAL_STATUS, result.code)
        assertEquals(listOf("payload", "vc", "credentialStatus"), result.path)
    }

    @Test
    fun `should include full path when nested in a context`() {
        val credential =
            W3CCredentialJwtV1(
                header = JwtHeader(mapOf("alg" to JsonPrimitive("ES256"))),
                payload =
                    JwtPayload(
                        claims =
                            mapOf(
                                "iss" to JsonPrimitive("did:example"),
                                "vc" to JsonObject(emptyMap()),
                            ),
                    ),
            )

        val nestedContext = baseContext.copy(path = listOf("credentials", 0))

        val result = credentialStatusVerifier(credential, nestedContext)

        assertNotNull(result)
        assertEquals(
            listOf("credentials", 0, "payload", "vc", "credentialStatus"),
            result.path,
        )
    }
}
