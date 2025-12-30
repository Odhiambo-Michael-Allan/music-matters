package com.squad.musicmatters.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.i8n.English

@Composable
fun LoaderScaffold(
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {

    Box( modifier = Modifier.fillMaxSize() ) {
        content()
        if ( isLoading ) {
            LoadingWheel(
                modifier = Modifier.align( Alignment.TopCenter )
            )
        }
    }
}

@Preview( showSystemUi = true )
@Composable
private fun LoaderScaffoldPreview() {
    LoaderScaffold(
        isLoading = true,
    ) {
        Text( text = "Hello" )
    }
}