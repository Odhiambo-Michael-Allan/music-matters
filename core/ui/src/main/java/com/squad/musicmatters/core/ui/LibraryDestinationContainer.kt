package com.squad.musicmatters.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun LibraryDestinationContainer(
    @StringRes title: Int? = null,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    options: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll( scrollBehavior.nestedScrollConnection )
    ) {
        MinimalAppBar(
            title = title?.let { stringResource( id = it ) } ?: "",
            scrollBehavior = scrollBehavior,
            onNavigationIconClicked = onNavigateBack,
            options = options,
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if ( isLoading )
                CircularProgressIndicator(
                    modifier = Modifier.align( androidx.compose.ui.Alignment.Center )
                )
            else content()
        }
    }
    
}