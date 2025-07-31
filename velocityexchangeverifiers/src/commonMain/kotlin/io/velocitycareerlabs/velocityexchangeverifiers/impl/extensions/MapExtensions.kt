/**
 * Created by Michael Avoyan on 30/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.impl.extensions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

fun Map<String, JsonElement>.getString(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull

inline fun <reified T> JsonElement.decodeAs(json: Json = Json.Default): T {
    // Use kotlinx.serialization's reified decoding from JsonElement:
    return json.decodeFromJsonElement<T>(this)
}

inline fun <reified T> JsonElement.decodeAsOrNull(json: Json = Json.Default): T? {
    if (this is JsonNull) return null
    return runCatching { json.decodeFromJsonElement<T>(this) }.getOrNull()
}

inline fun <reified T> T.encodeAsJsonElement(): JsonElement =
    kotlinx.serialization.json.Json
        .encodeToJsonElement(this)
