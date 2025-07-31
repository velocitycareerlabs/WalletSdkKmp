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
import kotlin.test.assertTrue

internal class CredentialSchemaVerifierTest {
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
        credentialSchema: JsonElement? = null,
        credentialStatus: JsonElement? = null,
    ): W3CCredentialJwtV1 {
        val vcClaims =
            buildMap {
                credentialSchema?.let { put("credentialSchema", it) }
                credentialStatus?.let { put("credentialStatus", it) }
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
    fun `should pass when credentialSchema is present`() {
        val schemaJson =
            JsonObject(
                mapOf(
                    "id" to JsonPrimitive("https://schema.org"),
                    "type" to JsonPrimitive("JsonSchema"),
                ),
            )

        val credential = buildCredential(credentialSchema = schemaJson)

        val result = credentialSchemaVerifier(credential, baseContext)

        assertNull(result)
    }

    @Test
    fun `should fail when credentialSchema is missing`() {
        val credential = buildCredential(credentialSchema = null)

        val result = credentialSchemaVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.MISSING_CREDENTIAL_SCHEMA, result.code)
        assertEquals(listOf("payload", "vc", "credentialSchema"), result.path)
        assertTrue(result.message.contains("Expected credentialSchema"))
    }

    @Test
    fun `should fail when vc is empty`() {
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

        val result = credentialSchemaVerifier(credential, baseContext)

        assertNotNull(result)
        assertEquals(ErrorCode.MISSING_CREDENTIAL_SCHEMA, result.code)
        assertEquals(listOf("payload", "vc", "credentialSchema"), result.path)
    }
}
