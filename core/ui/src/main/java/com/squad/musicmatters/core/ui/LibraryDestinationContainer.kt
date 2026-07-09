package com.squad.musicmatters.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons

@OptIn(  ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class )
@Composable
fun LibraryDestinationContainer(
    @StringRes titleResId: Int? = null,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: ( () -> Unit )? = null,
    options: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        MinimalAppBar(
            title = titleResId?.let { stringResource( id = it ) } ?: "",
            scrollBehavior = scrollBehavior,
            onNavigationIconClicked = onNavigateBack,
            options = options ?: {
                IconButton(
                    onClick = { onNavigateToSettings?.let { it() } }
                ) {
                    Icon(
                        imageVector = MusicMattersIcons.Settings,
                        contentDescription = null,
                    )
                }
            },
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if ( isLoading )
                LoadingIndicator(
                    modifier = Modifier.align( androidx.compose.ui.Alignment.Center )
                )
            else content()
        }
    }
    
}