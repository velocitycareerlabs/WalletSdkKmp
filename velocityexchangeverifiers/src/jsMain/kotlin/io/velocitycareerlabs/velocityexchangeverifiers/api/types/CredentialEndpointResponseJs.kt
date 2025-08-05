/**
 * Created by Michael Avoyan on 05/08/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("NON_CONSUMABLE_EXPORTED_IDENTIFIER", "OPT_IN_USAGE")
@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

/**
 * JS/TS-friendly version of a Credential Endpoint Response.
 *
 * Wraps an array of parsed credentials returned from an issuer endpoint.
 *
 * @property credentials Array of parsed VC JWTs in JS-friendly format.
 */
@JsName("CredentialEndpointResponseJs")
data class CredentialEndpointResponseJs(
    val credentials: Array<W3CCredentialJwtV1Js>,
)
