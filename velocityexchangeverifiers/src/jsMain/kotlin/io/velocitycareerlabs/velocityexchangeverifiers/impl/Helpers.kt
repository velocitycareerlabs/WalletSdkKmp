/**
 * Created by Michael Avoyan on 04/08/2025.
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
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationError
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationErrorJs
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

// ------------ Kotlin -> JS (toJs) ------------

internal fun CredentialIssuerMetadata.toJs(): CredentialIssuerMetadataJs =
    CredentialIssuerMetadataJs(
        iss = iss,
        credentialIssuer = credentialIssuer,
    )

internal fun VerificationContext.toJs(): VerificationContextJs =
    VerificationContextJs(
        credentialIssuerMetadata = credentialIssuerMetadata?.toJs(),
        path = path?.map { it.toString() }?.toTypedArray(),
    )

internal fun W3CCredentialJwtV1.toJs(): W3CCredentialJwtV1Js =
    W3CCredentialJwtV1Js(
        alg = header?.alg,
        kid = header?.kid,
        typ = header?.typ,
        iss = payload.iss,
        sub = payload.sub,
        credentialSchemaJson =
            payload.vc?.credentialSchema?.let { credentialSchema ->
                json.encodeToString(jsonObjectSerializer, credentialSchema)
            },
        credentialStatusJson =
            payload.vc?.credentialStatus?.let { credentialStatus ->
                json.encodeToString(jsonObjectSerializer, credentialStatus)
            },
    )

internal fun CredentialEndpointResponse.toJs(): CredentialEndpointResponseJs =
    CredentialEndpointResponseJs(
        credentials = credentials?.map { it.toJs() }?.toTypedArray() ?: emptyArray(),
    )

internal fun VerificationError.toJs(): VerificationErrorJs =
    VerificationErrorJs(
        code = code.code,
        message = message,
        path = path?.map { it.toString() }?.toTypedArray(),
    )

// ------------ JS -> Kotlin (toInternal) ------------

internal fun CredentialIssuerMetadataJs.toInternal(): CredentialIssuerMetadata =
    CredentialIssuerMetadata(
        iss = iss,
        credentialIssuer = credentialIssuer,
    )

internal fun VerificationContextJs.toInternal(): VerificationContext =
    VerificationContext(
        credentialIssuerMetadata = credentialIssuerMetadata?.toInternal(),
        path = path?.toList(),
    )

internal fun W3CCredentialJwtV1Js.toInternal(): W3CCredentialJwtV1 =
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

internal fun CredentialEndpointResponseJs.toInternal(): CredentialEndpointResponse =
    CredentialEndpointResponse(
        claims =
            mapOf(
                "credentials" to
                    json.encodeToJsonElement(
                        ListSerializer(W3CCredentialJwtV1.serializer()),
                        credentials.map { it.toInternal() },
                    ),
            ),
    )
