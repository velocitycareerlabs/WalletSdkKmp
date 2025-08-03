/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * JavaScript/TypeScript-friendly version of [W3CCredentialJwtV1].
 */
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

/**
 * Converts a [W3CCredentialJwtV1] into a [W3CCredentialJwtV1Js] for JS/TS interop.
 */
@JsExport
@JsName("toW3CCredentialJwtV1Js")
fun W3CCredentialJwtV1.toJs(): W3CCredentialJwtV1Js =
    W3CCredentialJwtV1Js(
        alg = header?.alg,
        kid = header?.kid,
        typ = header?.typ,
        iss = payload.iss,
        sub = payload.sub,
        credentialSchemaJson = payload.vc?.credentialSchema?.toString(),
        credentialStatusJson = payload.vc?.credentialStatus?.toString(),
    )

@JsExport
@JsName("mapCredentialsToJs")
fun mapCredentialsToJs(credentials: Array<W3CCredentialJwtV1>): Array<W3CCredentialJwtV1Js> = credentials.map { it.toJs() }.toTypedArray()
