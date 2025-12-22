package com.squad.musicmatters.ui.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.ui.settings.components.SettingsSliderTile
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
        onReset = { onFadePlaybackDurationChange( DefaultPreferences.FADE_PLAYBACK_DURATION ) },
        label = { currentValue -> language.xSecs( currentValue.toString() ) }
    )
}

@Preview( showSystemUi = true )
@Composable
fun FadePlaybackDurationPreview() {
    FadePlaybackDuration(
        language = English,
        value = 20f
    ) {}
}



