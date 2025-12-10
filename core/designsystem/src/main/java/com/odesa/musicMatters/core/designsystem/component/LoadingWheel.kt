package com.odesa.musicMatters.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingWheel(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding( top = 8.dp )
    ) {
        Card(
            shape = RoundedCornerShape( 60.dp ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 8.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = modifier.size( 40.dp )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align( Alignment.Center )
                        .size( 24.dp )
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun LoadingWheelPreview() {
    LoadingWheel()
}