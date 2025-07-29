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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class IssClaimMatchesEitherMetadataOrCredentialIssuerVerifierTest {
    private val context =
        VerificationContext(
            path = emptyList(),
            credentialIssuerMetadata =
                CredentialIssuerMetadata(
                    iss = "did:example:issuer",
                    credentialIssuer = "https://issuer.example.com",
                ),
        )

    private fun makeCredential(iss: String): W3CCredentialJwtV1 =
        W3CCredentialJwtV1(
            header = JwtHeader(alg = "ES256"),
            payload =
                JwtPayload(
                    iss = iss,
                    vc = VcClaims(),
                ),
        )

    @Test
    fun `should pass if iss matches metadata iss`() {
        val credential = makeCredential("did:example:issuer")

        val result = issClaimMatchesEitherMetadataOrCredentialIssuerVerifier(credential, context)

        assertNull(result)
    }

    @Test
    fun `should pass if iss matches metadata credentialIssuer`() {
        val credential = makeCredential("https://issuer.example.com")

        val result = issClaimMatchesEitherMetadataOrCredentialIssuerVerifier(credential, context)

        assertNull(result)
    }

    @Test
    fun `should fail if iss matches neither`() {
        val credential = makeCredential("https://other.example.org")

        val result = issClaimMatchesEitherMetadataOrCredentialIssuerVerifier(credential, context)

        assertNotNull(result)
        assertEquals(ErrorCode.UNEXPECTED_CREDENTIAL_PAYLOAD_ISS, result.code)
        assertEquals(listOf("payload", "iss"), result.path)
        assertTrue(result.message.contains("https://other.example.org"))
    }

    @Test
    fun `should fail if iss is empty string`() {
        val credential = makeCredential("")

        val result = issClaimMatchesEitherMetadataOrCredentialIssuerVerifier(credential, context)

        assertNotNull(result)
        assertEquals(ErrorCode.UNEXPECTED_CREDENTIAL_PAYLOAD_ISS, result.code)
        assertEquals(listOf("payload", "iss"), result.path)
        assertTrue(result.message.contains(""))
    }
}
