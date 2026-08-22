package com.squad.musicmatters.glance.medium

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
class MediumWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = MediumWidget()

    override fun onReceive( context: Context, intent: Intent ) {
        super.onReceive( context, intent )
        if ( intent.action == "com.squad.musicmatters.ACTION_UPDATE_WIDGET" ) {
            Toast.makeText(
                context,
                "Medium Widget Receiver received update intent",
                Toast.LENGTH_SHORT
            ).show()
            CoroutineScope( Dispatchers.IO ).launch {
                MediumWidget().updateAll( context )
            }
        }
    }

}