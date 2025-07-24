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
 * Verifies that the Credential JWT's `iss` claim exactly matches the expected issuer metadata.
 *
 * This verifier enforces the Velocity Profile requirement that the `payload.iss` value
 * must exactly equal `credentialIssuerMetadata.iss`. No fallback matching is permitted.
 *
 * @param credential The [W3CCredentialJwtV1] to validate.
 * @param context The [VerificationContext] containing issuer metadata and validation path.
 * @return A list of [VerificationError] if the issuer does not match, otherwise an empty list.
 *
 * @see W3CCredentialJwtV1
 * @see VerificationError
 * @see VerificationContext
 */
val issClaimMatchesMetadataVerifier: Verifier<W3CCredentialJwtV1> =
    { credential, context ->
        val actual = credential.payload.iss;
        val expected = context.credentialIssuerMetadata?.iss;
        val path = withPath(context, listOf("payload", "iss")).path ?: emptyList()

        if (actual != expected) {
            listOf(
                buildError(
                    ErrorCode.UNEXPECTED_CREDENTIAL_PAYLOAD_ISS,
                    "Expected iss to be exactly '$expected', but got '$actual'",
                    path
                )
            )
        } else {
            emptyList()
        }
    }