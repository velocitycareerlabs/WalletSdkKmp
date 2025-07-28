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

class CredentialStatusVerifierTest {

    private val baseContext = VerificationContext(
        path = emptyList(),
        credentialIssuerMetadata = CredentialIssuerMetadata(
            iss = "did:issuer:example",
            credentialIssuer = "https://issuer.example.com"
        )
    )

    private fun buildCredential(statusValue: Any? = mapOf("id" to "https://status.example.com")): W3CCredentialJwtV1 {
        return W3CCredentialJwtV1(
            header = JwtHeader(alg = "ES256"),
            payload = JwtPayload(
                iss = "did:example",
                vc = VcClaims(
                    credentialStatus = statusValue
                )
            )
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
        val credential = W3CCredentialJwtV1(
            header = JwtHeader(alg = "ES256"),
            payload = JwtPayload(
                iss = "did:example",
                vc = VcClaims(credentialStatus = null)
            )
        )

        val result = credentialStatusVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.MISSING_CREDENTIAL_STATUS, result.code)
        assertEquals(listOf("payload", "vc", "credentialStatus"), result.path)
    }

    @Test
    fun `should fail when vc is missing all fields`() {
        val credential = W3CCredentialJwtV1(
            header = JwtHeader(alg = "ES256"),
            payload = JwtPayload(
                iss = "did:example",
                vc = VcClaims()
            )
        )

        val result = credentialStatusVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.MISSING_CREDENTIAL_STATUS, result.code)
        assertEquals(listOf("payload", "vc", "credentialStatus"), result.path)
    }

    @Test
    fun `should include full path when nested in a context`() {
        val credential = W3CCredentialJwtV1(
            header = JwtHeader(alg = "ES256"),
            payload = JwtPayload(
                iss = "did:example",
                vc = VcClaims()
            )
        )
        val nestedContext = baseContext.copy(
            path = listOf("credentials", 0)
        )

        val result = credentialStatusVerifier(credential, nestedContext)

        assertNotNull(result)
        assertEquals(
            listOf("credentials", 0, "payload", "vc", "credentialStatus"),
            result.path
        )
    }
}
