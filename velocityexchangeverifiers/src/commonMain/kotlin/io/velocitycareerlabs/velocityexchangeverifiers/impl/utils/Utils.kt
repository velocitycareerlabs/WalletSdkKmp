/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.impl.utils

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext

/**
 * Appends a nested path segment to an existing [VerificationContext].
 *
 * This utility is used during recursive validation to track the exact location
 * of a field or value within a nested JSON structure (e.g., a Verifiable Credential).
 * It ensures that each verifier can report precise and traceable error locations.
 *
 * The resulting `context.path` is composed by appending [pathExtension] to the
 * existing `context.path`, if any. This supports both object and array navigation.
 *
 * ### Example
 * ```
 * val baseContext = VerificationContext(path = listOf("credentials", 0))
 * val extended = withPath(baseContext, listOf("payload", "vc", "credentialSchema"))
 * println(extended.path) // [credentials, 0, payload, vc, credentialSchema]
 * ```
 *
 * @param context The original validation context to extend
 * @param pathExtension A list of keys or indices to append to the existing path
 * @return A new [VerificationContext] with the extended path
 *
 * @see VerificationContext
 */
internal fun withPath(
    context: VerificationContext,
    pathExtension: List<Any>,
): VerificationContext =
    context.copy(
        path = (context.path ?: emptyList()) + pathExtension,
    )
