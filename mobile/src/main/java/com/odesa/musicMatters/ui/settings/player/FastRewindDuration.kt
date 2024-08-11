package com.odesa.musicMatters.ui.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.runtime.Composable
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsSliderTile
import kotlin.math.roundToInt

@Composable
fun FastRewindDuration(
    language: Language,
    value: Float,
    onFastRewindDurationChange: ( Float ) -> Unit
) {
    SettingsSliderTile(
        value = value,
        range = 3f..60f,
        imageVector = Icons.Filled.FastRewind,
        headlineContentText = language.fastRewindDuration,
        done = language.done,
        reset = language.reset,
        calculateSliderValue = { currentValue ->
            currentValue.roundToInt().toFloat()
        },
        onValueChange = onFastRewindDurationChange,
        onReset = { onFastRewindDurationChange( SettingsDefaults.FAST_REWIND_DURATION.toFloat() ) },
        label = { currentValue -> language.xSecs( currentValue.toString() ) }
    )
}