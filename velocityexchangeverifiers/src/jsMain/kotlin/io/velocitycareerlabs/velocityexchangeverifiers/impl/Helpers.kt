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
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContextJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationError
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationErrorJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1Js
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

internal fun CredentialIssuerMetadata.toJs(): CredentialIssuerMetadataJs =
    CredentialIssuerMetadataJs(
        iss = iss,
        credentialIssuer = credentialIssuer,
    )

internal fun CredentialIssuerMetadataJs.toInternal(): CredentialIssuerMetadata =
    CredentialIssuerMetadata(
        iss = iss,
        credentialIssuer = credentialIssuer,
    )

internal fun VerificationContext.toJs(): VerificationContextJs =
    VerificationContextJs(
        credentialIssuerMetadata = credentialIssuerMetadata?.toJs(),
        path = path?.map { it.toString() }?.toList(),
    )

internal fun VerificationContextJs.toInternal(): VerificationContext =
    VerificationContext(
        credentialIssuerMetadata = credentialIssuerMetadata?.toInternal(),
        path = path?.toList(),
    )

internal fun W3CCredentialJwtV1.toJs(): W3CCredentialJwtV1Js =
    W3CCredentialJwtV1Js(
        alg = header?.alg,
        kid = header?.kid,
        typ = header?.typ,
        iss = payload.iss,
        sub = payload.sub,
        credentialSchemaJson =
            payload.vc?.credentialSchema?.let {
                json.encodeToString(jsonObjectSerializer, it)
            },
        credentialStatusJson =
            payload.vc?.credentialStatus?.let {
                json.encodeToString(jsonObjectSerializer, it)
            },
    )

internal fun CredentialEndpointResponse.toJs(): CredentialEndpointResponseJs =
    CredentialEndpointResponseJs(
        credentials = credentials?.map { it.toJs() }?.toList() ?: emptyList(),
    )

internal fun VerificationError.toVerificationErrorJs(): VerificationErrorJs =
    VerificationErrorJs(
        code = code.code,
        message = message,
        path = path?.map { it.toString() }?.toList(),
    )
