package com.squad.musicmatters.feature.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.feature.settings.components.SettingsSliderTile
import kotlin.math.roundToInt
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun FadePlaybackDuration(
    value: Float,
    enabled: Boolean,
    onFadePlaybackDurationChange: ( Float ) -> Unit
) {

    val context = LocalContext.current

    SettingsSliderTile(
        value = value,
        range = 0.5f..6f,
        enabled = enabled,
        imageVector = Icons.Filled.GraphicEq,
        headlineContentText = stringResource( id = i8nR.string.core_i8n_fade_playback_in_out ),
        done = stringResource( id = i8nR.string.core_i8n_done ),
        reset = stringResource( id = i8nR.string.core_i8n_done ),
        calculateSliderValue = { currentValue ->
            currentValue.times( 2 ).roundToInt().toFloat().div( 2 )
        },
        onValueChange = onFadePlaybackDurationChange,
        onReset = { onFadePlaybackDurationChange( DefaultPreferences.FADE_PLAYBACK_DURATION ) },
        label = { currentValue ->
            context.getString(
                i8nR.string.core_i8n_xSeconds,
                currentValue.toString()
            )
        }
    )
}




