/**
 * Created by Michael Avoyan on 15/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.core.api

import id.walt.credentials.signatures.sdjwt.SelectivelyDisclosableVerifiableCredential
import id.walt.oid4vc.data.CredentialOffer
import id.walt.oid4vc.responses.TokenResponse
import io.velocitycareerlabs.core.api.entities.CredentialOfferResult
import io.velocitycareerlabs.core.api.entities.NetworkType

interface MiniCredentialSdk {

    /**
     * Parses and validates a `credential_offer` from the given deep link URI string.
     *
     * The URI can contain either:
     * - a `credential_offer` parameter: a URL-encoded JSON string representing the credential offer,
     * - or a `credential_offer_uri`: a remote URL pointing to the JSON offer object.
     *
     * This method extracts and validates the offer, resolves the corresponding grant (e.g., pre-authorized_code),
     * and determines the target network based on the credential_issuer.
     *
     * @param uri The deep link string received from an external intent or invocation.
     * @return CredentialOfferResult including the parsed offer, grant information, and resolved network type.
     * @throws IllegalArgumentException if the URI is invalid or does not contain required parameters.
     */
    fun handleCredentialOfferUri(uri: String): CredentialOfferResult

    /**
     * Fetches and parses a `credential_offer` JSON object from a remote URI.
     *
     * This is used when a `credential_offer_uri` is provided, and performs an HTTP GET request to retrieve
     * the actual credential offer JSON payload, then parses it into a CredentialOffer data structure.
     *
     * @param uri The full URL to the credential offer JSON (typically from credential_offer_uri).
     * @return The parsed CredentialOffer object.
     * @throws IOException or deserialization errors if the response is invalid or unreachable.
     */
    fun fetchCredentialOffer(uri: String): CredentialOffer

    /**
     * Resolves the network type (mainnet, testnet, danet, or devnet) based on the credential_issuer URL.
     *
     * This helps the wallet select the appropriate configuration/environment for API interaction.
     * The decision is typically based on known issuer domain patterns.
     *
     * @param offer The CredentialOffer that contains the credential_issuer field.
     * @return A NetworkType enum representing the environment the offer originates from.
     */
    fun resolveNetwork(offer: CredentialOffer): NetworkType

    /**
     * Exchanges the provided pre-authorized code for an access token using the OID4VCI token endpoint.
     *
     * This flow does not require user interaction or redirect URI. It is commonly used in mobile wallets
     * and is compliant with the urn:ietf:params:oauth:grant-type:pre-authorized_code grant.
     *
     * Optionally, a txCode (e.g., a PIN or user-entered code) can be supplied if required by the issuer.
     *
     * @param issuer The base URL of the credential issuer.
     * @param code The pre-authorized code received in the credential offer.
     * @param txCode Optional user-entered code (tx_code), if requested by the issuer.
     * @return A TokenResponse containing the access token and related metadata.
     * @throws AuthorizationException if the exchange fails due to invalid or expired code.
     */
    fun exchangePreAuthorizedCode(
        issuer: String,
        code: String,
        txCode: String? = null
    ): TokenResponse

    /**
     * Requests a verifiable credential from the issuer's Credential Endpoint using the access token.
     *
     * This method uses the credential_issuer and credential_configuration_ids declared in the CredentialOffer,
     * and is capable of retrieving SD-JWT credentials or other supported formats.
     *
     * The implementation must include proof-of-possession (e.g., DID-based signing) if required by the issuer.
     *
     * @param accessToken The access token obtained from the token endpoint.
     * @param credentialOffer The CredentialOffer used to determine the credential configuration.
     * @return A SelectivelyDisclosableVerifiableCredential object representing the issued credential.
     * @throws CredentialIssuanceException if the issuance fails or the response is invalid.
     */
    fun requestCredential(
        accessToken: String,
        credentialOffer: CredentialOffer
    ): SelectivelyDisclosableVerifiableCredential
}