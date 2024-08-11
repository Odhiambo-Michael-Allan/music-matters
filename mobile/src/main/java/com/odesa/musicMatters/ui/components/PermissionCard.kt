package com.odesa.musicMatters.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme

@Composable
fun PermissionCard(
    title: String,
    description: String,
    permissionGranted: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.padding( 12.dp )
    ) {
        Column (
            modifier = Modifier.padding( 8.dp ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer( modifier = Modifier.height( 8.dp ) )
            Text(
                text = description
            )
            Spacer( modifier = Modifier.height( 16.dp ) )
            TextButton(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape( 32.dp )
                ),
                onClick = onClick
            ) {
                Icon(
                    modifier = Modifier.padding( 8.dp ),
                    imageVector = Icons.Outlined.SdStorage,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding( 8.dp ),
                    text = stringResource( id = R.string.grant_access )
                )
                if ( permissionGranted ) {
                    Icon(
                        modifier = Modifier.padding( 8.dp ),
                        imageVector = Icons.Filled.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview( showBackground = true )
@Composable
fun PermissionCardPreview() {
    MusicMattersTheme(
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = SettingsDefaults.USE_MATERIAL_YOU
    ) {
        PermissionCard(
            title = stringResource( id = R.string.storage_access ),
            description = stringResource( id = R.string.storage_access_prompt ),
            permissionGranted = true,
            onClick = {}
        )
    }
}