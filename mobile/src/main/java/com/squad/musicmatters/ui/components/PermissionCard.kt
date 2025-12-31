package com.squad.musicmatters.ui.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme

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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer( modifier = Modifier.height( 8.dp ) )
            Text(
                text = description,
                textAlign = TextAlign.Center,
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
                    text = stringResource( id = R.string.grant_access ),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding( 8.dp ),
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

@DevicePreviews
@Composable
fun PermissionCardPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        PermissionCard(
            title = stringResource( id = R.string.storage_access ),
            description = stringResource( id = R.string.storage_access_prompt ),
            permissionGranted = true,
            onClick = {}
        )
    }
}