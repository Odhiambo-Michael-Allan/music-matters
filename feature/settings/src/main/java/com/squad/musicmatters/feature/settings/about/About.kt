package com.squad.musicmatters.feature.settings.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.squad.musicmatters.feature.settings.components.SettingsTileDefaults
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun About(
    onGoToGithubProfile: () -> Unit,
    onGoToAppGithubRepository: () -> Unit,
) {
    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = onGoToGithubProfile
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null
                )
            },
            headlineContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_made_by ),
                    fontWeight = FontWeight.ExtraBold,
                )
            },
            supportingContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_github_profile_url ),
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
        onClick = onGoToAppGithubRepository
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.AdsClick,
                    contentDescription = null
                )
            },
            headlineContent = {
                Text( text = stringResource( id = i8nR.string.core_i8n_github ) )
            },
            supportingContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_app_github_repo_url ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}