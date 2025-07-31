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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class KidClaimIsVelocityV2VerifierTest {
    private val baseContext =
        VerificationContext(
            path = emptyList(),
            credentialIssuerMetadata =
                CredentialIssuerMetadata(
                    iss = "did:issuer:example",
                    credentialIssuer = "https://issuer.example.com",
                ),
        )

    private fun makeCredential(kid: String? = null): W3CCredentialJwtV1 {
        val headerMap =
            buildMap {
                put("alg", JsonPrimitive("ES256"))
                if (kid != null) put("kid", JsonPrimitive(kid))
            }

        val payloadMap =
            mapOf(
                "iss" to JsonPrimitive("did:example"),
                "vc" to JsonObject(emptyMap()),
            )

        return W3CCredentialJwtV1(
            header = JwtHeader(headerMap),
            payload = JwtPayload(claims = payloadMap),
        )
    }

    @Test
    fun `should pass when kid starts with did colon velocity colon v2`() {
        val credential = makeCredential("did:velocity:v2:1234")
        val result = kidClaimIsVelocityV2Verifier(credential, baseContext)

        assertNull(result)
    }

    @Test
    fun `should fail when kid is invalid variants`() {
        val invalidKids =
            listOf(
                "did:velocity:v1:1234",
                "did:web:velocity.com",
                "something-else",
                "",
            )

        for (invalidKid in invalidKids) {
            val credential = makeCredential(invalidKid)
            val result = kidClaimIsVelocityV2Verifier(credential, baseContext)

            assertNotNull(result, "Expected error for kid: '$invalidKid'")
            assertEquals(ErrorCode.INVALID_KID, result.code)
            assertEquals(listOf("header", "kid"), result.path)
            assertTrue(result.message.contains(invalidKid))
        }
    }

    @Test
    fun `should fail when kid is null`() {
        val credential = makeCredential(null)
        val result = kidClaimIsVelocityV2Verifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.INVALID_KID, result.code)
        assertEquals(listOf("header", "kid"), result.path)
        assertTrue(result.message.contains("null") || result.message.contains("undefined"))
    }
}
