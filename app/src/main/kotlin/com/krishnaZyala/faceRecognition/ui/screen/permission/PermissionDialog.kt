package com.krishnaZyala.faceRecognition.ui.screen.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PermissionDialog(isDenied: Boolean, provider: PermissionProvider, modifier: Modifier = Modifier, onClick: (Boolean) -> Unit) = PermissionDialog(
    heading = provider.title,
    body = provider.body(isDenied),
    trueBtnText = provider.trueBtnText,
    modifier = modifier,
    falseBtnText = provider.falseBtnText,
    properties = provider.properties,
    onClick = onClick
)

@Composable
fun PermissionDialog(
    heading: String,
    body: String,
    trueBtnText: String,
    modifier: Modifier = Modifier,
    falseBtnText: String? = null,
    properties: DialogProperties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    onClick: (Boolean) -> Unit
) = Dialog({ onClick(false) }, properties) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(10.dp)) {
            Text(
                text = heading, maxLines = 2, textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
            )
            Text(text = body, textAlign = TextAlign.Start, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(12.dp, 10.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                if (!falseBtnText.isNullOrEmpty()) Text(text = falseBtnText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 10.dp)
                        .weight(1f)
                        .clickable { onClick(false) })
                if (trueBtnText.isNotEmpty()) Text(text = trueBtnText,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 10.dp)
                        .weight(1f)
                        .clickable { onClick(true) })
            }
        }
    }
}