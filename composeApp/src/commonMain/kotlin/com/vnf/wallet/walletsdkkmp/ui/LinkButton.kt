/**
 * Created by Michael Avoyan on 29/07/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vnf.wallet.walletsdkkmp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LinkButton(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        color = Color.Blue,
        style = MaterialTheme.typography.bodyLarge,
        modifier =
            Modifier
                .padding(vertical = 8.dp)
                .clickable { onClick() },
    )
}
