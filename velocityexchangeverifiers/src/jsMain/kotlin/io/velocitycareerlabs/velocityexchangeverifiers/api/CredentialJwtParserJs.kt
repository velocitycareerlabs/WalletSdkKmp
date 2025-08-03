/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package io.velocitycareerlabs.velocityexchangeverifiers.api

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1Js
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.toJs
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import io.velocitycareerlabs.velocityexchangeverifiers.api.CredentialJwtParser as InternalParser

/**
 * JavaScript/TypeScript-friendly wrapper for [CredentialJwtParser].
 *
 * Exposes all JSON parsing capabilities as JS-interop functions for:
 * - Individual JWTs
 * - Endpoint responses
 */
@JsExport
object CredentialJwtParserJs {
    /** Parses a credential JWT into a strongly typed internal object. */
    @JsName("parseCredentialJwt")
    fun parseCredentialJwt(json: String): W3CCredentialJwtV1 = InternalParser.parseCredentialJwt(json)

    /** Parses a credential JWT into a JS-friendly object. */
    @JsName("parseCredentialJwtAsJs")
    fun parseCredentialJwtAsJs(json: String): W3CCredentialJwtV1Js = InternalParser.parseCredentialJwt(json).toJs()

    /** Parses a credential JWT or returns null on failure. */
    @JsName("parseCredentialJwtOrNull")
    fun parseCredentialJwtOrNull(json: String): W3CCredentialJwtV1? = InternalParser.parseCredentialJwtOrNull(json)

    /** Parses a credential endpoint response into a structured object. */
    @JsName("parseCredentialEndpointResponse")
    fun parseCredentialEndpointResponse(json: String): CredentialEndpointResponse = InternalParser.parseCredentialEndpointResponse(json)

    /** Parses a credential endpoint response and returns its credentials as JS-friendly objects. */
    @JsName("parseCredentialEndpointResponseAsJs")
    fun parseCredentialEndpointResponseAsJs(json: String): Array<W3CCredentialJwtV1Js>? =
        InternalParser
            .parseCredentialEndpointResponse(json)
            .credentials
            ?.map { it.toJs() }
            ?.toTypedArray()

    /** Parses a credential endpoint response or returns null on failure. */
    @JsName("parseCredentialEndpointResponseOrNull")
    fun parseCredentialEndpointResponseOrNull(json: String): CredentialEndpointResponse? =
        InternalParser.parseCredentialEndpointResponseOrNull(json)
}
