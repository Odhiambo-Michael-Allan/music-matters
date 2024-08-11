package com.odesa.musicMatters.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun GenericCard(
    modifier: Modifier = Modifier,
    imageRequest: ImageRequest?,
    imageLabel: ( @Composable () -> Unit )? = null,
    title: @Composable () -> Unit,
    subtitle: ( @Composable () -> Unit )? = null,
    options: ( @Composable ( expanded: Boolean, onDismissRequest: () -> Unit ) -> Unit )? = null,
    onClick: () -> Unit,
) {
    Card (
        modifier = Modifier.fillMaxWidth().then( modifier ),
        colors = CardDefaults.cardColors( containerColor = Color.Transparent ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding( 12.dp, 12.dp, 4.dp, 12.dp )
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                imageRequest?.let {
                    Box {
                        AsyncImage(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            model = it,
                            contentDescription = null
                        )
                        imageLabel?.let {
                            Box(
                                modifier = Modifier
                                    .offset(y = 8.dp)
                                    .align(Alignment.BottomCenter)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(3.dp, 0.dp)
                                ) {
                                    ProvideTextStyle(
                                        value = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        it()
                                    }
                                }
                            }
                        }
                    }
                    Spacer( modifier = Modifier.width( 15.dp ) )
                }
                Column (
                    modifier = Modifier.weight( 1f )
                ) {
                    ProvideTextStyle( value = MaterialTheme.typography.bodyMedium ) {
                        title()
                    }
                    subtitle?.let {
                        ProvideTextStyle( value = MaterialTheme.typography.bodySmall ) {
                            it()
                        }
                    }
                }
                Spacer( modifier = Modifier.width( 15.dp ) )
                options?.let {
                    var showOptionsMenu by remember { mutableStateOf( false ) }
                    IconButton(
                        onClick = { showOptionsMenu = !showOptionsMenu }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                        it( showOptionsMenu ) {
                            showOptionsMenu = false
                        }
                    }
                }
            }
        }
    }
}