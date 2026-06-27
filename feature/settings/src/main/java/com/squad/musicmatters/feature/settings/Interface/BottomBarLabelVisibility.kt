package com.squad.musicmatters.feature.settings.Interface

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.feature.settings.components.SettingsOptionTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun BottomBarLabelVisibility(
    value: BottomBarLabelVisibility,
    onValueChange: ( BottomBarLabelVisibility ) -> Unit
) {
    SettingsOptionTile(
        currentValue = value,
        possibleValues = BottomBarLabelVisibility.entries.associateBy(
            { it },
            { it.resolveName() }
        ) ,
        enabled = true,
        dialogTitle = stringResource( id = i8nR.string.core_i8n_bottom_bar_label_visibility ),
        onValueChange = onValueChange,
        leadingContentIcon = Icons.Filled.Label,
        headlineContentText = stringResource( id = i8nR.string.core_i8n_bottom_bar_label_visibility ),
        supportingContentText = value.resolveName()
    )
}

@Composable
private fun BottomBarLabelVisibility.resolveName() = when ( this ) {
    BottomBarLabelVisibility.INVISIBLE -> stringResource( id = i8nR.string.core_i8n_invisible )
    BottomBarLabelVisibility.VISIBLE_WHEN_ACTIVE ->
        stringResource( id = i8nR.string.core_i8n_visible_when_active )
    BottomBarLabelVisibility.ALWAYS_VISIBLE ->
        stringResource( id = i8nR.string.core_i8n_always_visible )
}
