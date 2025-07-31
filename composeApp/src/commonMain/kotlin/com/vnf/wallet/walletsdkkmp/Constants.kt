/**
 * Created by Michael Avoyan on 29/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vnf.wallet.walletsdkkmp

import io.velocitycareerlabs.velocityexchangeverifiers.api.CredentialJwtParser
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.CredentialIssuerMetadata
import io.velocitycareerlabs.velocityexchangeverifiers.api.types.VerificationContext

class Constants {
    companion object {
        @Suppress("ktlint:standard:max-line-length")
        const val JWT_HEADER_MOCK =
            """{
              "typ": "JWT",
              "kid": "did:velocity:v2:0x34ffcc9ccb0b80ecf9c341ba2726ce179f3c7a89:185070538008268:1696#key-1",
              "alg": "ES256K"
            }"""

        @Suppress("ktlint:standard:max-line-length")
        const val JWT_PAYLOAD_MOCK =
            """{
              "vc": {
                "@context": ["https://www.w3.org/2018/credentials/v1"],
                "type": ["EmailV1.0", "VerifiableCredential"],
                "credentialStatus": {
                  "type": "VelocityRevocationListJan2021",
                  "id": "ethereum:0x1C29461C7480d1d8570df7c0A4F314D0bE8cD5Bf/getRevokedStatus?address=0xeC529b7bbC84821D2e71001c9F4fed80dd44b655&listId=58998093281042&index=2695",
                  "statusListIndex": 2695,
                  "statusListCredential": "ethereum:0x1C29461C7480d1d8570df7c0A4F314D0bE8cD5Bf/getRevokedStatus?address=0xeC529b7bbC84821D2e71001c9F4fed80dd44b655&listId=58998093281042",
                  "linkCodeCommit": "EiAPe/02cKuCP8qc4xhgr31wra7jT3ogBr9/yD7JQMwIeg=="
                },
                "contentHash": {
                  "type": "VelocityContentHash2020",
                  "value": "89d4dcc86d540c6eea39d178eedc30213e782f251e03b76f4273106808dd0d4f"
                },
                "credentialSchema": {
                  "id": "https://stagingregistrar.velocitynetwork.foundation/schemas/email-v1.0.schema.json",
                  "type": "JsonSchemaValidator2018"
                },
                "credentialSubject": {
                  "email": "adam.smith@example.com"
                }
              },
              "iss": "did:issuer:example",
              "sub": "did:jwk",
              "jti": "did:velocity:v2:0x34ffcc9ccb0b80ecf9c341ba2726ce179f3c7a89:185070538008268:1696",
              "iat": 1653643149,
              "nbf": 1653643149
            }"""

        const val CREDENTIALS_MOCK = """
            {
              "credentials": [
                {
                  "header": $JWT_HEADER_MOCK,
                  "payload": $JWT_PAYLOAD_MOCK
                }
              ],
              "claims": {}
            }
        """

        val CredentialEndpointResponse =
            CredentialJwtParser.parseCredentialEndpointResponse(CREDENTIALS_MOCK)

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
