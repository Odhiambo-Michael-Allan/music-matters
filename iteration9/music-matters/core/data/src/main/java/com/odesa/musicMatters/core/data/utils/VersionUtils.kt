package com.odesa.musicMatters.core.data.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

// https://github.com/RetroMusicPlayer/RetroMusicPlayer/blob/dev/appthemehelper/src/main/java/code/name/monkey/appthemehelper/util/VersionUtils.kt
object VersionUtils {

    /**
     * @return true if the device is running API >= 25
     */
    @ChecksSdkIntAtLeast( api = Build.VERSION_CODES.N_MR1 )
    fun isNougatMRandAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    }

    /**
     * @return true if device is running API >= 26
     */
    @ChecksSdkIntAtLeast( api = Build.VERSION_CODES.O )
    fun isOreoAndAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    /**
     * @return true if device is running API >= 27
     */
    @ChecksSdkIntAtLeast( api = Build.VERSION_CODES.O_MR1 )
    fun isOreoMR1AndAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    }

    /**
     * @return true if device is running API >= 28
     */
    @ChecksSdkIntAtLeast( api = Build.VERSION_CODES.P )
    fun isPandAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    /**
     * @return true if device is running API >= 29
     */
    @ChecksSdkIntAtLeast( api = Build.VERSION_CODES.Q )
    @JvmStatic
    fun isQandAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    /**
     * @return true if device is running API >= 30
     */
    @ChecksSdkIntAtLeast( api = Build.VERSION_CODES.R )
    @JvmStatic
    fun isRandAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    /**
     * @return true if device is running API >= 31
     */
    @ChecksSdkIntAtLeast( api = Build.VERSION_CODES.S )
    @JvmStatic
    fun isSandAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    /**
     * @return true if device is running API >= 33
     */
    @ChecksSdkIntAtLeast( api = Build.VERSION_CODES.TIRAMISU )
    @JvmStatic
    fun isTandAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}