/**
 * Created by Michael Avoyan on 15/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
 
package io.velocitycareerlabs.core.api.entities

import id.walt.oid4vc.data.CredentialOffer

/**
 * Represents the result of parsing and resolving a credential offer within the OpenID4VCI issuance flow.
 *
 * This data class aggregates the decoded credential offer, the pre-authorized code grant extracted from it,
 * and the resolved network environment in which the credential issuance process should take place.
 *
 * @property credentialOffer The original credential offer object, typically obtained via deep link or remote URI.
 * @property preAuthorizedCodeGrant The extracted pre-authorized code grant from the credential offer's `grants` section,
 *                                  including the code and optional tx_code if present.
 * @property network The resolved network environment (MAINNET, TESTNET, QANET, DEVNET), derived from the credential_issuer URL.
 */
data class CredentialOfferResult(
    val credentialOffer: CredentialOffer,
    val preAuthorizedCodeGrant: PreAuthorizedCodeGrant,
    val network: NetworkType
)