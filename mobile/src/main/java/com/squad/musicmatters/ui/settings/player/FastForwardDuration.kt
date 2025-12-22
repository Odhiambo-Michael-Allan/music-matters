package com.squad.musicmatters.ui.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.runtime.Composable
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.ui.settings.components.SettingsSliderTile
import kotlin.math.roundToInt

@Composable
fun FastForwardDuration(
    language: Language,
    value: Float,
    onFastForwardDurationChange: ( Float ) -> Unit
) {
    SettingsSliderTile(
        value = value,
        range = 3f..60f,
        imageVector = Icons.Rounded.FastForward,
        headlineContentText = language.fastForwardDuration,
        done = language.done,
        reset = language.reset,
        calculateSliderValue = { currentValue ->
            currentValue.roundToInt().toFloat()
        },
        onValueChange = onFastForwardDurationChange,
        onReset = { onFastForwardDurationChange( DefaultPreferences.FAST_FORWARD_DURATION.toFloat() ) },
        label = { currentValue -> language.xSecs( currentValue.toString() ) }
    )
}