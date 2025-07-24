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
 * Verifies that the `vc.credentialSchema` field exists in the Credential JWT payload.
 *
 * This verifier ensures that the `credentialSchema` property is present inside the Verifiable Credential (VC)
 * object located within the payload. This is a mandatory requirement for conformance with the Velocity Profile.
 * If missing, the credential is considered invalid under profile rules.
 *
 * @param credential The [W3CCredentialJwtV1] object containing both `header` and `payload`.
 * @param context The [VerificationContext] used for error path tracking and metadata access.
 *
 * @return A list of [VerificationError] containing a single error if the field is missing,
 * or an empty list if the credential is valid.
 *
 * @see W3CCredentialJwtV1
 * @see VerificationError
 * @see VerificationContext
 */
val credentialSchemaVerifier: Verifier<W3CCredentialJwtV1> = { credential, context ->
    if (credential.payload.vc.credentialSchema == null) {
        listOf(
            buildError(
                code = ErrorCode.MISSING_CREDENTIAL_SCHEMA,
                message = "Expected credentialSchema in payload.vc.credentialSchema but got undefined",
                path = withPath(context, listOf("payload", "vc", "credentialSchema")).path ?: emptyList()
            )
        )
    } else {
        emptyList()
    }
}