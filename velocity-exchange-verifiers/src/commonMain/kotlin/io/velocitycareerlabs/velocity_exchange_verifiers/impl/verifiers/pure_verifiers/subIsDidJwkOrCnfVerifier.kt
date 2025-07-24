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

/**
 * Verifies that the Credential JWT payload includes a valid subject declaration using either `sub` or `cnf`.
 *
 * According to the OpenID for Verifiable Credential Issuance (OpenID4VCI) specification and the Velocity profile,
 * a credential must express subject binding via either:
 *
 * - A `sub` (subject) claim with the exact value `"did:jwk"`, or
 * - A `cnf` (confirmation) object containing key binding information.
 *
 * This verifier enforces that at least one of these identity mechanisms is present.
 *
 * @param credential The parsed [W3CCredentialJwtV1] to validate.
 * @param context A [VerificationContext] object, used to trace the source of the error.
 * @return A list containing a [VerificationError] if validation fails, or an empty list if the credential is valid.
 *
 * @validationRule `payload.sub` must equal `"did:jwk"` **or** `payload.cnf` must be present.
 * @errorCode `SUB_OR_CNF_MISSING` — if both are missing or invalid.
 *
 * @see W3CCredentialJwtV1
 * @see VerificationError
 * @see VerificationContext
 */
val subIsDidJwkOrCnfVerifier: Verifier<W3CCredentialJwtV1> = { credential, context ->
    val sub = credential.payload.sub
    val cnf = credential.payload.cnf

    if (sub != "did:jwk" && cnf == null) {
        listOf(
            buildError(
                code = ErrorCode.SUB_OR_CNF_MISSING,
                message = "Expected sub to be 'did:jwk' or cnf to be present. Got sub=$sub, cnf=${cnf?.toString() ?: "null"}",
                path = (context.path ?: emptyList()) + "payload"
            )
        )
    } else {
        emptyList()
    }
}