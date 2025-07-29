/**
 * Created by Michael Avoyan on 29/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vnf.wallet.walletsdkkmp

import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialEndpointResponse
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialIssuerMetadata
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtHeader
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.JwtPayload
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.W3CCredentialJwtV1
import io.velocitycareerlabs.velocityexchangeverifiers.impl.utils.fromJson

class Constants {
    companion object {
        @Suppress("ktlint:standard:max-line-length")
        const val JWT_HEADER_MOCK =
            "{\n" +
                "  \"typ\": \"JWT\",\n" +
                "  \"kid\": \"did:velocity:v2:0x34ffcc9ccb0b80ecf9c341ba2726ce179f3c7a89:185070538008268:1696#key-1\",\n" +
                "  \"alg\": \"ES256K\"\n" +
                "}"

        @Suppress("ktlint:standard:max-line-length")
        const val JWT_PAYLOAD_MOCK =
            "{\n" +
                "  \"vc\": {\n" +
                "    \"@context\": [\n" +
                "      \"https://www.w3.org/2018/credentials/v1\"\n" +
                "    ],\n" +
                "    \"type\": [\n" +
                "      \"EmailV1.0\",\n" +
                "      \"VerifiableCredential\"\n" +
                "    ],\n" +
                "    \"credentialStatus\": {\n" +
                "      \"type\": \"VelocityRevocationListJan2021\",\n" +
                "      \"id\": \"ethereum:0x1C29461C7480d1d8570df7c0A4F314D0bE8cD5Bf/getRevokedStatus?address=0xeC529b7bbC84821D2e71001c9F4fed80dd44b655&listId=58998093281042&index=2695\",\n" +
                "      \"statusListIndex\": 2695,\n" +
                "      \"statusListCredential\": \"ethereum:0x1C29461C7480d1d8570df7c0A4F314D0bE8cD5Bf/getRevokedStatus?address=0xeC529b7bbC84821D2e71001c9F4fed80dd44b655&listId=58998093281042\",\n" +
                "      \"linkCodeCommit\": \"EiAPe/02cKuCP8qc4xhgr31wra7jT3ogBr9/yD7JQMwIeg==\"\n" +
                "    },\n" +
                "    \"contentHash\": {\n" +
                "      \"type\": \"VelocityContentHash2020\",\n" +
                "      \"value\": \"89d4dcc86d540c6eea39d178eedc30213e782f251e03b76f4273106808dd0d4f\"\n" +
                "    },\n" +
                "    \"credentialSchema\": {\n" +
                "      \"id\": \"https://stagingregistrar.velocitynetwork.foundation/schemas/email-v1.0.schema.json\",\n" +
                "      \"type\": \"JsonSchemaValidator2018\"\n" +
                "    },\n" +
                "    \"credentialSubject\": {\n" +
                "      \"email\": \"adam.smith@example.com\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"iss\": \"did:ion:EiCCN-6ZRhoJwDPu83APpiImOtIPyc1cC72khVqWyBI3Fw\",\n" +
                "  \"jti\": \"did:velocity:v2:0x34ffcc9ccb0b80ecf9c341ba2726ce179f3c7a89:185070538008268:1696\",\n" +
                "  \"iat\": 1653643149,\n" +
                "  \"nbf\": 1653643149\n" +
                "}"
        val MockCredential =
            W3CCredentialJwtV1(
                header = fromJson<JwtHeader>(JWT_HEADER_MOCK),
                payload = fromJson<JwtPayload>(JWT_PAYLOAD_MOCK),
            )
        val CredentialEndpointResponse = CredentialEndpointResponse(credentials = listOf(MockCredential))
        val BaseContext =
            VerificationContext(
                path = emptyList(),
                credentialIssuerMetadata =
                    CredentialIssuerMetadata(
                        iss = "did:issuer:example",
                        credentialIssuer = "https://issuer.example.com",
                    ),
            )
    }
}
