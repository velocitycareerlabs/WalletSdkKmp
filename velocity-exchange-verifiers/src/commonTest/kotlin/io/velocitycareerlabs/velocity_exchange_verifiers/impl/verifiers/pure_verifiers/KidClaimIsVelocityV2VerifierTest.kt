/**
 * Created by Michael Avoyan on 28/07/2025.
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
import kotlin.test.assertTrue

class SubIsDidJwkOrCnfVerifierTest {

    private val baseContext = VerificationContext(
        path = emptyList(),
        credentialIssuerMetadata = CredentialIssuerMetadata(
            iss = "did:issuer:example",
            credentialIssuer = "https://issuer.example.com"
        )
    )

    private fun makeCredential(sub: String? = null, cnf: Any? = null): W3CCredentialJwtV1 {
        return W3CCredentialJwtV1(
            header = JwtHeader(alg = "ES256"),
            payload = JwtPayload(
                iss = "did:example",
                sub = sub,
                cnf = cnf,
                vc = VcClaims()
            )
        )
    }

    @Test
    fun `should pass if sub is did colon jwk`() {
        val credential = makeCredential(sub = "did:jwk")

        val result = subIsDidJwkOrCnfVerifier(credential, baseContext)

        assertNull(result)
    }

    @Test
    fun `should pass if cnf is defined and sub is something else`() {
        val credential = makeCredential(sub = "some-other-sub", cnf = mapOf("jwk" to emptyMap<String, Any>()))

        val result = subIsDidJwkOrCnfVerifier(credential, baseContext)

        assertNull(result)
    }

    @Test
    fun `should fail if sub is not did colon jwk and cnf is missing`() {
        val credential = makeCredential(sub = "some-other-sub", cnf = null)

        val result = subIsDidJwkOrCnfVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.SUB_OR_CNF_MISSING, result.code)
        assertEquals(listOf("payload"), result.path)
        assertTrue(result.message.contains("some-other-sub"))
    }

    @Test
    fun `should fail if sub and cnf are both missing`() {
        val credential = makeCredential(sub = null, cnf = null)

        val result = subIsDidJwkOrCnfVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.SUB_OR_CNF_MISSING, result.code)
        assertEquals(listOf("payload"), result.path)
        assertTrue(result.message.contains("null") || result.message.contains("undefined"))
    }
}
