/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers

import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.ErrorCode
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.Verifier
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.W3CCredentialJwtV1
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.errors.buildError
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.utils.withPath

/**
 * Verifies that the Credential JWT's `iss` claim matches the expected issuer metadata.
 *
 * This verifier implements the OpenID4VCI specification fallback behavior by validating that
 * the `payload.iss` field in the Credential JWT matches either:
 * - `credentialIssuerMetadata.iss`
 * - `credentialIssuerMetadata.credentialIssuer`
 *
 * This ensures conformance with OpenID4VCI while enabling broader ecosystem compatibility.
 *
 * @param credential The [W3CCredentialJwtV1] object containing the credential payload.
 * @param context The [VerificationContext] including issuer metadata and optional path.
 * @return A list of [VerificationError] if validation fails, or an empty list if valid.
 *
 * @see W3CCredentialJwtV1
 * @see VerificationError
 * @see VerificationContext
 */
val issClaimMatchesEitherMetadataOrCredentialIssuerVerifier: Verifier<W3CCredentialJwtV1> =
    { credential, context ->

        val credentialIssuerMetadata = context.credentialIssuerMetadata

        // Filter out null values explicitly
        val allowedValues = listOfNotNull(
            credentialIssuerMetadata?.iss?.takeIf { it.isNotBlank() },
            credentialIssuerMetadata?.credentialIssuer?.takeIf { it.isNotBlank() }
        )

        val actualIss = credential.payload.iss
        if (actualIss.isBlank() || actualIss !in allowedValues) {
            listOf(
                buildError(
                    ErrorCode.UNEXPECTED_CREDENTIAL_PAYLOAD_ISS,
                    "Expected iss to be one of [${allowedValues.joinToString()}], but got '$actualIss'",
                    (context.path ?: emptyList()) + listOf("payload", "iss")
                )
            )
        } else {
            emptyList()
        }
    }