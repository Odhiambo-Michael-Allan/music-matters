package com.squad.musicmatters.ui.settings.player

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.rounded.FastRewind
//import androidx.compose.runtime.Composable
//import com.squad.musicmatters.core.datastore.DefaultPreferences
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.ui.settings.components.SettingsSliderTile
//import kotlin.math.roundToInt
//
//@Composable
//fun FastRewindDuration(
//    language: Language,
//    value: Float,
//    onFastRewindDurationChange: ( Float ) -> Unit
//) {
//    SettingsSliderTile(
//        value = value,
//        range = 3f..60f,
//        imageVector = Icons.Rounded.FastRewind,
//        headlineContentText = language.fastRewindDuration,
//        done = language.done,
//        reset = language.reset,
//        calculateSliderValue = { currentValue ->
//            currentValue.roundToInt().toFloat()
//        },
//        onValueChange = onFastRewindDurationChange,
//        onReset = { onFastRewindDurationChange( DefaultPreferences.FAST_REWIND_DURATION.toFloat() ) },
//        label = { currentValue -> language.xSecs( currentValue.toString() ) }
//    )
//}