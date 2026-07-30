package com.squad.musicmatters.glance.ui

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.Composable
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.components.Scaffold

@Composable
internal fun ZeroState( widgetId: Int ) {
    val widgetIdKey = ActionParameters.Key<Int>( AppWidgetManager.EXTRA_APPWIDGET_ID )
    Scaffold() { }
}