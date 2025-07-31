package com.vnf.wallet.walletsdkkmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vnf.wallet.walletsdkkmp.ui.LinkButton
import io.velocitycareerlabs.velocityexchangeverifiers.api.verifyCredentialEndpointResponse

@Composable
fun App() {
    MaterialTheme {
        var message by remember { mutableStateOf<String?>(null) }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MainMenu(onMessage = { message = it })
        }

        if (message != null) {
            AlertDialog(
                onDismissRequest = { message = null },
                title = { Text("Verification Result") },
                text = { Text(message!!) },
                confirmButton = {
                    TextButton(onClick = { message = null }) {
                        Text("OK")
                    }
                },
            )
        }
    }
}

@Composable
fun MainMenu(onMessage: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        LinkButton(
            text = "Verify Credential Endpoint Response",
            onClick = { onVerifyCredentialEndpointResponse(onMessage) },
        )
    }
}

fun onVerifyCredentialEndpointResponse(onMessage: (String) -> Unit) {
    val errors =
        verifyCredentialEndpointResponse(
            response = Constants.CredentialEndpointResponse,
            context = Constants.BaseContext,
        )

    val message =
        if (errors.isEmpty()) {
            "✅ Verification successful. No errors found."
        } else {
            buildString {
                append("❌ Verification failed:\n")
                errors.forEach { error ->
                    append("- ${error.code}: ${error.message}\n")
                }
            }
        }

    onMessage(message)
}
