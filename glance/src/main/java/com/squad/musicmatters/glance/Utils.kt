package com.squad.musicmatters.glance

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.RoundedCornersTransformation

suspend fun Context.loadBitmapFromUri(
    uri: Uri,
    cornerRadiusPx: Float = 32f
): Bitmap? {
    val request = ImageRequest.Builder( this )
        .data( uri )
        .allowHardware( false ) // Required for Glance IPC
        .size( 256, 256 )
        .transformations( RoundedCornersTransformation( cornerRadiusPx ) )
        .build()

    val result = ImageLoader( this ).execute( request )
    return ( result as? SuccessResult )?.image?.toBitmap()
}