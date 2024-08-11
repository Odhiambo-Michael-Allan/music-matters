package com.odesa.musicMatters.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogOption(
    selected: Boolean,
    title: String,
    caption: String? = null,
    onClick: () -> Unit,
) {
    Card(
        colors = SettingsTileDefaults.cardColors(),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding( 12.dp ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            Spacer( modifier = Modifier.width( 8.dp ) )
            Column {
                Text( text = title )
                caption?.let {
                    Spacer( modifier = Modifier.height( 2.dp ) )
                    Text(
                        caption,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LocalContentColor.current.copy( alpha = 0.7f )
                        )
                    )
                }
            }
        }
    }
}

//@Preview( showBackground = true )
//@Composable
//fun DialogOptionPreview() {
//    MusicallyTheme {
//        DialogOption(
//            selected = true,
//            title = "English",
//            caption = "English",
//            onClick = {}
//        )
//    }
//}