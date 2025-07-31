/**
 * Created by Michael Avoyan on 30/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CredentialJwtParserTest {
    private val validCredentialJwt =
        """
        {
          "header": {
            "alg": "ES256K",
            "kid": "did:velocity:v2:12345"
          },
          "payload": {
            "iss": "did:issuer:velocity",
            "sub": "did:jwk:subject",
            "vc": {
              "credentialSchema": { "id": "https://schema.org", "type": "JsonSchemaValidator2018" },
              "credentialStatus": { "id": "https://revocation.example", "type": "StatusList2021" }
            },
            "cnf": {
              "jwk": {
                "kty": "EC",
                "crv": "secp256k1",
                "x": "xval",
                "y": "yval"
              }
            }
          }
        }
        """.trimIndent()

    private val validEndpointResponse =
        """
        {
          "credentials": [
            {
              "header": { "alg": "ES256K", "kid": "did:velocity:v2:12345" },
              "payload": {
                "iss": "did:issuer:velocity",
                "sub": "did:jwk:subject",
                "vc": {
                  "credentialSchema": { "id": "https://schema.org", "type": "JsonSchemaValidator2018" },
                  "credentialStatus": { "id": "https://revocation.example", "type": "StatusList2021" }
                }
              }
            }
          ]
        }
        """.trimIndent()

    private val invalidJson = """{ "header": 123 }""" // deliberately malformed

    // === parseCredentialJwt ===

    @Test
    fun test_parseCredentialJwt_success() {
        val result = CredentialJwtParser.parseCredentialJwt(validCredentialJwt)

        assertEquals("ES256K", result.header?.alg)
        assertEquals("did:issuer:velocity", result.payload.iss)
        assertNotNull(result.payload.vc?.credentialSchema)
        assertNotNull(result.payload.cnf?.jwk)
    }

    @Test
    fun test_parseCredentialJwtOrNull_success() {
        val result = CredentialJwtParser.parseCredentialJwtOrNull(validCredentialJwt)

        assertNotNull(result)
        assertEquals("did:jwk:subject", result?.payload?.sub)
    }

    @Test
    fun test_parseCredentialJwtOrNull_failure() {
        val result = CredentialJwtParser.parseCredentialJwtOrNull(invalidJson)

        assertNull(result)
    }

    @Test
    fun test_parseCredentialJwt_failure_throws() {
        assertFailsWith<Exception> {
            CredentialJwtParser.parseCredentialJwt(invalidJson)
        }
    }

    // === parseCredentialEndpointResponse ===

    @Test
    fun test_parseCredentialEndpointResponse_success() {
        val response = CredentialJwtParser.parseCredentialEndpointResponse(validEndpointResponse)

        assertEquals(1, response.credentials?.size)
        assertEquals(
            "ES256K",
            response.credentials
                ?.firstOrNull()
                ?.header
                ?.alg,
        )
    }

    @Test
    fun test_parseCredentialEndpointResponseOrNull_success() {
        val result = CredentialJwtParser.parseCredentialEndpointResponseOrNull(validEndpointResponse)

        assertNotNull(result)
        assertEquals(
            "did:jwk:subject",
            result
                .credentials
                ?.firstOrNull()
                ?.payload
                ?.sub,
        )
    }

    @Test
    fun test_parseCredentialEndpointResponseOrNull_failure() {
        val result = CredentialJwtParser.parseCredentialEndpointResponseOrNull("not a json")

        assertNull(result)
    }

    @Test
    fun test_parseCredentialEndpointResponse_failure_throws() {
        assertFailsWith<Exception> {
            CredentialJwtParser.parseCredentialEndpointResponse("not valid")
        }
    }
}
