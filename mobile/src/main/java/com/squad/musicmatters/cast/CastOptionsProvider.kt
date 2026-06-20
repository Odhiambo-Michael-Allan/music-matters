package com.squad.musicmatters.cast

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID

/**
 * Provides the CastOptions for the Cast SDK. Referenced in the AndroidManifest.xml.
 */
class CastOptionsProvider : OptionsProvider {

    override fun getCastOptions( p0: Context ): CastOptions {
        return CastOptions.Builder()
            .setReceiverApplicationId( DEFAULT_MEDIA_RECEIVER_APPLICATION_ID )
            .build()
    }

    override fun getAdditionalSessionProviders( p0: Context ): List<SessionProvider?>? {
        return null
    }

}