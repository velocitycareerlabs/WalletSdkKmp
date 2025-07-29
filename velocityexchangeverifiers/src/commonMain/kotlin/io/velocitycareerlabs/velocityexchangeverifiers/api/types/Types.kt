/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * A structured group of verifiers used for validating a W3C Credential JWT under the Velocity Profile.
 *
 * Each verifier enforces a distinct validation rule on the credential, such as checking required fields,
 * cryptographic algorithm support, issuer consistency, and schema conformance.
 *
 * This type allows verifiers to be injected for testing or customized validation behavior.
 */
data class CredentialVerifiers(
    val algIsSupported: Verifier<W3CCredentialJwtV1>,
    val credentialSchema: Verifier<W3CCredentialJwtV1>,
    val credentialStatus: Verifier<W3CCredentialJwtV1>,
    val issClaimMatchesEitherMetadataOrCredentialIssuer: Verifier<W3CCredentialJwtV1>,
    val issClaimMatchesMetadata: Verifier<W3CCredentialJwtV1>,
    val kidClaimIsVelocityV2: Verifier<W3CCredentialJwtV1>,
    val subIsDidJwkOrCnf: Verifier<W3CCredentialJwtV1>,
)

/**
 * Represents a reusable verification function that checks a value against one or more rules.
 *
 * A [Verifier] is a pure function that receives an input value and shared [VerificationContext],
 * and returns a lVerificationError describing any issue found.
 */
typealias Verifier<T> = (T, VerificationContext) -> VerificationError?

/**
 * Describes metadata about a credential issuer, typically retrieved from
 * the `.well-known/openid-credential-issuer` endpoint.
 *
 * This metadata is used during credential verification to verify issuer claims.
 *
 * @property iss The primary issuer identifier expected to match the `iss` claim in credentials.
 * @property credentialIssuer Optional fallback identifier used for OpenID4VCI compatibility.
 */
data class CredentialIssuerMetadata(
    val iss: String,
    val credentialIssuer: String? = null,
)

/**
 * Shared context passed to all verifiers during credential or response validation.
 *
 * This includes issuer metadata, the full response (if needed), and a hierarchical path
 * used to generate precise error traces.
 *
 * @property credentialIssuerMetadata Issuer metadata used to verify `iss` claims in credentials.
 * @property response Optional raw response object being verified, used by higher-level verifiers.
 * @property path Optional path to the current value being verified, for error tracing.
 */
data class VerificationContext(
    val credentialIssuerMetadata: CredentialIssuerMetadata? = null,
    val response: Any? = null,
    val path: List<Any>? = null,
)

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

/**
 * Represents a parsed Credential JWT used in VC issuance flows.
 *
 * Includes both JOSE header and payload fields with strongly typed VC-specific claims.
 */
@Serializable
data class W3CCredentialJwtV1(
    val header: JwtHeader?,
    val payload: JwtPayload,
)

/**
 * Represents the JOSE header section of a JWT.
 *
 * @property kid Optional Key ID; expected to start with `did:velocity:v2` under the Velocity Profile.
 * @property alg The cryptographic algorithm used to sign the JWT (e.g., ES256K, ES256, RS256).
 * @property additionalFields Any additional JOSE header parameters.
 */
@Serializable
data class JwtHeader(
    val alg: String,
    val kid: String? = null,
    val additionalFields: Map<String, JsonElement> = emptyMap(),
)

/**
 * Represents the payload section of a JWT.
 *
 * @property iss The credential issuer's identifier.
 * @property sub Optional subject identifier; typically "did:jwk".
 * @property cnf Optional confirmation object for key binding.
 * @property vc Verifiable Credential object containing schema and status.
 * @property additionalClaims Any additional claims in the payload.
 */
@Serializable
data class JwtPayload(
    val iss: String,
    val sub: String? = null,
    val cnf: JsonElement? = null,
    val vc: VcClaims,
    val additionalClaims: Map<String, JsonElement> = emptyMap(),
)

/**
 * Represents the Verifiable Credential (VC) object within a JWT.
 *
 * @property credentialSchema Credential schema URI or object. Required under the Velocity Profile.
 * @property credentialStatus Credential status information (e.g., revocation or suspension).
 * @property additionalFields Any additional VC fields.
 */
@Serializable
data class VcClaims(
    val credentialSchema: JsonElement? = null,
    val credentialStatus: JsonElement? = null,
    val additionalFields: Map<String, JsonElement> = emptyMap(),
)

/**
 * Represents a response from the Credential Endpoint (OpenID4VCI).
 *
 * Typically includes a list of [W3CCredentialJwtV1] objects issued by the credential issuer.
 *
 * @property credentials Optional list of issued credentials.
 * @property additionalFields Additional issuer-specific fields.
 */
@Serializable
data class CredentialEndpointResponse(
    val credentials: List<W3CCredentialJwtV1>? = null,
    val additionalFields: Map<String, JsonElement> = emptyMap(),
)

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
