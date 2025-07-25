/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers

import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.Verifier
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.W3CCredentialJwtV1

/**
 * Creates a compound verifier by composing multiple single-purpose verifiers.
 *
 * This function enables declarative validation by combining multiple reusable [Verifier] functions
 * into a single pipeline. Each verifier is applied to the same [value] and [context], and their
 * resulting [List]<[VerificationError]> are merged into a single result list.
 *
 * This utility is useful for building modular, maintainable validation logic.
 *
 * @param T The type of data being validated (e.g., a parsed credential JWT).
 * @param rules A list of [Verifier] functions to apply in order.
 * @return A [Verifier] that applies all provided rules and returns a combined list of validation errors.
 *
 * @see W3CCredentialJwtV1
 * @see VerificationError
 * @see VerificationContext
 */
fun <T> createVerifier(rules: List<Verifier<T>>): Verifier<T> = { value, context ->
    rules.flatMap { rule -> rule(value, context) }
}