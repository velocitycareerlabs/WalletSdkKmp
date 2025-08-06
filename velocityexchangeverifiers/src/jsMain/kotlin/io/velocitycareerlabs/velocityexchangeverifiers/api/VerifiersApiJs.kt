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

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponseJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContextJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationErrorJs
import io.velocitycareerlabs.velocityexchangeverifiers.impl.toJs
import io.velocitycareerlabs.velocityexchangeverifiers.impl.toKotlin
import kotlin.js.JsName

/**
 * JavaScript/TypeScript-friendly entry point for credential verification.
 *
 * Wraps [VerifiersApi] with JS-compatible parameters and return types.
 */
@JsExport
@JsName("VerifiersApiJs")
class VerifiersApiJs {
    /**
     * Verifies a parsed [CredentialEndpointResponseJs] against the Velocity Network rule set.
     *
     * @param responseJs The issuer's parsed credential response (JS version).
     * @param contextJs JS/TS-friendly verification context.
     * @return Array of [VerificationErrorJs] representing failed validation rules, if any.
     */
    @JsName("verifyCredentialEndpointResponse")
    fun verifyCredentialEndpointResponseJs(
        responseJs: CredentialEndpointResponseJs,
        contextJs: VerificationContextJs,
    ): Array<VerificationErrorJs> {
        val verifiersApi = VerifiersApi()
        val internalResponse = responseJs.toKotlin()
        val internalContext = contextJs.toKotlin()

        return verifiersApi
            .verifyCredentialEndpointResponse(
                response = internalResponse,
                context = internalContext,
                verifiers = VerifiersApi.defaultCredentialVerifiers,
            ).map { it.toJs() }
            .toTypedArray()
    }
}
