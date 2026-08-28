package com.squad.musicmatters.core.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toBitmap
import com.squad.musicmatters.core.designsystem.component.ThemePreviews
import com.squad.musicmatters.core.designsystem.theme.LocalTintTheme


@Composable
fun DynamicAsyncImage(
    imageUri: Uri?,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onImageLoaded: ( ( ImageBitmap? ) -> Unit )? = null,
) {
    val iconTint = LocalTintTheme.current.iconTint
    val isLocalInspection = LocalInspectionMode.current
    val context = LocalContext.current
    val sizeResolver = rememberConstraintsSizeResolver()

    // Remember the ImageRequest based on the imageUrl key.
    val imageRequest = remember( imageUri ) {
        ImageRequest.Builder( context )
            .data( imageUri )
            .size( sizeResolver )
            .crossfade( true )
            .build()
    }

    var isLoading by remember { mutableStateOf( true ) }
    var isError by remember { mutableStateOf( false ) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if ( isLoading || isError || isLocalInspection ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray
                ),
                // Internal items should just fill the parent Box
                modifier = Modifier.fillMaxSize()
            ) {}
        }

        AsyncImage(
            model = imageRequest,
            contentScale = contentScale,
            contentDescription = contentDescription,
            colorFilter = if ( iconTint != Color.Unspecified ) {
                ColorFilter.tint( iconTint )
            } else null,
            onSuccess = {
                isLoading = false
                isError = false
                onImageLoaded?.invoke( it.result.image.toBitmap().asImageBitmap() )
            },
            onError = {
                isError = true
                onImageLoaded?.invoke( null )
            },
            onLoading = {

            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@ThemePreviews
@Composable
private fun DynamicAsyncImagePreview() {
    DynamicAsyncImage(
        imageUri = Uri.EMPTY,
        contentDescription = null,
        modifier = Modifier.size( 100.dp, 80.dp )
    )
}