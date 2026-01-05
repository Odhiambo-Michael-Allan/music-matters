package com.squad.musicmatters.ui.settings.Interface

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Label
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import com.squad.musicmatters.core.i8n.Chinese
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.BottomBarLabelVisibility
//import com.squad.musicmatters.ui.settings.components.SettingsOptionTile
//
//@Composable
//fun BottomBarLabelVisibility(
//    value: BottomBarLabelVisibility,
//    language: Language,
//    onValueChange: (BottomBarLabelVisibility) -> Unit
//) {
//    SettingsOptionTile(
//        currentValue = value,
//        possibleValues = BottomBarLabelVisibility.entries.associateBy( { it }, { it.resolveName( language ) } ) ,
//        enabled = true,
//        dialogTitle = language.bottomBarLabelVisibility,
//        onValueChange = onValueChange,
//        leadingContentIcon = Icons.Filled.Label,
//        headlineContentText = language.bottomBarLabelVisibility,
//        supportingContentText = value.resolveName( language )
//    )
//}
//
//fun BottomBarLabelVisibility.resolveName(language: Language) = when ( this ) {
//    BottomBarLabelVisibility.INVISIBLE -> language.invisible
//    BottomBarLabelVisibility.VISIBLE_WHEN_ACTIVE -> language.visibleWhenActive
//    BottomBarLabelVisibility.ALWAYS_VISIBLE -> language.alwaysVisible
//}
//
//@Preview( showBackground = true )
//@Composable
//fun BottomBarLabelVisibilityPreview() {
//    BottomBarLabelVisibility(
//        value = BottomBarLabelVisibility.ALWAYS_VISIBLE,
//        language = Chinese,
//        onValueChange = {}
//    )
//}