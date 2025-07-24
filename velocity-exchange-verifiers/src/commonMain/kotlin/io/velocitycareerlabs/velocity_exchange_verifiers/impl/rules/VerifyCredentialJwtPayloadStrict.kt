/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocity_exchange_verifiers.impl.rules

import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationError
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.W3CCredentialJwtV1
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.algIsSupportedVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.credentialSchemaVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.credentialStatusVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.issClaimMatchesEitherMetadataOrCredentialIssuerVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.issClaimMatchesMetadataVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.kidClaimIsVelocityV2Verifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.subIsDidJwkOrCnfVerifier

/**
 * Verifies a Credential JWT payload using strict validation rules defined by both
 * the Velocity Profile and the OpenID4VCI specification.
 *
 * This composite verifier applies multiple independent verifiers to enforce critical
 * constraints on the credential's structure and issuer claims. It validates the payload
 * with both Velocity-specific and spec-compliant fallback logic for maximum security
 * and interoperability.
 *
 * The following validations are applied:
 * - `header.alg` must be one of `ES256`, `ES256K`, or `RS256`.
 * - `payload.vc.credentialSchema` must be present.
 * - `payload.vc.credentialStatus` must be present.
 * - `payload.iss` must match either:
 *     - the exact `issuerMetadata.iss`
 *     - or `issuerMetadata.credentialIssuer` (fallback support)
 * - `payload.kid` must start with `did:velocity:v2`.
 * - Subject binding must be satisfied via:
 *     - `sub == "did:jwk"` or presence of `cnf`.
 *
 * @param credential The parsed credential (JWT) to validate.
 * @param context The verification context containing metadata and path tracking.
 * @return A list of [VerificationError]s, or an empty list if validation passes.
 */
fun verifyCredentialJwtPayloadStrict(
    credential: W3CCredentialJwtV1,
    context: VerificationContext
): List<VerificationError> {
    return listOf(
        algIsSupportedVerifier,
        credentialSchemaVerifier,
        credentialStatusVerifier,
        issClaimMatchesEitherMetadataOrCredentialIssuerVerifier,
        issClaimMatchesMetadataVerifier,
        kidClaimIsVelocityV2Verifier,
        subIsDidJwkOrCnfVerifier
    ).flatMap { verifier -> verifier(credential, context) }
}