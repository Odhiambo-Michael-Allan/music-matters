package com.squad.musicmatters.glance.small

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import com.squad.musicmatters.core.media.media.UPDATE_SMALL_WIDGET_INTENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmallWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = SmallWidget()

    override fun onReceive( context: Context, intent: Intent ) {
        super.onReceive( context, intent )
        if ( intent.action == UPDATE_SMALL_WIDGET_INTENT ) {
            Toast.makeText(
                context,
                "Small Widget Received update intent",
                Toast.LENGTH_SHORT
            ).show()
            CoroutineScope( Dispatchers.IO ).launch {
                SmallWidget().updateAll( context )
            }
        }
    }

}