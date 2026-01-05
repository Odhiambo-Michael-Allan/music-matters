package com.squad.musicmatters.ui.settings.appearance

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.TextFormat
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersFont
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersTypography
//import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.ui.settings.components.SettingsOptionTile
//
//@Composable
//fun Font(
//    font: MusicMattersFont,
//    language: Language,
//    onFontChange: (MusicMattersFont ) -> Unit
//) {
//    SettingsOptionTile(
//        currentValue = font,
//        possibleValues = MusicMattersTypography.all.values.associateBy( { it }, { it.name } ),
//        enabled = true,
//        dialogTitle = language.font,
//        onValueChange = onFontChange,
//        leadingContentIcon = Icons.Filled.TextFormat,
//        headlineContentText = language.font,
//        supportingContentText = font.name
//    )
//}
//
//
//@Preview( showSystemUi = true )
//@Composable
//fun FontPreview() {
//    Font(
//        font = SupportedFonts.ProductSans,
//        language = English,
//        onFontChange = {}
//    )
//}