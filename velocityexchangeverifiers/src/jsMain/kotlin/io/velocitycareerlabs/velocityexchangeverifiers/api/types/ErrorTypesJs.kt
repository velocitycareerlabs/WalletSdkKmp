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
 * JavaScript/TypeScript-friendly version of [VerificationError].
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("VerificationErrorJs")
data class VerificationErrorJs(
    val code: String,
    val message: String,
    val path: Array<String>? = null,
)

/**
 * Converts a [VerificationError] into a [VerificationErrorJs] for JS/TS interop.
 */
internal fun VerificationError.toVerificationErrorJs(): VerificationErrorJs =
    VerificationErrorJs(
        code = this.code.code,
        message = this.message,
        path = this.path?.map { it.toString() }?.toTypedArray(),
    )
