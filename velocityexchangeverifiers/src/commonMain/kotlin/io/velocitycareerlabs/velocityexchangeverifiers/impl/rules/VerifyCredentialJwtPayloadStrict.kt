/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.impl.rules

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialVerifiers
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationError
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1

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
internal fun verifyCredentialJwtPayloadStrict(
    credential: W3CCredentialJwtV1,
    context: VerificationContext,
    verifiers: CredentialVerifiers,
): List<VerificationError> =
    listOfNotNull(
        verifiers.algIsSupported(credential, context),
        verifiers.credentialSchema(credential, context),
        verifiers.credentialStatus(credential, context),
        verifiers.issClaimMatchesEitherMetadataOrCredentialIssuer(credential, context),
        verifiers.issClaimMatchesMetadata(credential, context),
        verifiers.kidClaimIsVelocityV2(credential, context),
        verifiers.subIsDidJwkOrCnf(credential, context),
    )
