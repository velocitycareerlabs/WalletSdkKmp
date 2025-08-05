/**
 * Created by Michael Avoyan on 05/08/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * Represents the Verifiable Credential (VC) object within a JWT.
 *
 * @property credentialSchema Credential schema object.
 * @property credentialStatus Credential status information.
 * @property claims All raw VC fields.
 */
@Serializable
data class VcClaims(
    val claims: Map<String, JsonElement> = emptyMap(),
) {
    constructor(json: JsonObject) : this(json.toMap())

    val credentialSchema: JsonObject? get() = claims["credentialSchema"]?.jsonObject
    val credentialStatus: JsonObject? get() = claims["credentialStatus"]?.jsonObject
}
