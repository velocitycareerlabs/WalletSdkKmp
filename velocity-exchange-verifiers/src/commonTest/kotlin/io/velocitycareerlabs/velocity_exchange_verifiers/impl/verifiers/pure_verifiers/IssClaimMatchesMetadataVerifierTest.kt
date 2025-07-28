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

class IssClaimMatchesMetadataVerifierTest {

    private val baseContext = VerificationContext(
        path = emptyList(),
        credentialIssuerMetadata = CredentialIssuerMetadata(
            iss = "did:velocity:issuer123",
            credentialIssuer = "https://issuer.velocitycareerlabs.com"
        )
    )

    private fun makeCredential(iss: String): W3CCredentialJwtV1 {
        return W3CCredentialJwtV1(
            header = JwtHeader(alg = "ES256"),
            payload = JwtPayload(
                iss = iss,
                vc = VcClaims()
            )
        )
    }

    @Test
    fun `should pass when iss matches credential_issuer_metadata iss exactly`() {
        val credential = makeCredential("did:velocity:issuer123")

        val result = issClaimMatchesMetadataVerifier(credential, baseContext)

        assertNull(result)
    }

    @Test
    fun `should fail when iss matches credential_issuer but not iss`() {
        val credential = makeCredential("https://issuer.velocitycareerlabs.com")

        val result = issClaimMatchesMetadataVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.UNEXPECTED_CREDENTIAL_PAYLOAD_ISS, result.code)
        assertEquals(listOf("payload", "iss"), result.path)
        assertTrue(result.message.contains("https://issuer.velocitycareerlabs.com"))
    }

    @Test
    fun `should fail when iss does not match anything`() {
        val credential = makeCredential("did:wrong:issuer")

        val result = issClaimMatchesMetadataVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.UNEXPECTED_CREDENTIAL_PAYLOAD_ISS, result.code)
        assertEquals(listOf("payload", "iss"), result.path)
        assertTrue(result.message.contains("did:wrong:issuer"))
    }

    @Test
    fun `should fail when iss is missing`() {
        val credential = makeCredential("")

        val result = issClaimMatchesMetadataVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.UNEXPECTED_CREDENTIAL_PAYLOAD_ISS, result.code)
        assertEquals(listOf("payload", "iss"), result.path)
        assertTrue(result.message.contains(""))
    }
}
