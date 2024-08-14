package com.odesa.musicMatters.core.common.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaStoreUpdateBroadcastReceiver(
    private val onReceiveBroadcast: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive( context: Context?, intent: Intent? ) {
        onReceiveBroadcast()
    }
}