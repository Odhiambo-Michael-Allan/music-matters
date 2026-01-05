package com.squad.musicmatters.ui.settings.appearance

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Face
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.ui.settings.components.SettingsSwitchTile
//
//@Composable
//fun MaterialYou(
//    language: Language,
//    useMaterialYou: Boolean,
//    onUseMaterialYouChange: ( Boolean ) -> Unit
//) {
//    SettingsSwitchTile(
//        icon = {
//            Icon(
//                imageVector = Icons.Filled.Face,
//                contentDescription = null
//            )
//        },
//        title = {
//            Text( text = language.materialYou )
//        },
//        value = useMaterialYou,
//        onChange = onUseMaterialYouChange
//    )
//}
//
//@Preview( showBackground = true )
//@Composable
//fun MaterialYouPreview() {
//    MaterialYou(
//        language = English,
//        useMaterialYou = true,
//        onUseMaterialYouChange = {}
//    )
//}