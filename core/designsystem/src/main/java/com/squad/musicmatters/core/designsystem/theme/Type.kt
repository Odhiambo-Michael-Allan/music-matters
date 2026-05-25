package com.squad.musicmatters.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.squad.musicMatters.core.designsystem.R

val ProductSans = FontFamily(
    Font( R.font.product_sans_regular ),
)

val MusicMattersTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        fontFamily = ProductSans
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        fontFamily = ProductSans
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        fontFamily = ProductSans
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        fontFamily = ProductSans
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        fontFamily = ProductSans
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
        fontFamily = ProductSans
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
        fontFamily = ProductSans
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        fontFamily = ProductSans
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontFamily = ProductSans
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
        fontFamily = ProductSans
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        fontFamily = ProductSans
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        fontFamily = ProductSans
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
        fontFamily = ProductSans
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
        fontFamily = ProductSans
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
        fontFamily = ProductSans
    ),
)

//class MusicMattersFont(
//    val name: String,
//    val fontFamily: FontFamily
//) {
//    companion object {
//        fun fromValue( fontName: String, fontFamily: FontFamily ) = MusicMattersFont(
//            name = fontName,
//            fontFamily = fontFamily
//        )
//
//        fun getSupportedFontFromName( fontName: String ): MusicMattersFont = when( fontName ) {
//            SupportedFonts.DMSans.name -> SupportedFonts.DMSans
//            SupportedFonts.Inter.name -> SupportedFonts.Inter
//            SupportedFonts.Poppins.name -> SupportedFonts.Poppins
//            SupportedFonts.Roboto.name -> SupportedFonts.Roboto
//            SupportedFonts.ProductSans.name -> SupportedFonts.ProductSans
//            else -> SupportedFonts.GoogleSans
//        }
//    }
//}

//object SupportedFonts {
//    val Inter = MusicMattersFont.fromValue(
//        fontName = "Inter",
//        fontFamily = FontFamily(
//            Font(R.font.inter_regular, FontWeight.Normal ),
//            Font( R.font.inter_bold, FontWeight.Bold )
//        )
//    )

//    val Poppins = MusicMattersFont.fromValue(
//        fontName = "Poppins",
//        fontFamily = FontFamily(
//            Font( R.font.roboto_regular, FontWeight.Normal ),
//            Font( R.font.roboto_bold, FontWeight.Bold )
//        )
//    )

//    val DMSans = MusicMattersFont.fromValue(
//        fontName = "DM Sans",
//        fontFamily = FontFamily(
//            Font( R.font.dmsans_regular, FontWeight.Normal ),
//            Font( R.font.dmsans_bold, FontWeight.Bold )
//        )
//    )

//    val Roboto = MusicMattersFont.fromValue(
//        fontName = "Roboto",
//        fontFamily = FontFamily(
//            Font( R.font.roboto_regular, FontWeight.Normal ),
//            Font( R.font.roboto_bold, FontWeight.Bold )
//        )
//    )

//    val ProductSans = MusicMattersFont.fromValue(
//        fontName = "Product Sans",
//        fontFamily = FontFamily(
//            Font( R.font.productsans_regular, FontWeight.Normal ),
//            Font( R.font.productsans_bold, FontWeight.Bold )
//        )
//    )

//    val GoogleSans = MusicMattersFont.fromValue(
//        fontName = "Google Sans",
//        fontFamily = FontFamily(
//            Font( R.font.google_sans_medium, FontWeight.Medium ),
//            Font( R.font.google_sans_regular, FontWeight.Normal ),
//            Font( R.font.google_sans_semi_bold, FontWeight.SemiBold ),
//            Font( R.font.google_sans_bold, FontWeight.Bold ),
//        )
//    )

//}

//object MusicMattersTypography {
//    private val defaultFont = SupportedFonts.GoogleSans
//
//    val all = mapOf(
//        SupportedFonts.Inter.name to SupportedFonts.Inter,
//        SupportedFonts.Poppins.name to SupportedFonts.Poppins,
//        SupportedFonts.DMSans.name to SupportedFonts.DMSans,
//        SupportedFonts.Roboto.name to SupportedFonts.Roboto,
//        SupportedFonts.ProductSans.name to SupportedFonts.ProductSans,
//        SupportedFonts.GoogleSans.name to SupportedFonts.GoogleSans
//    )
//
//    fun resolveFont( name: String? ) = all[ name ] ?: defaultFont
//
//    fun toTypography( font: MusicMattersFont, textDirection: TextDirection ): Typography {
//        val fontFamily = font.fontFamily
//
//        return Typography().run {
//            copy(
//                displayLarge = displayLarge.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                displayMedium = displayMedium.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                displaySmall = displaySmall.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                headlineLarge = headlineLarge.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                headlineMedium = headlineMedium.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                headlineSmall = headlineSmall.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                titleLarge = titleLarge.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                titleMedium = titleMedium.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                titleSmall = titleSmall.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                bodyLarge = bodyLarge.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                bodyMedium = bodyMedium.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                bodySmall = bodySmall.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                labelLarge = labelLarge.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                labelMedium = labelMedium.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                ),
//                labelSmall = labelSmall.copy(
//                    fontFamily = fontFamily,
//                    textDirection = textDirection
//                )
//            )
//        }
//    }
//}