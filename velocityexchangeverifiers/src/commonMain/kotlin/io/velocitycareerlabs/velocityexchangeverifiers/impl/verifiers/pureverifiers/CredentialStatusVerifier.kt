/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.ErrorCode
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.Verifier
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import io.velocitycareerlabs.velocityexchangeverifiers.impl.errors.buildError
import io.velocitycareerlabs.velocityexchangeverifiers.impl.utils.withPath

/**
 * Verifies that the `vc.credentialStatus` field exists in the Credential JWT payload.
 *
 * This verifier ensures that the `credentialStatus` property is present within the Verifiable Credential (VC)
 * section of the JWT payload. This field is critical for enabling status checks like revocation or suspension,
 * and is required by the Velocity Profile conformance rules.
 *
 * @param credential The [W3CCredentialJwtV1] object containing both `header` and `payload`.
 * @param context The [VerificationContext] used for issuer metadata and error path tracking.
 *
 * @return A [VerificationError] if the field is missing, or `null` if the credential is valid.
 *
 * @see W3CCredentialJwtV1
 * @see VerificationError
 * @see VerificationContext
 */
internal val credentialStatusVerifier: Verifier<W3CCredentialJwtV1> = { credential, context ->
    val exists = credential.payload.vc?.credentialStatus != null
    if (!exists) {
        buildError(
            code = ErrorCode.MISSING_CREDENTIAL_STATUS,
            message = "Expected vc.credentialStatus to exist",
            path = withPath(context, listOf("payload", "vc", "credentialStatus")).path ?: emptyList(),
        )
    } else {
        null
    }
}
