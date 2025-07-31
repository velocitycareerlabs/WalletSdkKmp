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
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class SubIsDidJwkOrCnfVerifierTest {
    private val baseContext =
        VerificationContext(
            path = emptyList(),
            credentialIssuerMetadata =
                CredentialIssuerMetadata(
                    iss = "did:issuer:example",
                    credentialIssuer = "https://issuer.example.com",
                ),
        )

    private fun makeCredential(
        sub: String? = null,
        cnf: JsonElement? = null,
    ): W3CCredentialJwtV1 {
        val payloadMap =
            buildMap<String, JsonElement> {
                put("iss", JsonPrimitive("did:example"))
                put("vc", JsonObject(emptyMap()))
                if (sub != null) put("sub", JsonPrimitive(sub))
                if (cnf != null) put("cnf", cnf)
            }

        return W3CCredentialJwtV1(
            header = JwtHeader(mapOf("alg" to JsonPrimitive("ES256"))),
            payload = JwtPayload(payloadMap),
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
        val cnf =
            buildJsonObject {
                put(
                    "jwk",
                    buildJsonObject {
                        put("kty", JsonPrimitive("EC"))
                        put("crv", JsonPrimitive("secp256k1"))
                        put("x", JsonPrimitive("fakeX"))
                        put("y", JsonPrimitive("fakeY"))
                    },
                )
            }
        val credential = makeCredential(sub = "some-other-sub", cnf = cnf)

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
