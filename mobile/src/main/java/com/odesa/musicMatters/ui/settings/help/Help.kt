package com.odesa.musicMatters.ui.settings.help

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.R
import com.odesa.musicMatters.ui.settings.components.SettingsTileDefaults


@Composable
fun Help(
    goToReddit: () -> Unit,
    goToDiscord: () -> Unit,
    goToTelegram: () -> Unit,
) {
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = goToReddit
    ) {
        ListItem(
            leadingContent = {
                Image(
                    modifier = Modifier.size( 24.dp ),
                    painter = painterResource( id = R.drawable.reddit ),
                    contentDescription = null
                )
            },
            headlineContent = {
                Text( text = stringResource( id = R.string.reddit ) )
            },
            supportingContent = {
                Text( text = stringResource( id = R.string.reddit_community ) )
            }
        )
    }
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = goToDiscord
    ) {
        ListItem(
            leadingContent = {
                Image(
                    modifier = Modifier.size( 24.dp ),
                    painter = painterResource( id = R.drawable.discord ),
                    contentDescription = null
                )
            },
            headlineContent = {
                Text( text = stringResource( id = R.string.discord ) )
            },
            supportingContent = {
                Text( text = stringResource( id = R.string.discord_server_url ) )
            }
        )
    }
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = goToTelegram
    ) {
        ListItem(
            leadingContent = {
                Image(
                    modifier = Modifier.size( 24.dp ),
                    painter = painterResource( id = R.drawable.telegram ),
                    contentDescription = null
                )
            },
            headlineContent = {
                Text( text = stringResource( id = R.string.telegram ) )
            },
            supportingContent = {
                Text( text = stringResource( id = R.string.telegram_channel_link ) )
            }
        )
    }
}