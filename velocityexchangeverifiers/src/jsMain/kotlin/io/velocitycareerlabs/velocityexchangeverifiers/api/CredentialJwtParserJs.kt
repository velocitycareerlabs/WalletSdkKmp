/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("OPT_IN_USAGE", "NON_CONSUMABLE_EXPORTED_IDENTIFIER")
@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package io.velocitycareerlabs.velocityexchangeverifiers.api

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1Js
import io.velocitycareerlabs.velocityexchangeverifiers.impl.toJs
import kotlin.js.JsName

/**
 * JavaScript/TypeScript-friendly wrapper around [CredentialJwtParser].
 *
 * This object exposes functions to:
 * - Parse individual JWT credentials into interoperable [W3CCredentialJwtV1Js] objects.
 * - Parse full Credential Endpoint Response payloads into arrays of credentials.
 *
 * These functions return JS-friendly structures and avoid leaking internal Kotlin-native models.
 */
@JsName("CredentialJwtParserJs")
object CredentialJwtParserJs {
    /**
     * Parses a Verifiable Credential JWT string into a [W3CCredentialJwtV1Js].
     *
     * @param json JSON string representing a single VC JWT.
     * @return JS-compatible credential, or `null` if parsing fails.
     */
    @JsName("parseCredentialJwtAsJs")
    fun parseCredentialJwtAsJs(json: String): W3CCredentialJwtV1Js? = CredentialJwtParser.parseCredentialJwtOrNull(json)?.toJs()

    /**
     * Parses a Credential Endpoint Response JSON string into an array of [W3CCredentialJwtV1Js].
     *
     * @param json JSON string from the issuer endpoint containing credential claims.
     * @return JS-compatible array of credentials, or `null` if parsing fails or is empty.
     */
    @JsName("parseCredentialEndpointResponseAsJs")
    fun parseCredentialEndpointResponseAsJs(json: String): Array<W3CCredentialJwtV1Js>? =
        CredentialJwtParser
            .parseCredentialEndpointResponseOrNull(json)
            ?.credentials
            ?.map { it.toJs() }
            ?.toTypedArray()
}
