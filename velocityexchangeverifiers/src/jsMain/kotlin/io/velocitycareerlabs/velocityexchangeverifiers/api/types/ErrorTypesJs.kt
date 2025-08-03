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
 * JavaScript/TypeScript-friendly version of [VerificationError].
 */
@OptIn(ExperimentalJsExport::class)
@JsName("VerificationErrorJs")
data class VerificationErrorJs(
    val code: String,
    val message: String,
    val path: Array<String>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as VerificationErrorJs

        if (code != other.code) return false
        if (message != other.message) return false
        if (!path.contentEquals(other.path)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + (path?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * Converts a [VerificationError] into a [VerificationErrorJs] object for use in JS/TS environments.
 */
@JsName("toVerificationErrorJs")
fun VerificationError.toJs(): VerificationErrorJs =
    VerificationErrorJs(
        code = this.code.code,
        message = this.message,
        path = this.path?.map { it.toString() }?.toTypedArray(),
    )
