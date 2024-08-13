package com.odesa.musicMatters.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.core.i8n.English

@Composable
fun LoaderScaffold(
    isLoading: Boolean,
    loading: String,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current

    Box( modifier = Modifier.fillMaxSize() ) {
        AnimatedVisibility(
            visible = !isLoading,
            enter = expandVertically( expandFrom = Alignment.Bottom ) + fadeIn(),
            exit = shrinkVertically( shrinkTowards = Alignment.Bottom ) + fadeOut(),
        ) {
            Box {
                content()
            }
        }
        AnimatedVisibility(
            visible = isLoading,
            enter = expandVertically( expandFrom = Alignment.Bottom ) + fadeIn(),
            exit = shrinkVertically( shrinkTowards = Alignment.Bottom ) + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .align( Alignment.Center ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding( 16.dp )
                    )
                    Text(
                        text = loading,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Preview( showSystemUi = true )
@Composable
fun LoaderScaffoldPreview() {
    LoaderScaffold(
        isLoading = true,
        loading = English.loading
    ) {
        Text( text = "Hello" )
    }
}