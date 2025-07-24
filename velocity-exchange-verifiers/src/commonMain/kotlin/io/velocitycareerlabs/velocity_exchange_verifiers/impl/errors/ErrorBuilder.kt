/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocity_exchange_verifiers.impl.errors

import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.ErrorCode
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationError

/**
 * Creates a [VerificationError] with the given error code, message, and location path.
 *
 * @param code A predefined [ErrorCode] representing the type of validation failure.
 * @param message A human-readable explanation describing the error condition.
 * @param path An optional list representing the JSON pointer path to the offending field.
 *             This helps identify the exact location of the error within nested structures.
 *
 * @return A [VerificationError] object with standardized shape suitable for reporting or logging.
 *
 * @see VerificationError
 * @see ErrorCode
 *
 * ### Example
 * ```
 * val error = buildError(ErrorCodes.INVALID_ALG, "Unsupported algorithm", listOf("header", "alg"))
 * ```
 */
fun buildError(
    code: ErrorCode,
    message: String,
    path: List<Any> = emptyList()
): VerificationError {
    return VerificationError(code = code, message = message, path = path)
}