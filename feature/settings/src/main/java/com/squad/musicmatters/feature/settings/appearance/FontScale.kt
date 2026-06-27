package com.squad.musicmatters.feature.settings.appearance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.ui.dialog.ScaffoldDialog
import com.squad.musicmatters.feature.settings.components.SettingsTileDefaults
import com.squad.musicmatters.core.i8n.R as i8nR

internal val FONT_SCALE_VALUES = setOf( 1f, 1.25f, 1.5f, 1.75f, 2f, 2.25f, 2.5f, 2.75f, 3f )

@Composable
fun FontScale(
    fontScale: Float,
    onFontScaleChange: ( String ) -> Unit
) {

    var fontScaleDialogIsOpen by remember { mutableStateOf( false ) }

    Card(
        colors = SettingsTileDefaults.cardColors(),
        onClick = { fontScaleDialogIsOpen = !fontScaleDialogIsOpen }
    ) {
        ListItem(
            colors = SettingsTileDefaults.listItemColors(),
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.TextIncrease,
                    contentDescription = null
                )
            },
            headlineContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_font_scale ),
                    fontWeight = FontWeight.ExtraBold,
                )
            },
            supportingContent = {
                Text(
                    text = "x$fontScale",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    ),
                )
            }
        )

        if ( fontScaleDialogIsOpen ) {
            var currentInputValue by remember { mutableStateOf( fontScale.toString() ) }

            ScaffoldDialog(
                title = { Text( text = stringResource( id = i8nR.string.core_i8n_font_scale ) ) },
                content = {
                    DialogContent(
                        currentInputValue = currentInputValue,
                        onValueChange = {
                            currentInputValue = it.ifEmpty { "" }
                        },
                        onPresetClicked = {
                            currentInputValue = it.toString()
                        }
                    )
                },
                onDismissRequest = {
                    fontScaleDialogIsOpen = false
                },
                actions = {
                    TextButton(
                        onClick = {
                            onFontScaleChange( DefaultPreferences.FONT_SCALE.toString() )
                            fontScaleDialogIsOpen = false
                        }
                    ) {
                        Text(
                            text = stringResource( id = i8nR.string.core_i8n_reset )
                        )
                    }
                    TextButton(
                        onClick = {
                            fontScaleDialogIsOpen = false
                        }
                    ) {
                        Text(
                            text = stringResource( id = i8nR.string.core_i8n_cancel )
                        )
                    }
                    TextButton(
                        onClick = {
                            onFontScaleChange( currentInputValue )
                            fontScaleDialogIsOpen = false
                        }
                    ) {
                        Text(
                            text = stringResource( id = i8nR.string.core_i8n_done )
                        )
                    }
                }
            )
        }
    }
}

@OptIn( ExperimentalLayoutApi::class )
@Composable
private fun DialogContent(
    currentInputValue: String,
    onValueChange: ( String ) -> Unit,
    onPresetClicked: ( Float ) -> Unit
) {
    Column(
        modifier = Modifier
            .padding( start = 20.dp, end = 20.dp, top = 16.dp )
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                unfocusedIndicatorColor = DividerDefaults.color
            ),
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Number ),
            value = currentInputValue,
            onValueChange = onValueChange
        )
        Box(
            modifier = Modifier.padding( 8.dp )
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    4.dp,
                    Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy( 6.dp ),
                modifier = Modifier.fillMaxWidth()
            ) {
                FONT_SCALE_VALUES.forEach {
                    val active = currentInputValue == it.toString()
                    val xString = "x$it"
                    val shape = RoundedCornerShape( 4.dp )
                    val backgroundColor = when {
                        active -> MaterialTheme.colorScheme.primaryContainer
                        else -> Color.Transparent
                    }
                    val borderColor = when {
                        active -> MaterialTheme.colorScheme.primaryContainer
                        else -> DividerDefaults.color
                    }
                    val contentColor = when {
                        active -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> LocalContentColor.current
                    }
                    Box(
                        modifier = Modifier
                            .clip( shape )
                            .border( 1.dp, borderColor, shape )
                            .background( backgroundColor, shape )
                            .clickable { onPresetClicked( it ) }
                            .padding( 5.dp, 2.dp )
                    ) {
                        Text(
                            text = xString,
                            style = MaterialTheme.typography.labelMedium
                                .copy( color = contentColor )
                        )
                    }
                }
            }
        }
    }
}

@Preview( showSystemUi = true )
@Composable
private fun DialogContentPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = SupportedFonts.ProductSans.name,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        DialogContent(
            currentInputValue = "1.0",
            onValueChange = {},
            onPresetClicked = {}
        )
    }

}