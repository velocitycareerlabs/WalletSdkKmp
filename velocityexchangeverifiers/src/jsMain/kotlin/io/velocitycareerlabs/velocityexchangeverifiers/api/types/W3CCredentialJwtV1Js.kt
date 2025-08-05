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
 * JS/TS-friendly version of a parsed W3C Credential JWT.
 *
 * This flattened structure combines JOSE header and VC-specific claims
 * and is designed for direct use in JavaScript/TypeScript applications.
 *
 * @property alg Algorithm used in the JOSE header (e.g., ES256, ES256K)
 * @property kid Key ID (typically starts with did:velocity:v2)
 * @property typ Type field (usually "JWT")
 * @property iss Issuer of the credential
 * @property sub Subject of the credential
 * @property credentialSchemaJson Raw JSON string of credentialSchema
 * @property credentialStatusJson Raw JSON string of credentialStatus
 */
@JsName("W3CCredentialJwtV1Js")
data class W3CCredentialJwtV1Js(
    val alg: String? = null,
    val kid: String? = null,
    val typ: String? = null,
    val iss: String? = null,
    val sub: String? = null,
    val credentialSchemaJson: String? = null,
    val credentialStatusJson: String? = null,
)
