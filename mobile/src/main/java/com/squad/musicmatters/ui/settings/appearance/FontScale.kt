package com.squad.musicmatters.ui.settings.appearance
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.ExperimentalLayoutApi
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.TextIncrease
//import androidx.compose.material3.Card
//import androidx.compose.material3.DividerDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.ListItem
//import androidx.compose.material3.LocalContentColor
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.ui.components.ScaffoldDialog
//import com.squad.musicmatters.ui.settings.components.SettingsTileDefaults
//
//@Composable
//fun FontScale(
//    language: Language,
//    fontScale: Float,
//    onFontScaleChange: ( String ) -> Unit
//) {
//
//    var fontScaleDialogIsOpen by remember { mutableStateOf( false ) }
//
//    Card(
//        colors = SettingsTileDefaults.cardColors(),
//        onClick = { fontScaleDialogIsOpen = !fontScaleDialogIsOpen }
//    ) {
//        ListItem(
//            colors = SettingsTileDefaults.listItemColors(),
//            leadingContent = {
//                Icon(
//                    imageVector = Icons.Filled.TextIncrease,
//                    contentDescription = null
//                )
//            },
//            headlineContent = {
//                Text( text = language.fontScale )
//            },
//            supportingContent = {
//                Text( text = "x$fontScale" )
//            }
//        )
//
//        if ( fontScaleDialogIsOpen ) {
//            var currentInputValue by remember { mutableStateOf( fontScale.toString() ) }
//
//            ScaffoldDialog(
//                title = { Text( text = language.fontScale ) },
//                content = {
//                    DialogContent(
//                        currentInputValue = currentInputValue,
//                        onValueChange = {
//                            currentInputValue = it.ifEmpty { "" }
//                        },
//                        onPresetClicked = {
//                            currentInputValue = it.toString()
//                        }
//                    )
//                },
//                onDismissRequest = {
//                    fontScaleDialogIsOpen = false
//                },
//                actions = {
//                    TextButton(
//                        onClick = {
//                            onFontScaleChange( SettingsDefaults.FONT_SCALE.toString() )
//                            fontScaleDialogIsOpen = false
//                        }
//                    ) {
//                        Text( text = language.reset )
//                    }
//                    TextButton(
//                        onClick = {
//                            fontScaleDialogIsOpen = false
//                        }
//                    ) {
//                        Text( text = language.cancel )
//                    }
//                    TextButton(
//                        onClick = {
//                            onFontScaleChange( currentInputValue )
//                            fontScaleDialogIsOpen = false
//                        }
//                    ) {
//                        Text( text = language.done )
//                    }
//                }
//            )
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun FontScalePreview() {
//    FontScale(
//        language = English,
//        fontScale = 1.0f
//    ) {}
//}
//
//@OptIn( ExperimentalLayoutApi::class )
//@Composable
//fun DialogContent(
//    currentInputValue: String,
//    onValueChange: ( String ) -> Unit,
//    onPresetClicked: ( Float ) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .padding( start = 20.dp, end = 20.dp, top = 16.dp )
//    ) {
//        OutlinedTextField(
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true,
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color.Transparent,
//                unfocusedContainerColor = Color.Transparent,
//                errorContainerColor = Color.Transparent,
//                unfocusedIndicatorColor = DividerDefaults.color
//            ),
//            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Number ),
//            value = currentInputValue,
//            onValueChange = onValueChange
//        )
//        Box(
//            modifier = Modifier.padding( 8.dp )
//        ) {
//            FlowRow(
//                horizontalArrangement = Arrangement.spacedBy(
//                    4.dp,
//                    Alignment.CenterHorizontally
//                ),
//                verticalArrangement = Arrangement.spacedBy( 6.dp ),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                scalingPresets.map {
//                    val active = currentInputValue == it.toString()
//                    val xString = "x$it"
//                    val shape = RoundedCornerShape( 4.dp )
//                    val backgroundColor = when {
//                        active -> MaterialTheme.colorScheme.primaryContainer
//                        else -> Color.Transparent
//                    }
//                    val borderColor = when {
//                        active -> MaterialTheme.colorScheme.primaryContainer
//                        else -> DividerDefaults.color
//                    }
//                    val contentColor = when {
//                        active -> MaterialTheme.colorScheme.onPrimaryContainer
//                        else -> LocalContentColor.current
//                    }
//                    Box(
//                        modifier = Modifier
//                            .clip( shape )
//                            .border( 1.dp, borderColor, shape )
//                            .background( backgroundColor, shape )
//                            .clickable { onPresetClicked( it ) }
//                            .padding( 5.dp, 2.dp )
//                    ) {
//                        Text(
//                            text = xString,
//                            style = MaterialTheme.typography.labelMedium
//                                .copy( color = contentColor )
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun DialogContentPreview() {
//    DialogContent(
//        currentInputValue = "1.0",
//        onValueChange = {},
//        onPresetClicked = {}
//    )
//}