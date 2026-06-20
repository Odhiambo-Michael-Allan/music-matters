package com.squad.musicmatters.cast

import android.content.Context
import androidx.mediarouter.app.MediaRouteButton

/**
 * An extended version of the cast framework's built-in [MediaRouteButton] that allows us to keep
 * track of the valid instances. The idea is to bind the addition and removal of the button to the
 * event when the button is attached and detached from the window respectively.
 */
class CustomMediaRouteButton( context: Context ) : MediaRouteButton( context ) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        MediaRouteButtonManager.add( this )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        MediaRouteButtonManager.remove( this )
    }

}

/**
 * A helper singleton to store the valid [CustomMediaRouteButton] instances. Since these buttons
 * can be created and destroyed dynamically, we need to keep track of the valid instances.
 */
object MediaRouteButtonManager {

    private val buttons = mutableListOf<CustomMediaRouteButton>()

    // Since we only want to fake a click on the button, it's enough to always interact with
    // the last one
    val current: CustomMediaRouteButton?
        get() = buttons.lastOrNull()

    fun add( button: CustomMediaRouteButton ) {
        buttons.add( button )
    }

    fun remove( button: CustomMediaRouteButton ) {
        buttons.remove( button )
    }

}