package com.squad.musicmatters.ui.settings.appearance

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Colorize
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.ui.settings.components.SettingsOptionTile
//
//@OptIn( ExperimentalMaterial3Api::class )
//@Composable
//fun PrimaryColor(
//    primaryColor: String,
//    language: Language,
//    onPrimaryColorChange: ( String ) -> Unit,
//    useMaterialYou: Boolean
//) {
//    SettingsOptionTile(
//        currentValue = primaryColor,
//        possibleValues = PrimaryThemeColors.entries.toSet().associateBy( { it.name }, { it.name } ),
//        enabled = !useMaterialYou,
//        dialogTitle = language.primaryColor,
//        onValueChange = onPrimaryColorChange,
//        leadingContentIcon = Icons.Filled.Colorize,
//        headlineContentText = language.primaryColor,
//        supportingContentText = primaryColor
//    )
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun PrimaryColorPreview() {
//    PrimaryColor(
//        PrimaryThemeColors.Blue.name,
//        language = English,
//        onPrimaryColorChange = {},
//        useMaterialYou = true
//    )
//}