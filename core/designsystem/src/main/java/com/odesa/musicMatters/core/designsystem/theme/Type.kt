package com.odesa.musicMatters.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import com.odesa.musicMatters.core.designsystem.R

class MusicMattersFont(
    val name: String,
    val fontFamily: FontFamily
) {
    companion object {
        fun fromValue( fontName: String, fontFamily: FontFamily ) = MusicMattersFont(
            name = fontName,
            fontFamily = fontFamily
        )
    }
}

object SupportedFonts {
    val Inter = MusicMattersFont.fromValue(
        fontName = "Inter",
        fontFamily = FontFamily(
            Font( R.font.inter_regular, FontWeight.Normal ),
            Font( R.font.inter_bold, FontWeight.Bold )
        )
    )

    val Poppins = MusicMattersFont.fromValue(
        fontName = "Poppins",
        fontFamily = FontFamily(
            Font( R.font.roboto_regular, FontWeight.Normal ),
            Font( R.font.roboto_bold, FontWeight.Bold )
        )
    )

    val DMSans = MusicMattersFont.fromValue(
        fontName = "DM Sans",
        fontFamily = FontFamily(
            Font( R.font.dmsans_regular, FontWeight.Normal ),
            Font( R.font.dmsans_bold, FontWeight.Bold )
        )
    )

    val Roboto = MusicMattersFont.fromValue(
        fontName = "Roboto",
        fontFamily = FontFamily(
            Font( R.font.roboto_regular, FontWeight.Normal ),
            Font( R.font.roboto_bold, FontWeight.Bold )
        )
    )

    val ProductSans = MusicMattersFont.fromValue(
        fontName = "Product Sans",
        fontFamily = FontFamily(
            Font( R.font.productsans_regular, FontWeight.Normal ),
            Font( R.font.productsans_bold, FontWeight.Bold )
        )
    )

}

object MusicallyTypography {
    private val defaultFont = SupportedFonts.ProductSans
    val all = mapOf(
        SupportedFonts.Inter.name to SupportedFonts.Inter,
        SupportedFonts.Poppins.name to SupportedFonts.Poppins,
        SupportedFonts.DMSans.name to SupportedFonts.DMSans,
        SupportedFonts.Roboto.name to SupportedFonts.Roboto,
        SupportedFonts.ProductSans.name to SupportedFonts.ProductSans
    )

    fun resolveFont( name: String? ) = all[ name ] ?: defaultFont

    fun toTypography(font: MusicMattersFont, textDirection: TextDirection ): Typography {
        val fontFamily = font.fontFamily

        return Typography().run {
            copy(
                displayLarge = displayLarge.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                displayMedium = displayMedium.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                displaySmall = displaySmall.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                headlineLarge = headlineLarge.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                headlineMedium = headlineMedium.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                headlineSmall = headlineSmall.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                titleLarge = titleLarge.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                titleMedium = titleMedium.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                titleSmall = titleSmall.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                bodyLarge = bodyLarge.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                bodyMedium = bodyMedium.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                bodySmall = bodySmall.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                labelLarge = labelLarge.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                labelMedium = labelMedium.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                ),
                labelSmall = labelSmall.copy(
                    fontFamily = fontFamily,
                    textDirection = textDirection
                )
            )
        }
    }
}