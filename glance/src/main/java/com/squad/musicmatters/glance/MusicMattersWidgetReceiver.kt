package com.squad.musicmatters.glance

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicMattersWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = MusicMattersAppWidget()

    override fun onReceive( context: Context, intent: Intent ) {
        super.onReceive( context, intent )
        if ( intent.action == "com.squad.musicmatters.ACTION_UPDATE_WIDGET" ) {
            Toast.makeText(
                context,
                "Widget Receiver received update intent",
                Toast.LENGTH_SHORT
            ).show()
            CoroutineScope( Dispatchers.IO ).launch {
                MusicMattersAppWidget().updateAll( context )
            }
        }
    }

}