/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("NON_CONSUMABLE_EXPORTED_IDENTIFIER", "OPT_IN_USAGE")
@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * JS/TS-friendly version of [VerificationError].
 *
 * This structure is exported to JavaScript to represent verifier errors in a way that is
 * idiomatic and consumable by TypeScript clients.
 *
 * @property code Stable string identifier for the type of verification failure.
 * @property message Human-readable explanation of what went wrong.
 * @property path Optional JSON pointer indicating the affected path in the credential.
 */
@JsName("VerificationErrorJs")
data class VerificationErrorJs(
    val code: String,
    val message: String,
    val path: Array<String>? = null,
)
