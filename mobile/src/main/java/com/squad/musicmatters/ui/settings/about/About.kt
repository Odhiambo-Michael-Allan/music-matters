package com.squad.musicmatters.ui.settings.about

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AdsClick
//import androidx.compose.material.icons.filled.Face
//import androidx.compose.material3.Card
//import androidx.compose.material3.Icon
//import androidx.compose.material3.ListItem
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextOverflow
//import com.squad.musicmatters.R
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.ui.settings.components.SettingsTileDefaults
//
//@Composable
//fun About(
//    language: Language,
//    goToGithubProfile: () -> Unit,
//    goToAppGithubRepository: () -> Unit,
//) {
//    Card(
//        colors = SettingsTileDefaults.cardColors(),
//        onClick = goToGithubProfile
//    ) {
//        ListItem(
//            leadingContent = {
//                Icon(
//                    imageVector = Icons.Default.Face,
//                    contentDescription = null
//                )
//            },
//            headlineContent = {
//                Text( text = language.madeByX( stringResource( id = R.string.my_name ) ) )
//            },
//            supportingContent = {
//                Text(
//                    text = stringResource( id = R.string.github_profile_url ),
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        )
//    }
//    Card(
//        colors = SettingsTileDefaults.cardColors(),
//        onClick = goToAppGithubRepository
//    ) {
//        ListItem(
//            leadingContent = {
//                Icon(
//                    imageVector = Icons.Default.AdsClick,
//                    contentDescription = null
//                )
//            },
//            headlineContent = {
//                Text( text = stringResource( id = R.string.github ) )
//            },
//            supportingContent = {
//                Text(
//                    text = stringResource( id = R.string.app_github_repo ),
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        )
//    }
//}