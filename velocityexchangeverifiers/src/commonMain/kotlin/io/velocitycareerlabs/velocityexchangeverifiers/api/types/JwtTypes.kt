/**
 * Created by Michael Avoyan on 23/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api.types

import io.velocitycareerlabs.velocityexchangeverifiers.impl.extensions.decodeAsOrNull
import io.velocitycareerlabs.velocityexchangeverifiers.impl.serializers.JwtHeaderSerializer
import io.velocitycareerlabs.velocityexchangeverifiers.impl.serializers.JwtPayloadSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.jvm.Transient

/**
 * Represents a parsed Credential JWT used in VC issuance flows.
 *
 * Includes both JOSE header and payload fields with strongly typed VC-specific claims.
 */
@Serializable
data class W3CCredentialJwtV1(
    val header: JwtHeader?,
    val payload: JwtPayload,
)

/**
 * Represents the JOSE header section of a JWT.
 *
 * @property kid Optional Key ID; expected to start with `did:velocity:v2` under the Velocity Profile.
 * @property alg The cryptographic algorithm used to sign the JWT (e.g., ES256K, ES256, RS256).
 * @property typ Optional type, typically "JWT".
 * @property claims All raw fields from the header.
 */
@Serializable(with = JwtHeaderSerializer::class)
data class JwtHeader(
    val claims: Map<String, JsonElement>,
    @Transient val alg: String? = claims["alg"]?.jsonPrimitive?.contentOrNull,
    @Transient val kid: String? = claims["kid"]?.jsonPrimitive?.contentOrNull,
    @Transient val typ: String? = claims["typ"]?.jsonPrimitive?.contentOrNull,
)

/**
 * Represents the `cnf` (confirmation) claim used in JWTs for proof-of-possession.
 *
 * @property jwk A JSON Web Key (JWK) object representing the public key that the
 * presenter must prove possession of.
 */
@Serializable
data class Cnf(
    val claims: Map<String, JsonElement> = emptyMap(),
) {
    constructor(json: JsonObject) : this(json.toMap())

    val jwk: JsonObject? get() = claims["jwk"]?.jsonObject
}

/**
 * Represents the payload section of a JWT.
 *
 * @property iss The credential issuer's identifier.
 * @property sub Optional subject identifier; typically "did:jwk".
 * @property cnf Optional confirmation object for key binding.
 * @property vc Verifiable Credential object containing schema and status.
 * @property claims All raw fields from the payload.
 */
@Serializable(with = JwtPayloadSerializer::class)
data class JwtPayload(
    val claims: Map<String, JsonElement>,
    @Transient val iss: String? = claims["iss"]?.jsonPrimitive?.contentOrNull,
    @Transient val sub: String? = claims["sub"]?.jsonPrimitive?.contentOrNull,
    @Transient val cnf: Cnf? =
        runCatching {
            claims["cnf"]?.jsonObject?.let { Cnf(it) }
        }.getOrNull(),
    @Transient val vc: VcClaims? =
        runCatching {
            claims["vc"]?.jsonObject?.let { VcClaims(it) }
        }.getOrNull() ?: VcClaims(),
)

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
