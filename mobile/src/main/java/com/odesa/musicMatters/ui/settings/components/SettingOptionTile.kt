package com.odesa.musicMatters.ui.settings.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.SupportedLanguages
import com.odesa.musicMatters.ui.components.ScaffoldDialog

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
                Text( text = headlineContentText )
            },
            supportingContent = {
                Text( text = supportingContentText )
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

@Preview( showBackground = true )
@Composable
fun SettingsOptionTilePreview() {
    SettingsOptionTile(
        currentValue = English,
        possibleValues = SupportedLanguages.associateBy( { it }, { it.nativeName } ),
        captions = SupportedLanguages.associateBy( { it }, { it.englishName } ),
        enabled = true ,
        dialogTitle = English.language,
        onValueChange = {},
        leadingContentIcon = Icons.Filled.Language ,
        headlineContentText = English.language,
        supportingContentText = English.nativeName
    )
}