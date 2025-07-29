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
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VcClaims
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
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
        statusValue: JsonElement? =
            buildJsonObject {
                put("id", JsonPrimitive("https://status.example.com"))
            },
    ): W3CCredentialJwtV1 =
        W3CCredentialJwtV1(
            header = JwtHeader(alg = "ES256"),
            payload =
                JwtPayload(
                    iss = "did:example",
                    vc =
                        VcClaims(
                            credentialStatus = statusValue,
                        ),
                ),
        )

    @Test
    fun `should pass when credentialStatus is present`() {
        val credential = buildCredential()

        val result = credentialStatusVerifier(credential, baseContext)

        assertNull(result)
    }

    @Test
    fun `should fail when credentialStatus is undefined`() {
        val credential =
            W3CCredentialJwtV1(
                header = JwtHeader(alg = "ES256"),
                payload =
                    JwtPayload(
                        iss = "did:example",
                        vc = VcClaims(credentialStatus = null),
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
                header = JwtHeader(alg = "ES256"),
                payload =
                    JwtPayload(
                        iss = "did:example",
                        vc = VcClaims(),
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
                header = JwtHeader(alg = "ES256"),
                payload =
                    JwtPayload(
                        iss = "did:example",
                        vc = VcClaims(),
                    ),
            )
        val nestedContext =
            baseContext.copy(
                path = listOf("credentials", 0),
            )

        val result = credentialStatusVerifier(credential, nestedContext)

        assertNotNull(result)
        assertEquals(
            listOf("credentials", 0, "payload", "vc", "credentialStatus"),
            result.path,
        )
    }
}
