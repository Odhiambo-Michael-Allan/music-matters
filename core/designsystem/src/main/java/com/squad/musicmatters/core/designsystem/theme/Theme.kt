package com.squad.musicmatters.core.designsystem.theme

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Density
import com.squad.musicmatters.core.model.ThemeMode

const val contentScale = 1.0f



enum class ColorSchemeMode {
    LIGHT,
    DARK,
    BLACK
}

@Composable
fun MusicMattersTheme(
    themeMode: ThemeMode,
    primaryColorName: String,
    fontName: String,
    fontScale: Float,
    useMaterialYou: Boolean,
    content: @Composable () -> Unit
) {

    val colorSchemeMode = themeMode.toColorSchemeMode( isSystemInDarkTheme() )
    val colorScheme = if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && useMaterialYou ) {
        val context = LocalContext.current
        when ( colorSchemeMode ) {
            ColorSchemeMode.LIGHT -> dynamicLightColorScheme( context )
            ColorSchemeMode.DARK -> dynamicDarkColorScheme( context )
            ColorSchemeMode.BLACK -> ThemeColorSchemes.toBlackColorScheme(
                dynamicDarkColorScheme( context )
            )
        }
    } else {
        val primaryColor = ThemeColors.resolvePrimaryColorName( primaryColorName )
        when ( colorSchemeMode ) {
            ColorSchemeMode.LIGHT -> ThemeColorSchemes.createLightColorScheme( primaryColor )
            ColorSchemeMode.DARK -> ThemeColorSchemes.createDarkColorScheme( primaryColor )
            ColorSchemeMode.BLACK -> ThemeColorSchemes.createBlackColorScheme( primaryColor )
        }
    }

    val view = LocalView.current
    if ( !view.isInEditMode ) {
        SideEffect {
            val window = ( view.context as Activity ).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
        }
    }

    val typography = MusicMattersTypography.toTypography(
        MusicMattersTypography.resolveFont( fontName ),
        TextDirection.Ltr
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = {
            CompositionLocalProvider(
                LocalDensity provides Density(
                    LocalDensity.current.density * contentScale,
                    LocalDensity.current.fontScale * fontScale

                )
            )
            {
                content()
            }
        }
    )
}

//fun ThemeMode.resolveName( language: Language) = when ( this ) {
//    ThemeMode.FOLLOW_SYSTEM -> language.systemLightDark
//    ThemeMode.LIGHT -> language.light
//    ThemeMode.DARK -> language.dark
//    ThemeMode.BLACK -> language.black
//}

fun ThemeMode.toColorSchemeMode(isSystemInDarkTheme: Boolean ) = when ( this ) {
    ThemeMode.FOLLOW_SYSTEM -> if ( isSystemInDarkTheme ) ColorSchemeMode.DARK else ColorSchemeMode.LIGHT
    ThemeMode.LIGHT -> ColorSchemeMode.LIGHT
    ThemeMode.DARK -> ColorSchemeMode.DARK
    ThemeMode.BLACK -> ColorSchemeMode.BLACK
}

fun ThemeMode.isLight(context: Context ) =
    context.resources.configuration.uiMode.let {
        val isSystemInDarkTheme =
            ( it and Configuration.UI_MODE_NIGHT_MASK ) == Configuration.UI_MODE_NIGHT_YES
        toColorSchemeMode( isSystemInDarkTheme ).isLight()
    }

fun ColorSchemeMode.isLight() = this == ColorSchemeMode.LIGHT
