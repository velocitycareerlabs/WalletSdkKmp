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
 * JavaScript/TypeScript-friendly version of [CredentialIssuerMetadata].
 */
@JsExport
data class CredentialIssuerMetadataJs(
    val iss: String,
    val credentialIssuer: String? = null,
)

/**
 * JavaScript/TypeScript-friendly version of [VerificationContext].
 */
@JsExport
data class VerificationContextJs(
    val credentialIssuerMetadata: CredentialIssuerMetadataJs? = null,
    val path: Array<String>? = null,
)

/**
 * Maps a [VerificationContext] to a JS-compatible [VerificationContextJs].
 */
@JsName("toVerificationContextJs")
fun VerificationContext.toJs(): VerificationContextJs =
    VerificationContextJs(
        credentialIssuerMetadata =
            this.credentialIssuerMetadata?.let {
                CredentialIssuerMetadataJs(it.iss, it.credentialIssuer)
            },
        path = this.path?.map { it.toString() }?.toTypedArray(),
    )
