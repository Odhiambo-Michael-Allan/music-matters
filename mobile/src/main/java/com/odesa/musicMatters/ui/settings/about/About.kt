package com.odesa.musicMatters.ui.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsTileDefaults

@Composable
fun About(
    language: Language,
    goToGithubProfile: () -> Unit,
    goToAppGithubRepository: () -> Unit,
) {
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = goToGithubProfile
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null
                )
            },
            headlineContent = {
                Text( text = language.madeByX( stringResource( id = R.string.my_name ) ) )
            },
            supportingContent = {
                Text( text = stringResource( id = R.string.github_profile_url ) )
            }
        )
    }
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = goToAppGithubRepository
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.AdsClick,
                    contentDescription = null
                )
            },
            headlineContent = {
                Text( text = stringResource( id = R.string.github ) )
            },
            supportingContent = {
                Text( text = stringResource( id = R.string.app_github_repo ) )
            }
        )
    }
}