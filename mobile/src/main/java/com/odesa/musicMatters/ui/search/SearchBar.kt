package com.odesa.musicMatters.ui.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.SearchFilter

@OptIn( ExperimentalMaterial3Api::class )
@Composable
internal fun SearchBar(
    searchQuery: String,
    language: Language,
    currentSearchFilter: SearchFilter?,
    onSearchQueryChange: ( String ) -> Unit,
    onFilterChange: ( SearchFilter? ) -> Unit,
    onClearSearch: () -> Unit,
    onNavigateBack: () -> Unit,
) {

    Column (
        Modifier
            .windowInsetsPadding( TopAppBarDefaults.windowInsets )
            .clipToBounds()
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Search ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            singleLine = true,
            value = searchQuery,
            onValueChange = { onSearchQueryChange( it ) },
            placeholder = {
                Text( text = language.searchYourMusic )
            },
            leadingIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            trailingIcon = {
                if ( searchQuery.isNotEmpty() ) {
                    IconButton(
                        onClick = onClearSearch
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        Spacer( modifier = Modifier.height( 4.dp ) )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll( rememberScrollState() ),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer( modifier = Modifier.width( 4.dp ) )
            FilterChip(
                modifier = Modifier.padding( 4.dp, 0.dp ),
                selected = currentSearchFilter == null,
                onClick = {
                    onFilterChange( null )
                },
                label = {
                    Text( text = language.all )
                }
            )
            SearchFilter.entries.forEach {
                FilterChip(
                    modifier = Modifier.padding( 4.dp, 0.dp ),
                    selected = currentSearchFilter == it,
                    onClick = {
                        onFilterChange( it )
                    },
                    label = {
                        Text( text = it.label( language ) )
                    }
                )
            }
            Spacer( modifier = Modifier.width( 4.dp ) )
        }
        Spacer( modifier = Modifier.height( 4.dp ) )
    }
}

@PreviewScreenSizes
@Composable
private fun TopAppBarPreview() {
    SearchBar(
        searchQuery = "",
        currentSearchFilter = null,
        language = English,
        onSearchQueryChange = {},
        onFilterChange = {},
        onClearSearch = {},
        onNavigateBack = {}
    )
}