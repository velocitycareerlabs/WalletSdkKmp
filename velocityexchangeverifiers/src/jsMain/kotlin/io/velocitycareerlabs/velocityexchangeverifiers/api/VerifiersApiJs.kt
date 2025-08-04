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

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContextJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationErrorJs
import io.velocitycareerlabs.velocityexchangeverifiers.impl.toInternal
import io.velocitycareerlabs.velocityexchangeverifiers.impl.toVerificationErrorJs
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
     * Verifies a parsed [CredentialEndpointResponse] against the Velocity Network rule set.
     *
     * @param response The issuer's parsed credential response.
     * @param contextJs JS/TS-friendly verification context.
     * @return Array of [VerificationErrorJs] representing failed validation rules, if any.
     */
    @JsName("verifyCredentialEndpointResponse")
    fun verifyCredentialEndpointResponseJs(
        response: CredentialEndpointResponse,
        contextJs: VerificationContextJs,
    ): Array<VerificationErrorJs> {
        val internalContext = contextJs.toInternal()

        return VerifiersApi
            .verifyCredentialEndpointResponse(
                response = response,
                context = internalContext,
                verifiers = VerifiersApi.defaultCredentialVerifiers,
            ).map { it.toVerificationErrorJs() }
            .toTypedArray()
    }
}
