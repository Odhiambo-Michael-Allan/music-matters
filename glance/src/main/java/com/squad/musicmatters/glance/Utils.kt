package com.squad.musicmatters.glance

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import coil3.ImageLoader
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.loadBitmapFromUri(
    uri: Uri,
    cornerRadiusPx: Float = 32f
): Bitmap? = withContext( Dispatchers.IO ) {
    val request = ImageRequest.Builder( applicationContext )
        .data(uri)
        .allowHardware( false )
        .size( 256, 256 )
        .transformations( RoundedCornersTransformation( cornerRadiusPx ) )
        .build()
    val result = applicationContext.imageLoader.execute( request )
    ( result as? SuccessResult )?.image?.toBitmap()
}
