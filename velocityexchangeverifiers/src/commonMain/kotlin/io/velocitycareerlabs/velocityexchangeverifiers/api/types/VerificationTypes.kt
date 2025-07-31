/**
 * Created by Michael Avoyan on 30/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

/**
 * Represents a reusable verification function that checks a value against one or more rules.
 *
 * A [Verifier] is a pure function that receives an input value and shared [VerificationContext],
 * and returns a lVerificationError describing any issue found.
 */
typealias Verifier<T> = (T, VerificationContext) -> VerificationError?

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
