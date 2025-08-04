/**
 * Created by Michael Avoyan on 04/08/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package io.velocitycareerlabs.velocityexchangeverifiers

import io.velocitycareerlabs.velocityexchangeverifiers.api.CredentialJwtParserJs as CredentialJwtParserJsExported
import io.velocitycareerlabs.velocityexchangeverifiers.api.VerifiersApiJs as VerifiersApiJsExported

object VelocityExchangeVerifiers {
    val VerifiersApiJs = VerifiersApiJsExported
    val CredentialJwtParserJs = CredentialJwtParserJsExported
}
