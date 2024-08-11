package com.odesa.musicMatters.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.SupportedLanguages
import com.odesa.musicMatters.ui.settings.components.DialogOption


@Composable
fun ScaffoldDialog(
    title: @Composable () -> Unit,
    topBar: ( @Composable () -> Unit )? = null,
    content: @Composable () -> Unit,
    actions: ( @Composable RowScope.() -> Unit )? = null,
    onDismissRequest: () -> Unit,
) {

    val configuration = LocalConfiguration.current

    Dialog( onDismissRequest = onDismissRequest ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape( 8.dp ),
            modifier = Modifier.run {
                val maxHeight = ( configuration.screenHeightDp * 0.9f ).dp
                requiredHeightIn( max = maxHeight )
            }
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)
                            .weight(1f)
                    ) {
                        ProvideTextStyle(
                            value = MaterialTheme.typography.bodyLarge.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            title()
                        }
                    }
                }
                Divider( thickness = 0.5.dp )
                topBar?.let { it() }
                Box {
                    content()
                }
                actions?.let {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp, 0.dp)
                    ) {
                        actions()
                    }
                }
            }
        }
    }
}

@Preview( showBackground = true )
@Composable
fun ScaffoldDialogPreview() {
    ScaffoldDialog(
        title = { Text( text = "Language" ) },
        topBar = {
            Box(
                modifier = Modifier
                    .padding( start = 24.dp, end = 24.dp, top = 16.dp )
                    .alpha( 0.7f )
            ) {
                ProvideTextStyle( value = MaterialTheme.typography.labelMedium ) {
                    Text( text = English.selectAtleast2orAtmost5Tabs )
                }
            }
        },
        content = {
            val values = SupportedLanguages

            LazyColumn {
                items( values ) {
                    DialogOption(
                        selected = false,
                        title = it.nativeName,
                        caption = it.englishName,
                        onClick = {}
                    )
                }
            }
        },
        onDismissRequest = {}
    )
}


