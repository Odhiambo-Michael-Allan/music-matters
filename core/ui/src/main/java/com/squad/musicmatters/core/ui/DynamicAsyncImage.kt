package com.squad.musicmatters.core.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.odesa.musicMatters.core.designsystem.component.ThemePreviews
import com.odesa.musicMatters.core.designsystem.theme.LocalTintTheme


@Composable
fun DynamicAsyncImage(
    imageUri: Uri?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val iconTint = LocalTintTheme.current.iconTint
    val isLocalInspection = LocalInspectionMode.current
    val context = LocalContext.current

    // Remember the ImageRequest based on the imageUri key.
    val imageRequest = remember( imageUri ) {
        ImageRequest.Builder( context )
            .data( imageUri )
            .crossfade( true )
            .build()
    }

    val painter = rememberAsyncImagePainter( model = imageRequest )
    val state = painter.state
    val isLoading = state is AsyncImagePainter.State.Loading
    val isError = state is AsyncImagePainter.State.Error

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        if ( isLoading || isError || isLocalInspection ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray
                ),
                modifier = modifier,
            ) {}
        }
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = contentDescription,
            colorFilter = if ( iconTint != Color.Unspecified ) {
                ColorFilter.tint( iconTint )
            } else null
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