package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Material 3 Expressive Motion Physics.
 *
 * Guidelines:
 * - Springs over Easing.
 * - Tactile Scaling (0.85 down, 1.0 up with bounce).
 */

internal object ExpressiveMotion {
    // Very bouncy spring for release
    val BouncySpring = spring<Float>(
        dampingRatio = 0.45f, // Very bouncy!
        stiffness = 300f // Slow enough to see the bounce
    )

    // Quick spring for press
    val QuickSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    // Formal spring for professional reveals (M3 Expressive)
    val FormalSpring = spring<Float>(
        dampingRatio = 0.8f, // Grounded, minimal bounce
        stiffness = 400f // Slightly faster for a punchy reveal
    )

    /**
     * Large spatial morphs (player sheet expand/collapse).
     *
     * Matches M3 Expressive *slow spatial* stiffness (200), but critically damped:
     * a full-screen sheet must ease into the anchor in one continuous motion —
     * underdamped bounce reads as a second animation fighting travel direction
     * (especially collapse: past mini, then jumps back up).
     */
    val SpatialLargeSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = 200f
    )

    // Sleek Fade Spec (App Store style)
    val SleekFadeSpec = tween<Float>(
        durationMillis = 500,
        easing = LinearOutSlowInEasing
    )
}