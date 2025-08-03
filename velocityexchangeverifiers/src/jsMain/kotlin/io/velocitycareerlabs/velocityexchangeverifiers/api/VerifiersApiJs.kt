/**
 * Created by Michael Avoyan on 31/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package io.velocitycareerlabs.velocityexchangeverifiers.api

import io.velocitycareerlabs.velocityexchangeverifiers.api.VerifiersApi.defaultCredentialVerifiers
import io.velocitycareerlabs.velocityexchangeverifiers.api.VerifiersApi.verifyCredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialIssuerMetadata
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContextJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationErrorJs
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.toJs
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * JavaScript/TypeScript-friendly access point to credential verification.
 */
@JsExport
object VerifiersApiJs {
    /**
     * Verifies a [CredentialEndpointResponse] against the Velocity ruleset.
     *
     * @param response The parsed endpoint response from the issuer.
     * @param contextJs JS-friendly context with issuer metadata.
     * @return Array of [VerificationErrorJs] describing issues found.
     */
    @JsName("verifyCredentialEndpointResponse")
    fun verifyCredentialEndpointResponseJs(
        response: CredentialEndpointResponse,
        contextJs: VerificationContextJs,
    ): Array<VerificationErrorJs> {
        val internalContext =
            VerificationContext(
                credentialIssuerMetadata =
                    contextJs.credentialIssuerMetadata?.let {
                        CredentialIssuerMetadata(it.iss, it.credentialIssuer)
                    },
                path = contextJs.path?.toList(),
            )

        return verifyCredentialEndpointResponse(
            response = response,
            context = internalContext,
            verifiers = defaultCredentialVerifiers,
        ).map { it.toJs() }.toTypedArray()
    }
}
