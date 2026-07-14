package com.squad.musicmatters.core.data.store

import android.database.Cursor
import androidx.core.database.getStringOrNull

internal fun Cursor.getLongFrom( columnName: String ): Long {
    val columnIndex = getColumnIndex( columnName )
    return getLong( columnIndex )
}

internal fun Cursor.getNullableStringFrom( columnName: String ): String? {
    val columnIndex = getColumnIndex( columnName )
    return getStringOrNull( columnIndex )
}