/**
 * Created by Michael Avoyan on 06/08/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.impl

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponseJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialIssuerMetadata
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialIssuerMetadataJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtHeader
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtPayload
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContextJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1Js
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * Internal helpers for converting between internal and JS-friendly data types.
 */

private val json =
    Json {
        encodeDefaults = true
        prettyPrint = false
    }

private val jsonObjectSerializer = JsonObject.serializer()

internal fun CredentialIssuerMetadataJs.toNative(): CredentialIssuerMetadata =
    CredentialIssuerMetadata(
        iss = iss,
        credentialIssuer = credentialIssuer,
    )

internal fun VerificationContextJs.toNative(): VerificationContext =
    VerificationContext(
        credentialIssuerMetadata = credentialIssuerMetadata?.toNative(),
        path = path?.toList(),
    )

internal fun W3CCredentialJwtV1Js.toNative(): W3CCredentialJwtV1 =
    W3CCredentialJwtV1(
        header =
            JwtHeader(
                claims =
                    buildMap {
                        alg?.let { put("alg", json.parseToJsonElement("\"$it\"")) }
                        kid?.let { put("kid", json.parseToJsonElement("\"$it\"")) }
                        typ?.let { put("typ", json.parseToJsonElement("\"$it\"")) }
                    },
            ),
        payload =
            JwtPayload(
                claims =
                    buildMap {
                        iss?.let { put("iss", json.parseToJsonElement("\"$it\"")) }
                        sub?.let { put("sub", json.parseToJsonElement("\"$it\"")) }
                        // vc is constructed below as an object if either schema/status present
                        val vcClaims =
                            buildMap<String, JsonObject> {
                                credentialSchemaJson?.let {
                                    put("credentialSchema", json.decodeFromString(jsonObjectSerializer, it))
                                }
                                credentialStatusJson?.let {
                                    put("credentialStatus", json.decodeFromString(jsonObjectSerializer, it))
                                }
                            }
                        if (vcClaims.isNotEmpty()) {
                            put("vc", JsonObject(vcClaims))
                        }
                    },
            ),
    )

internal fun CredentialEndpointResponseJs.toNative(): CredentialEndpointResponse =
    CredentialEndpointResponse(
        claims =
            mapOf(
                "credentials" to
                    json.encodeToJsonElement(
                        ListSerializer(W3CCredentialJwtV1.serializer()),
                        credentials.map { it.toNative() },
                    ),
            ),
    )
