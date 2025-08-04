/**
 * Created by Michael Avoyan on 30/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

/**
 * Enumerates all supported error codes used by credential validation verifiers.
 *
 * Each error code corresponds to a specific validation failure condition.
 *
 * @see VerificationError.code
 *
 * ### Example:
 * ```
 * if (error.code == ErrorCode.MISSING_CREDENTIAL_SCHEMA) {
 *     // Handle missing schema
 * }
 * ```
 */
enum class ErrorCode(
    val code: String,
) {
    /** Unsupported or unknown signing algorithm. */
    INVALID_ALG("invalid_alg"),

    /** Key ID is missing or not prefixed with `did:velocity:v2`. */
    INVALID_KID("invalid_kid"),

    /** Credential is missing both `sub` and `cnf` claims. */
    SUB_OR_CNF_MISSING("sub_or_cnf_missing"),

    /** `iss` claim in the credential does not match expected issuer metadata. */
    UNEXPECTED_CREDENTIAL_PAYLOAD_ISS("unexpected_credential_payload_iss"),

    /** Unexpected or malformed issuer metadata. */
    UNEXPECTED_CREDENTIAL_ISSUER_METADATA("unexpected_credential_credential_issuer_metadata"),

    /** Credential lacks a `credentialStatus` entry, required for revocation. */
    MISSING_CREDENTIAL_STATUS("missing_credential_status"),

    /** Credential lacks a `credentialSchema`, required under Velocity profile. */
    MISSING_CREDENTIAL_SCHEMA("missing_credential_schema"),
}

/**
 * Represents a structured verification error produced by a verifier.
 *
 * Errors are designed to be machine-readable, traceable to a path,
 * and informative for debugging or user-facing feedback.
 *
 * @property code A stable, machine-readable identifier for the verification rule that failed.
 * @property message A human-readable description of what went wrong.
 * @property path Optional JSON path to the offending value.
 */
data class VerificationError(
    val code: ErrorCode,
    val message: String,
    val path: List<Any>? = null,
)
