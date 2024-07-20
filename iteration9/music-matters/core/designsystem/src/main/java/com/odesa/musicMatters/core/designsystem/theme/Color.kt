package com.odesa.musicMatters.core.designsystem.theme

import androidx.compose.ui.graphics.Color

val GoogleRed = Color( 219, 68, 55 )
val GoogleBlue = Color(  66, 133, 244 )
val GoogleYellow = Color(  244, 180, 0 )
val GoogleGreen = Color( 15, 157, 88 )

enum class PrimaryThemeColors {
    Red,
    Orange,
    Amber,
    Yellow,
    Lime,
    Green,
    Emerald,
    Teal,
    Cyan,
    Sky,
    Blue,
    Indigo,
    Violet,
    Purple,
    Fuchsia,
    Pink,
    Rose;

    fun toHumanString() = name
        .split( "_" )
        .joinToString( " " ) {
            it[0].uppercase() + it.substring( 1 ).lowercase()
        }
}

object ThemeColors {
    val Red = GoogleRed
    val Orange = Color(0xFFF97316)
    val Amber = Color(0xFFF59E0B)
    val Yellow = GoogleYellow
    val Lime = Color(0xFF84CC16)
    val Green = GoogleGreen
    val Emerald = Color(0xFF10B981)
    val Teal = Color(0xFF14B8A6)
    val Cyan = Color(0xFF06B6D4)
    val Sky = Color(0xFF0EA5E9)
    val Blue = GoogleBlue
    val Indigo = Color(0xFF6366f1)
    val Violet = Color(0xFF8B5CF6)
    val Purple = Color(0xFFA855F7)
    val Fuchsia = Color(0xFFD946EF)
    val Pink = Color(0xFFEC4899)
    val Rose = Color(0xFFF43f5E)

    val Neutral50 = Color(0xFFFAFAFA)
    val Neutral100 = Color(0xFFF5F5F5)
    val Neutral200 = Color(0xFFE5E5E5)
    val Neutral800 = Color(0xFF262626)
    val Neutral900 = Color(0xFF171717)

    val PrimaryColorsMap = mapOf(
        PrimaryThemeColors.Red to Red,
        PrimaryThemeColors.Orange to Orange,
        PrimaryThemeColors.Amber to Amber,
        PrimaryThemeColors.Yellow to Yellow,
        PrimaryThemeColors.Lime to Lime,
        PrimaryThemeColors.Green to Green,
        PrimaryThemeColors.Emerald to Emerald,
        PrimaryThemeColors.Teal to Teal,
        PrimaryThemeColors.Cyan to Cyan,
        PrimaryThemeColors.Sky to Sky,
        PrimaryThemeColors.Blue to Blue,
        PrimaryThemeColors.Indigo to Indigo,
        PrimaryThemeColors.Violet to Violet,
        PrimaryThemeColors.Purple to Purple,
        PrimaryThemeColors.Fuchsia to Fuchsia,
        PrimaryThemeColors.Pink to Pink,
        PrimaryThemeColors.Rose to Rose,
    )

    fun resolvePrimaryColorName( primaryColorName: String ): Color {
        val colorEnum = PrimaryThemeColors.entries.find { it.name == primaryColorName }!!
        return PrimaryColorsMap.getOrDefault( colorEnum, Blue )
    }
}
