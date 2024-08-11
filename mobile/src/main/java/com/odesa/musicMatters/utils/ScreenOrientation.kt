package com.odesa.musicMatters.utils

import android.content.res.Configuration
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.unit.Dp

enum class ScreenOrientation {
    POTRAIT,
    LANDSCAPE;

    val isPotrait: Boolean
        get() = this == POTRAIT
    val isLandscape: Boolean
        get() = this == LANDSCAPE

    companion object {
        fun fromConfiguration( configuration: Configuration ) = when ( configuration.orientation ) {
            Configuration.ORIENTATION_LANDSCAPE -> LANDSCAPE
            else -> POTRAIT
        }

        fun fromConstraints( constraints: BoxWithConstraintsScope ) =
            fromDimension( constraints.maxHeight, constraints.maxWidth )

        fun fromDimension( height: Dp, width: Dp ) = when {
            width.value > height.value -> LANDSCAPE
            else -> POTRAIT
        }
    }
}