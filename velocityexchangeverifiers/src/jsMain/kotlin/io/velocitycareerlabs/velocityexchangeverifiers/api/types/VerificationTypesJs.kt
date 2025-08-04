/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("OPT_IN_USAGE", "NON_CONSUMABLE_EXPORTED_IDENTIFIER")
@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import kotlin.js.JsName

/**
 * JS/TS-friendly version of [CredentialIssuerMetadata].
 *
 * Represents issuer metadata used in credential verification. Typically retrieved from
 * `.well-known/openid-credential-issuer` and used to validate the `iss` claim.
 *
 * @property iss The issuer identifier (typically matches the `iss` in credentials)
 * @property credentialIssuer Optional alternative issuer URI (used in OpenID4VCI compatibility)
 */
@JsName("CredentialIssuerMetadataJs")
data class CredentialIssuerMetadataJs(
    val iss: String,
    val credentialIssuer: String? = null,
)

/**
 * JS/TS-friendly context container passed to verifiers.
 *
 * Includes credential issuer metadata and optional path information for tracing validation errors.
 *
 * @property credentialIssuerMetadata Optional issuer metadata
 * @property path Optional JSON path to help locate verification context
 */
@JsName("VerificationContextJs")
data class VerificationContextJs(
    val credentialIssuerMetadata: CredentialIssuerMetadataJs? = null,
    val path: List<String>? = null,
)
