package com.odesa.musicMatters.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.ui.components.ScaffoldDialog
import com.odesa.musicMatters.utils.RangeUtils
import kotlin.math.roundToInt

@Composable
fun SettingsSliderTile(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    imageVector: ImageVector,
    headlineContentText: String,
    done: String,
    reset: String,
    calculateSliderValue: ( Float ) -> Float,
    onValueChange: ( Float ) -> Unit,
    onReset: () -> Unit,
    label: ( Float ) -> String
) {

    var dialogIsOpen by remember { mutableStateOf( false ) }

    Card (
        colors = SettingsTileDefaults.cardColors(),
        onClick = { dialogIsOpen = !dialogIsOpen }
    ) {
        ListItem (
            colors = SettingsTileDefaults.listItemColors(),
            leadingContent = {
                Icon( imageVector = imageVector, contentDescription = null )
            },
            headlineContent = {
                Text( text = headlineContentText )
            },
            supportingContent = { Text( text = "${value}s" ) }
        )
    }
    if ( dialogIsOpen ) {

        var currentSliderValue by remember{ mutableFloatStateOf( value ) }

        ScaffoldDialog(
            title = { Text( text = headlineContentText ) },
            content = {
                Column (
                    modifier = Modifier
                        .padding( top = 16.dp )
                ) {
                    CustomSlider(
                        initialValue = value,
                        range = range,
                        calculateSliderValue = calculateSliderValue,
                        onSliderValueChange = { newValue -> currentSliderValue = newValue }
                    )
                    Spacer( modifier = Modifier.height( 8.dp ) )
                    ProvideTextStyle( value = MaterialTheme.typography.labelMedium ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp, 0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text( text = "${range.start}s" )
                            Text( text = label( currentSliderValue ) )
                            Text( text = label( range.endInclusive ) )
                        }
                    }
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        onReset()
                        dialogIsOpen = false
                    }
                ) {
                    Text( text = reset )
                }
                TextButton(
                    onClick = {
                        onValueChange( currentSliderValue )
                        dialogIsOpen = false
                    }
                ) {
                    Text( text = done )
                }
            },
            onDismissRequest = { dialogIsOpen = false }
        )
    }
}

@Composable
fun CustomSlider(
    initialValue: Float,
    range: ClosedFloatingPointRange<Float>,
    calculateSliderValue: ( Float ) -> Float = { it },
    onSliderValueChange: ( Float ) -> Unit
) {
    var ratio by remember {
        mutableFloatStateOf( RangeUtils.calculateRatioFromValue( initialValue, range ) )
    }
    BoxWithConstraints (
        modifier = Modifier.padding( 20.dp, 0.dp )
    ) {
        val height = 12.dp
        val shape = RoundedCornerShape( height.div( 2 ) )
        var xPositionClicked = 0f

        Box(
            modifier = Modifier
                .background( MaterialTheme.colorScheme.surfaceVariant, shape )
                .fillMaxWidth()
                .height( height )
                .pointerInput( Unit ) {
                    detectTapGestures(
                        onTap = {
                            xPositionClicked = it.x
                            val maxWidthInPixels = maxWidth.toPx()
                            val newRatio = ( xPositionClicked / maxWidthInPixels )
                                .coerceIn( 0f..1f )
                            val newValue = calculateSliderValue(
                                RangeUtils.calculateValueFromRatio(
                                    newRatio,
                                    range
                                )
                            )
                            onSliderValueChange( newValue )
                            ratio = RangeUtils.calculateRatioFromValue( newValue, range )
                        }
                    )
                }
                .pointerInput( Unit ) {
                    detectHorizontalDragGestures(
                        onDragStart = { xPositionClicked = it.x },
                        onHorizontalDrag = { pointer, dragAmount ->
                            pointer.consume()
                            xPositionClicked += dragAmount
                            val maxWidthInPixels = maxWidth.toPx()
                            val newRatio = ( xPositionClicked / maxWidthInPixels )
                                .coerceIn( 0f..1f )
                            val newValue = calculateSliderValue(
                                RangeUtils.calculateValueFromRatio(
                                    newRatio,
                                    range
                                )
                            )
                            onSliderValueChange( newValue )
                            ratio = RangeUtils.calculateRatioFromValue( newValue, range )
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .background( MaterialTheme.colorScheme.primary, shape )
                    .fillMaxWidth( ratio )
                    .height( height )
            )
        }
    }
}

@Preview( showBackground = true )
@Composable
fun CustomSliderPreview() {
    CustomSlider(
        SettingsDefaults.FADE_PLAYBACK_DURATION,
        0.5f..6f,
        calculateSliderValue = { value ->
            value.times( 2 ).roundToInt().toFloat().div( 2 ) },
    ) {}
}