/**
 * Created by Michael Avoyan on 30/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.velocityexchangeverifiers.impl.serializers

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtPayload
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object JwtPayloadSerializer : KSerializer<JwtPayload> {
    override val descriptor: SerialDescriptor =
        MapSerializer(String.serializer(), JsonElement.serializer()).descriptor

    override fun deserialize(decoder: Decoder): JwtPayload {
        val input =
            decoder as? JsonDecoder
                ?: throw SerializationException("Expected JsonDecoder")
        val jsonObj = input.decodeJsonElement().jsonObject
        return JwtPayload(claims = jsonObj)
    }

    override fun serialize(
        encoder: Encoder,
        value: JwtPayload,
    ) {
        val output =
            encoder as? JsonEncoder
                ?: throw SerializationException("Expected JsonEncoder")
        output.encodeJsonElement(JsonObject(value.claims))
    }
}
