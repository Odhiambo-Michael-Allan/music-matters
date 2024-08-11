package com.odesa.musicMatters.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.utils.runFunctionIfTrueElseReturnThisObject


@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun TopAppBar(
    onNavigationIconClicked: () -> Unit,
    title: String,
    settings: String,
    onSettingsClicked: () -> Unit,
) {
    
    CenterAlignedTopAppBar(
        modifier = Modifier
            .clearAndSetSemantics {
                contentDescription = "top-app-bar"
            },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton( onClick = onNavigationIconClicked ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            }
        },
        title = {
            Crossfade(
                targetState = title,
                label = "top-app-bar-title"
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TopAppBarMinimalTitle {
                        Text( text = it )
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = onSettingsClicked
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null
                )
            }
        }
    )
}

@Preview( showBackground = true )
@Composable
fun TopAppBarPreview() {
    TopAppBar(
        onNavigationIconClicked = { /*TODO*/ },
        title = English.songs,
        settings = English.settings
    ) {

    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun MinimalAppBar(
    modifier: Modifier = Modifier,
    onNavigationIconClicked: () -> Unit,
    title: String,
    options: ( @Composable () -> Unit )? = null
    
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton( onClick = onNavigationIconClicked ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {
            Crossfade(
                targetState = title,
                label = "top-app-bar-title"
            ) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                        textAlign = TextAlign.Center
                    )
                ) {
                    Text(
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            options?.let {
                options()
            }
        }
    )
}

@Composable
fun TopAppBarMinimalTitle(
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .runFunctionIfTrueElseReturnThisObject(fillMaxWidth) { fillMaxWidth() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center
            )
        ) {
            content()
        }
    }
}

@Preview( showBackground = true )
@Composable
fun TopAppBarMinimalAppBarPreview() {
    MinimalAppBar(
        onNavigationIconClicked = { /*TODO*/ },
        title = "Queue"
    )
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun QueueScreenTopAppBar(
    modifier: Modifier = Modifier,
    onBackArrowClick: () -> Unit,
    onSaveClick: () -> Unit,
    onClearClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton( onClick = onBackArrowClick ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {
            Crossfade(
                targetState = stringResource( id = R.string.queue ),
                label = "top-app-bar-title"
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TopAppBarMinimalTitle {
                        Text( text = it )
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = onSaveClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onClearClick
            ) {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = null
                )
            }
        }
    )
}

@Preview( showBackground = true )
@Composable
fun QueueScreenTopAppBarPreview() {
    QueueScreenTopAppBar(
        onBackArrowClick = {},
        onSaveClick = {},
        onClearClick = {},
    )
}