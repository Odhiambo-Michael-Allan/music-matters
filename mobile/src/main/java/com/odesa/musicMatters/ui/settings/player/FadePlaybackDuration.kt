package com.odesa.musicMatters.ui.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsSliderTile
import kotlin.math.roundToInt

@Composable
fun FadePlaybackDuration(
    language: Language,
    value: Float,
    onFadePlaybackDurationChange: ( Float ) -> Unit
) {
    SettingsSliderTile(
        value = value,
        range = 0.5f..6f,
        imageVector = Icons.Filled.GraphicEq,
        headlineContentText = language.fadePlaybackInOut,
        done = language.done,
        reset = language.reset,
        calculateSliderValue = { currentValue ->
            currentValue.times( 2 ).roundToInt().toFloat().div( 2 )
        },
        onValueChange = onFadePlaybackDurationChange,
        onReset = { onFadePlaybackDurationChange( SettingsDefaults.FADE_PLAYBACK_DURATION ) },
        label = { currentValue -> language.xSecs( currentValue.toString() ) }
    )
}

@Preview( showSystemUi = true )
@Composable
fun FadePlaybackDurationPreview() {
    FadePlaybackDuration(
        language = English,
        value = SettingsDefaults.FADE_PLAYBACK_DURATION
    ) {}
}



