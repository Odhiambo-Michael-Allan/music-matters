package com.squad.musicmatters.feature.settings.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.ui.dialog.ScaffoldDialog

@Composable
fun <T> SettingsOptionTile(
    currentValue: T,
    possibleValues: Map<T, String>,
    captions: Map<T, String>? = null,
    enabled: Boolean,
    dialogTitle: String,
    onValueChange: ( T ) -> Unit,
    leadingContentIcon: ImageVector,
    headlineContentText: String,
    supportingContentText: String
) {

    var dialogIsOpen by remember { mutableStateOf( false ) }

    Card (
        enabled = enabled,
        colors = SettingsTileDefaults.cardColors(),
        onClick = { dialogIsOpen = !dialogIsOpen }
    ) {
        ListItem (
            colors = SettingsTileDefaults.listItemColors( enabled = enabled ),
            leadingContent = {
                Icon(
                    imageVector = leadingContentIcon,
                    contentDescription = null
                )
            },
            headlineContent = {
                Text(
                    text = headlineContentText,
                    fontWeight = FontWeight.ExtraBold,
                )
            },
            supportingContent = {
                Text(
                    text = supportingContentText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    ),
                )
            }
        )
    }
    if ( dialogIsOpen ) {
        ScaffoldDialog(
            title = { Text( text = dialogTitle ) },
            content = {
                LazyColumn {
                    items( possibleValues.keys.toList() ) {
                        DialogOption(
                            selected = it == currentValue,
                            title = possibleValues[it]!!,
                            caption = captions?.get( it )
                        ) {
                            onValueChange( it )
                            dialogIsOpen = false
                        }
                    }
                }
            },
            onDismissRequest = { dialogIsOpen = false }
        )
    }
}
