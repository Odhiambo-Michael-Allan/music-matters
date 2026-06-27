package com.squad.musicmatters.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.squad.musicMatters.core.designsystem.R

object MusicMattersTypography {

    val all = mapOf(
        SupportedFonts.Inter.name to SupportedFonts.Inter,
        SupportedFonts.Poppins.name to SupportedFonts.Poppins,
        SupportedFonts.DMSans.name to SupportedFonts.DMSans,
        SupportedFonts.Roboto.name to SupportedFonts.Roboto,
        SupportedFonts.ProductSans.name to SupportedFonts.ProductSans,
        SupportedFonts.SystemDefault.name to SupportedFonts.SystemDefault
    )

    fun resolveFont( name: String? ) = all[ name ] ?: MusicMattersFont.UNSPECIFIED_FONT

    fun toTypography(
        font: MusicMattersFont? = null,
    ): Typography =
        Typography(
            displayLarge = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.25).sp,
                fontFamily = font?.family,
            ),
            displayMedium = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 45.sp,
                lineHeight = 52.sp,
                letterSpacing = 0.sp,
                fontFamily = font?.family,
            ),
            displaySmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.sp,
                fontFamily = font?.family,
            ),
            headlineLarge = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = 0.sp,
                fontFamily = font?.family,
            ),
            headlineMedium = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                letterSpacing = 0.sp,
                fontFamily = font?.family,
            ),
            headlineSmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Bottom,
                    trim = LineHeightStyle.Trim.None,
                ),
                fontFamily = font?.family,
            ),
            titleLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Bottom,
                    trim = LineHeightStyle.Trim.LastLineBottom,
                ),
                fontFamily = font?.family,
            ),
            titleMedium = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.1.sp,
                fontFamily = font?.family,
            ),
            titleSmall = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
                fontFamily = font?.family,
            ),
            // Default text style
            bodyLarge = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None,
                ),
                fontFamily = font?.family,
            ),
            bodyMedium = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp,
                fontFamily = font?.family,
            ),
            bodySmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp,
                fontFamily = font?.family,
            ),
            // Used for Button
            labelLarge = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.LastLineBottom,
                ),
                fontFamily = font?.family,
            ),
            // Used for Navigation items
            labelMedium = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.LastLineBottom,
                ),
                fontFamily = font?.family,
            ),
            // Used for Tag
            labelSmall = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.LastLineBottom,
                ),
                fontFamily = font?.family,
            ),
        )
}

object SupportedFonts {
    val Inter = MusicMattersFont.fromValue(
        fontName = "Inter",
        fontFamily = FontFamily(
            Font(R.font.inter_regular, FontWeight.Normal ),
        )
    )

    val Poppins = MusicMattersFont.fromValue(
        fontName = "Poppins",
        fontFamily = FontFamily(
            Font( R.font.roboto_regular, FontWeight.Normal ),
        )
    )

    val DMSans = MusicMattersFont.fromValue(
        fontName = "DM Sans",
        fontFamily = FontFamily(
            Font( R.font.dmsans_regular, FontWeight.Normal ),
        )
    )

    val Roboto = MusicMattersFont.fromValue(
        fontName = "Roboto",
        fontFamily = FontFamily(
            Font( R.font.roboto_regular, FontWeight.Normal ),
        )
    )

    val ProductSans = MusicMattersFont.fromValue(
        fontName = "Product Sans",
        fontFamily = FontFamily(
            Font( R.font.product_sans_regular, FontWeight.Normal ),
        )
    )

    val SystemDefault = MusicMattersFont.UNSPECIFIED_FONT

}

class MusicMattersFont private constructor(
    val name: String,
    val family: FontFamily?
) {
    companion object {
        fun fromValue( fontName: String, fontFamily: FontFamily ) = MusicMattersFont(
            name = fontName,
            family = fontFamily
        )

        val UNSPECIFIED_FONT = MusicMattersFont(
            name = SYSTEM_DEFAULT_FONT_NAME,
            family = null,
        )

    }
}

const val SYSTEM_DEFAULT_FONT_NAME = "System-Default"