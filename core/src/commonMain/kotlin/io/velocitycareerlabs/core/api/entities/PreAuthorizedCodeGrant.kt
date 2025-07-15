/**
 * Created by Michael Avoyan on 15/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.core.api.entities

import id.walt.oid4vc.data.TxCode

/**
 * Represents the pre-authorized code grant used in the OpenID4VCI issuance flow.
 *
 * This grant type allows the wallet to request an access token without requiring a user interaction flow
 * (such as browser-based authentication). The issuer provides this code in advance through a credential_offer.
 *
 * The optional [txCode] field provides additional information used to prompt the user to enter a short verification
 * code or PIN before proceeding with the token exchange. This improves security by ensuring that only authorized
 * holders can complete the flow.
 *
 * @property code The pre-authorized code issued by the credential issuer, used to obtain an access token.
 * @property txCode Optional data describing the input requirements for a user-entered transaction code.
 */
data class PreAuthorizedCodeGrant(
    val code: String,
    val txCode: TxCode? = null
)