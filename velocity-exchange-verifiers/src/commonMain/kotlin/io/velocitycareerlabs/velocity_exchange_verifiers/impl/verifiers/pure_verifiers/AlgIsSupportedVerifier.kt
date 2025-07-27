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
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationError

/**
 * Verifies that the JWT's `header.alg` value is one of the supported algorithms.
 *
 * This verifier ensures that the credential is signed using an approved algorithm,
 * as required by the Velocity profile and JOSE (JSON Object Signing and Encryption) standards.
 *
 * Supported algorithms are:
 * - ES256
 * - ES256K
 * - RS256
 *
 * @param credential The credential JWT to verify.
 * @param context The verification context used to trace the path of the validated field.
 * @return A [VerificationError] if validation fails, or null if the algorithm is supported.
 *
 * @see W3CCredentialJwtV1
 * @see VerificationError
 */
val algIsSupportedVerifier: Verifier<W3CCredentialJwtV1> = { credential, context ->
    val alg = credential.header?.alg
    if (alg !in supportedAlgs) {
        buildError(
            code = ErrorCode.INVALID_ALG,
            message = "Unsupported alg: '$alg'",
            path = (context.path ?: emptyList()) + listOf("header", "alg")
        )
    } else {
        null
    }
}

private val supportedAlgs = listOf("ES256", "ES256K", "RS256")
