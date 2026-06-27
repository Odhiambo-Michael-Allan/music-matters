package com.squad.musicmatters.feature.settings.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.feature.settings.R
import com.squad.musicmatters.feature.settings.components.SettingsTileDefaults
import com.squad.musicmatters.core.i8n.R as i8nR


@Composable
fun Community(
    onGoToReddit: () -> Unit,
    onGoToDiscord: () -> Unit,
    onGoToTelegram: () -> Unit,
) {
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = onGoToReddit
    ) {
        ListItem(
            leadingContent = {
                Image(
                    modifier = Modifier.size( 24.dp ),
                    painter = painterResource( id = R.drawable.feature_settings_reddit ),
                    contentDescription = null
                )
            },
            headlineContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_reddit ),
                    fontWeight = FontWeight.ExtraBold
                )
            },
            supportingContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_reddit_community_url ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    ),
                )
            }
        )
    }
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = onGoToDiscord
    ) {
        ListItem(
            leadingContent = {
                Image(
                    modifier = Modifier.size( 24.dp ),
                    painter = painterResource( id = R.drawable.feature_settings_discord ),
                    contentDescription = null
                )
            },
            headlineContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_discord ),
                    fontWeight = FontWeight.ExtraBold
                )
            },
            supportingContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_discord_server_url ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    ),
                )
            }
        )
    }
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = onGoToTelegram
    ) {
        ListItem(
            leadingContent = {
                Image(
                    modifier = Modifier.size( 24.dp ),
                    painter = painterResource( id = R.drawable.feature_settings_telegram ),
                    contentDescription = null
                )
            },
            headlineContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_telegram ),
                    fontWeight = FontWeight.ExtraBold
                )
            },
            supportingContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_telegram_channel_link ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    ),
                )
            }
        )
    }
}