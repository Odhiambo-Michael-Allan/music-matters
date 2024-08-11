package com.odesa.musicMatters.core.common.media.extensions

import android.database.Cursor
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull

fun Cursor.getLongFrom( columnName: String ): Long {
    val columnIndex = getColumnIndex( columnName )
    return getLong( columnIndex )
}

fun Cursor.getNullableLongFrom(columnName: String ): Long? {
    val columnIndex = getColumnIndex( columnName )
    return getLongOrNull( columnIndex )
}

fun Cursor.getNullableIntFrom( columnName: String ): Int? {
    val columnIndex = getColumnIndex( columnName )
    return getIntOrNull( columnIndex )
}

fun Cursor.getNullableStringFrom( columnName: String ): String? {
    val columnIndex = getColumnIndex( columnName )
    return getStringOrNull( columnIndex )
}

fun Cursor.getStringFrom( columnName: String ): String {
    val columnIndex = getColumnIndex( columnName );
    return getString( columnIndex )
}