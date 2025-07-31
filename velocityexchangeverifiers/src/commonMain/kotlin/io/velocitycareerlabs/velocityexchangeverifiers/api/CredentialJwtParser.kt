/**
 * Created by Michael Avoyan on 30/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.api

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Utility class for parsing JSON strings into strongly typed VC credential data structures
 * defined by the Velocity Exchange Verifiers library.
 *
 * This class wraps the use of [kotlinx.serialization] to deserialize external JSON payloads
 * (such as those received from credential endpoints or embedded in JWTs) into the internal
 * domain models like [W3CCredentialJwtV1] and [CredentialEndpointResponse].
 *
 * Use this class to safely and consistently decode input data across platforms (JVM, JS, iOS).
 */
object CredentialJwtParser {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    /**
     * Parses a JSON string into a [W3CCredentialJwtV1] object.
     */
    @Throws(SerializationException::class)
    fun parseCredentialJwt(jsonString: String): W3CCredentialJwtV1 = json.decodeFromString(W3CCredentialJwtV1.serializer(), jsonString)

    /**
     * Parses a JSON string into a [CredentialEndpointResponse] object.
     */
    @Throws(SerializationException::class)
    fun parseCredentialEndpointResponse(jsonString: String): CredentialEndpointResponse =
        json.decodeFromString(CredentialEndpointResponse.serializer(), jsonString)

    /**
     * Parses a credential JWT or returns null if it fails.
     */
    fun parseCredentialJwtOrNull(jsonString: String): W3CCredentialJwtV1? = runCatching { parseCredentialJwt(jsonString) }.getOrNull()

    /**
     * Parses a credential endpoint response or returns null if it fails.
     */
    fun parseCredentialEndpointResponseOrNull(jsonString: String): CredentialEndpointResponse? =
        runCatching {
            parseCredentialEndpointResponse(jsonString)
        }.getOrNull()
}
