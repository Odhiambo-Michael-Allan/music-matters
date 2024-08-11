package com.odesa.musicMatters.ui.settings.components

import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable

@Composable
fun SettingsSwitchTile(
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    value: Boolean,
    onChange: ( Boolean ) -> Unit
) {
    Card (
        colors = SettingsTileDefaults.cardColors(),
        onClick = { onChange( !value ) }
    ) {
        ListItem (
            colors = SettingsTileDefaults.listItemColors(),
            leadingContent = { icon() },
            headlineContent = { title() },
            trailingContent = {
                Switch(
                    checked = value,
                    onCheckedChange = { onChange( !value ) }
                )
            }
        )
    }
}
