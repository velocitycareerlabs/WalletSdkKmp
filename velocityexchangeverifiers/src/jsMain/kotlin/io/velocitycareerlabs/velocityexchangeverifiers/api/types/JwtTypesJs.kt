/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

private val json =
    Json {
        encodeDefaults = true
        prettyPrint = false
    }
private val jsonObjectSerializer = JsonObject.serializer()

/**
 * JavaScript/TypeScript-friendly version of [W3CCredentialJwtV1].
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("W3CCredentialJwtV1Js")
data class W3CCredentialJwtV1Js(
    val alg: String?,
    val kid: String?,
    val typ: String?,
    val iss: String?,
    val sub: String?,
    val credentialSchemaJson: String?,
    val credentialStatusJson: String?,
)

internal fun W3CCredentialJwtV1.toW3CCredentialJwtV1Js(): W3CCredentialJwtV1Js =
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

/**
 * JS/TS-friendly bulk credential conversion. Safe for export.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("mapCredentialsToJs")
fun mapCredentialsToJs(credentials: Array<W3CCredentialJwtV1>): Array<W3CCredentialJwtV1Js> =
    credentials.map { it.toW3CCredentialJwtV1Js() }.toTypedArray()
