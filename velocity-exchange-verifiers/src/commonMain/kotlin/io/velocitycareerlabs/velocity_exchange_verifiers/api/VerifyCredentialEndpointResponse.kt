/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocity_exchange_verifiers.api

import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.CredentialVerifiers
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationError
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.rules.verifyCredentialJwtPayloadStrict
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.utils.withPath
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.algIsSupportedVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.credentialSchemaVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.credentialStatusVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.issClaimMatchesEitherMetadataOrCredentialIssuerVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.issClaimMatchesMetadataVerifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.kidClaimIsVelocityV2Verifier
import io.velocitycareerlabs.velocity_exchange_verifiers.impl.verifiers.pure_verifiers.subIsDidJwkOrCnfVerifier

/**
 * Verifies the structure and contents of a Credential Endpoint response.
 *
 * @param response The object returned by the Credential Issuer's Credential Endpoint.
 * @param context Validation context containing issuer metadata and the current verification path.
 * @return A list of [io.velocitycareerlabs.velocity_exchange_verifiers.api.types.VerificationError] objects describing all validation issues found.
 *
 * This validator is designed for **immediate issuance flows** as defined by
 * the OpenID for Verifiable Credential Issuance (OpenID4VCI) specification.
 *
 * Each entry in `response.credentials` is passed to [verifyCredentialJwtPayloadStrict],
 * which applies strict Velocity profile validation including:
 *
 * - Supported signing algorithm (`alg`)
 * - Exact issuer matching (`iss`)
 * - Velocity-compliant `kid` prefix
 * - Subject identity (`sub` or `cnf`)
 * - Credential schema and status presence
 *
 * #### Behavior Notes:
 * - If `credentials` is missing or empty, this function returns an empty list.
 * - Fields like `notification_id` are **not yet validated**.
 * - Deferred issuance (`transaction_id`, `interval`) is **not supported here**.
 */
fun verifyCredentialEndpointResponse(
    response: CredentialEndpointResponse,
    context: VerificationContext,
    verifiers: CredentialVerifiers = defaultCredentialVerifiers
): List<VerificationError> {
    val credentials = response.credentials ?: return emptyList()

    return credentials.flatMapIndexed { index, credential ->
        verifyCredentialJwtPayloadStrict(
            credential,
            withPath(context, listOf("credentials", index)),
            verifiers
        )
    }
}

val defaultCredentialVerifiers = CredentialVerifiers(
    algIsSupported = algIsSupportedVerifier,
    credentialSchema = credentialSchemaVerifier,
    credentialStatus = credentialStatusVerifier,
    issClaimMatchesEitherMetadataOrCredentialIssuer = issClaimMatchesEitherMetadataOrCredentialIssuerVerifier,
    issClaimMatchesMetadata = issClaimMatchesMetadataVerifier,
    kidClaimIsVelocityV2 = kidClaimIsVelocityV2Verifier,
    subIsDidJwkOrCnf = subIsDidJwkOrCnfVerifier
)
