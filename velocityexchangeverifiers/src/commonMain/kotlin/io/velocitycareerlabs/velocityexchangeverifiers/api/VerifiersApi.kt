/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialVerifiers
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationError
import io.velocitycareerlabs.velocityexchangeverifiers.impl.rules.verifyCredentialJwtPayloadStrict
import io.velocitycareerlabs.velocityexchangeverifiers.impl.utils.withPath
import io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers.algIsSupportedVerifier
import io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers.credentialSchemaVerifier
import io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers.credentialStatusVerifier
import io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers.issClaimMatchesEitherMetadataOrCredentialIssuerVerifier
import io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers.issClaimMatchesMetadataVerifier
import io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers.kidClaimIsVelocityV2Verifier
import io.velocitycareerlabs.velocityexchangeverifiers.impl.verifiers.pureverifiers.subIsDidJwkOrCnfVerifier

class VerifiersApi {
    /**
     * Verifies the structure and contents of a Credential Endpoint response.
     *
     * @param response The object returned by the Credential Issuer's Credential Endpoint.
     * @param context Validation context containing issuer metadata and the current verification path.
     * @return A list of [io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationError] objects describing all validation issues found.
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
        verifiers: CredentialVerifiers = defaultCredentialVerifiers,
    ): List<VerificationError> {
        val credentials = response.credentials ?: return emptyList()

        return credentials.flatMapIndexed { index, credential ->
            verifyCredentialJwtPayloadStrict(
                credential = credential,
                context = withPath(context, listOf("credentials", index)),
                verifiers = verifiers,
            )
        }
    }
//    suspend fun verifyCredentialEndpointResponse(
//        response: CredentialEndpointResponse,
//        context: VerificationContext,
//        verifiers: CredentialVerifiers = defaultCredentialVerifiers,
//    ): List<VerificationError> =
//        coroutineScope {
//            val credentials = response.credentials ?: return@coroutineScope emptyList()
//
//            credentials
//                .mapIndexed { index, credential ->
//                    async {
//                        verifyCredentialJwtPayloadStrict(
//                            credential = credential,
//                            context = withPath(context, listOf("credentials", index)),
//                            verifiers = verifiers,
//                        )
//                    }
//                }.awaitAll()
//                .flatten()
//        }

    /**
     * The default set of verifiers that enforce the Velocity profile rules for credential validation.
     */
    val defaultCredentialVerifiers =
        CredentialVerifiers(
            algIsSupported = algIsSupportedVerifier,
            credentialSchema = credentialSchemaVerifier,
            credentialStatus = credentialStatusVerifier,
            issClaimMatchesEitherMetadataOrCredentialIssuer = issClaimMatchesEitherMetadataOrCredentialIssuerVerifier,
            issClaimMatchesMetadata = issClaimMatchesMetadataVerifier,
            kidClaimIsVelocityV2 = kidClaimIsVelocityV2Verifier,
            subIsDidJwkOrCnf = subIsDidJwkOrCnfVerifier,
        )
}
