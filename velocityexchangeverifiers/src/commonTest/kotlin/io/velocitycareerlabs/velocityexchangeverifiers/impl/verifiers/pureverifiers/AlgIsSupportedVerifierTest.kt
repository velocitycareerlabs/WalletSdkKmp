/**
 * Created by Michael Avoyan on 25/07/2025.
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
import kotlin.test.assertTrue

internal class AlgIsSupportedVerifierTest {
    private val baseContext =
        VerificationContext(
            path = emptyList(),
            credentialIssuerMetadata =
                CredentialIssuerMetadata(
                    iss = "did:issuer:example",
                    credentialIssuer = "https://issuer.example.com",
                ),
        )

    private fun makeCredential(alg: String?): W3CCredentialJwtV1 {
        val headerClaims: Map<String, JsonElement> =
            if (alg != null) {
                mapOf("alg" to JsonPrimitive(alg))
            } else {
                emptyMap()
            }

        val payloadClaims: Map<String, JsonElement> =
            mapOf(
                "iss" to JsonPrimitive("did:example"),
                "vc" to JsonObject(emptyMap()),
            )

        return W3CCredentialJwtV1(
            header = JwtHeader(headerClaims),
            payload = JwtPayload(payloadClaims),
        )
    }

    @Test
    fun `should pass for supported alg values`() {
        listOf("ES256", "ES256K", "RS256").forEach { alg ->
            val credential = makeCredential(alg)

            val result = algIsSupportedVerifier(credential, baseContext)

            assertNull(result, "Expected null for supported alg $alg")
        }
    }

    @Test
    fun `should fail for unsupported alg values`() {
        listOf("HS256", "none", "", "EdDSA").forEach { alg ->
            val credential = makeCredential(alg)

            val result = algIsSupportedVerifier(credential, baseContext)

            assertNotNull(result)
            assertEquals(ErrorCode.INVALID_ALG, result.code)
            assertTrue(result.message.contains(alg), "Expected message to contain $alg")
            assertEquals(listOf("header", "alg"), result.path)
        }
    }

    @Test
    fun `should fail when alg is missing`() {
        val credential = makeCredential(null)

        val result = algIsSupportedVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.INVALID_ALG, result.code)
        assertTrue(result.message.contains("null"), "Expected message to mention 'null'")
        assertEquals(listOf("header", "alg"), result.path)
    }
}
