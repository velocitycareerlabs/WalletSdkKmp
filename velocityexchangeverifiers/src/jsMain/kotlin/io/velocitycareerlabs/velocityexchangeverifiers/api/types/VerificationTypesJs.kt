/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * JavaScript/TypeScript-friendly version of [CredentialIssuerMetadata].
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("CredentialIssuerMetadataJs")
data class CredentialIssuerMetadataJs(
    val iss: String,
    val credentialIssuer: String? = null,
)

/**
 * JavaScript/TypeScript-friendly version of [VerificationContext].
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("VerificationContextJs")
data class VerificationContextJs(
    val credentialIssuerMetadata: CredentialIssuerMetadataJs? = null,
    val path: Array<String>? = null,
)

internal fun VerificationContext.toVerificationContextJs(): VerificationContextJs =
    VerificationContextJs(
        credentialIssuerMetadata = this.credentialIssuerMetadata?.toCredentialIssuerMetadataJs(),
        path = this.path?.map { it.toString() }?.toTypedArray(),
    )

internal fun CredentialIssuerMetadata.toCredentialIssuerMetadataJs(): CredentialIssuerMetadataJs =
    CredentialIssuerMetadataJs(
        iss = this.iss,
        credentialIssuer = this.credentialIssuer,
    )

internal fun CredentialIssuerMetadataJs.toInternal(): CredentialIssuerMetadata =
    CredentialIssuerMetadata(
        iss = this.iss,
        credentialIssuer = this.credentialIssuer,
    )

internal fun VerificationContextJs.toInternal(): VerificationContext =
    VerificationContext(
        credentialIssuerMetadata = this.credentialIssuerMetadata?.toInternal(),
        path = this.path?.toList(),
    )
