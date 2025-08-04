/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1Js
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.toW3CCredentialJwtV1Js
import kotlin.js.JsName

/**
 * JavaScript/TypeScript-friendly wrapper for [CredentialJwtParser].
 *
 * This object provides safe parsing of:
 * - Individual VC JWTs into JS-friendly models
 * - CredentialEndpointResponse into an array of parsed credentials
 *
 * Only exports JS-compatible types to avoid Kotlin-native leaks.
 */
object CredentialJwtParserJs {
    /**
     * Parses a credential JWT string into a JS-friendly [W3CCredentialJwtV1Js] object.
     * Returns null if parsing fails.
     */
    @JsName("parseCredentialJwtAsJs")
    fun parseCredentialJwtAsJs(json: String): W3CCredentialJwtV1Js? =
        CredentialJwtParser.parseCredentialJwtOrNull(json)?.toW3CCredentialJwtV1Js()

    /**
     * Parses a credential endpoint response JSON string into an array of
     * JS-friendly [W3CCredentialJwtV1Js] objects.
     * Returns null if parsing fails or contains no credentials.
     */
    @JsName("parseCredentialEndpointResponseAsJs")
    fun parseCredentialEndpointResponseAsJs(json: String): Array<W3CCredentialJwtV1Js>? {
        val response = CredentialJwtParser.parseCredentialEndpointResponseOrNull(json)
        val credentials = response?.credentials ?: return null
        return credentials.map { it.toW3CCredentialJwtV1Js() }.toTypedArray()
    }
}
