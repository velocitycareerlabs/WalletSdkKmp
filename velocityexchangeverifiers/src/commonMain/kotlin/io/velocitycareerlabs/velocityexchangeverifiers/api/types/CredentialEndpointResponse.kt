/**
 * Created by Michael Avoyan on 05/08/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import io.velocitycareerlabs.velocityexchangeverifiers.impl.extensions.decodeAsOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Represents a response from the Credential Endpoint (OpenID4VCI).
 *
 * Typically includes a list of [W3CCredentialJwtV1] objects issued by the credential issuer.
 *
 * @property credentials Optional list of issued credentials.
 * @property claims All raw fields from the response.
 */
@Serializable
data class CredentialEndpointResponse(
    val claims: Map<String, JsonElement> = emptyMap(),
) {
    val credentials: List<W3CCredentialJwtV1>? =
        runCatching { claims["credentials"]?.decodeAsOrNull<List<W3CCredentialJwtV1>>() }.getOrNull()
}
