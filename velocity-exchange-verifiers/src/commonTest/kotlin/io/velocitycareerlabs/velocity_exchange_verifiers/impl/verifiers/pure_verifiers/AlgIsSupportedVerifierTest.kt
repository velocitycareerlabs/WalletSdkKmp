/**
 * Created by Michael Avoyan on 25/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers

import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.CredentialIssuerMetadata
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.ErrorCode
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.JwtHeader
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.JwtPayload
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VcClaims
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.W3CCredentialJwtV1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AlgIsSupportedVerifierTest {

    private val baseContext = VerificationContext(
        path = emptyList(),
        credentialIssuerMetadata = CredentialIssuerMetadata(
            iss = "did:issuer:example",
            credentialIssuer = "https://issuer.example.com"
        )
    )

    private fun makeCredential(alg: String?): W3CCredentialJwtV1 {
        return W3CCredentialJwtV1(
            header = if (alg != null) JwtHeader(alg = alg) else null,
            payload = JwtPayload(
                iss = "did:example",
                vc = VcClaims(),
            )
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
            assertEquals(result.message.contains(alg), true)
            assertEquals(listOf("header", "alg"), result.path)
        }
    }

    @Test
    fun `should fail when alg is missing`() {
        val credential = makeCredential(null)

        val result = algIsSupportedVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.INVALID_ALG, result.code)
        assertEquals(result.message.contains("null"), true)
        assertEquals(listOf("header", "alg"), result.path)
    }
}
