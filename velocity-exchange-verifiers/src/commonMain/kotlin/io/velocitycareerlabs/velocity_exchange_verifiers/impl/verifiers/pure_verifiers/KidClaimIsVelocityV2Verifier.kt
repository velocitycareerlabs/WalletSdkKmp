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
 * Verifies that the Credential JWT's `kid` (Key ID) claim starts with the required Velocity DID prefix.
 *
 * This verifier enforces the Velocity Profile Conformance rule that mandates the issuer's `kid`
 * to begin with `"did:velocity:v2"`, ensuring that the key used for signing is anchored in the expected
 * namespace. This is essential for enforcing trust boundaries and key provenance.
 *
 * @param credential A parsed [W3CCredentialJwtV1] containing a `header` with the `kid` field.
 * @param context The [VerificationContext] used to track the current JSON path for precise error reporting.
 * @return A [VerificationError] if the `kid` is missing or invalid, or null if valid.
 *
 * @validationRule `credential.header.kid` must start with `"did:velocity:v2"`.
 * @errorCode `INVALID_KID` — when `kid` is missing or does not begin with the required prefix.
 *
 * @see W3CCredentialJwtV1
 * @see VerificationError
 * @see VerificationContext
 */
val kidClaimIsVelocityV2Verifier: Verifier<W3CCredentialJwtV1> = { credential, context ->
    val kid = credential.header?.kid
    if (kid == null || !kid.startsWith("did:velocity:v2")) {
        buildError(
            code = ErrorCode.INVALID_KID,
            message = "kid must start with 'did:velocity:v2', got '$kid'",
            path = (context.path ?: emptyList()) + listOf("header", "kid")
        )
    } else {
        null
    }
}
