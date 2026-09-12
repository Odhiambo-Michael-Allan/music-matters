package com.squad.musicmatters.core.ui

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.launch
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun <T, E: Enum<E>> GenericGrid(
    items: List<T>,
    @StringRes multipleItemsSortBarLabel: Int,
    @StringRes singleItemSortBarLabel: Int,
    icon: ImageVector,
    sortBy: E,
    sortTypes: Map<E, Int>,
    sortInReverse: Boolean,
    onSortTypeChange: ( E ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onGetItemKeyFor: ( T ) -> String,
    onGetArtworkUriFor: (T ) -> Uri?,
    onGetTitleFor: ( T ) -> String,
    onGetSubTitleFor: ( T ) -> String?,
    onGetHeaderDescriptionFor: ( T ) -> String,
    onViewItem: ( T ) -> Unit,
    onPlaySongsForItem: ( T ) -> Unit,
    onAddSongsForItemToQueue: (T ) -> Unit,
    onPlaySongsForItemNext: ( T ) -> Unit,
    onShuffleAndPlaySongsForItem: ( T ) -> Unit,
    onGetPlaylists: () -> List<Playlist>,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetSongsForItem: ( T ) -> List<Song>,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOptionFor: ( T ) -> Boolean,
    onRemoveSongsForItemFromQueue: ( T ) -> Unit,
    additionalBottomSheetMenuItems: ( @Composable ( T, () -> Unit ) -> Unit )? = null,
) {

    val coroutineScope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    // Show the scroll to top button if the first visible item is past the 10th item. We use
    // a remembered derived state to minimize unnecessary compositions
    val showScrollToTopButton by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 10
        }
    }

    MediaSortBarScaffold(
        mediaSortBar = {
            MediaSortBar(
                sortBy = sortBy,
                sortTypes = sortTypes,
                sortInReverse = sortInReverse,
                onSortTypeChange = onSortTypeChange,
                onSortInReverseChange = onSortInReverseChange,
                label = {
                    Text(
                        text = stringResource(
                            id = if ( items.size > 1 ) {
                                multipleItemsSortBarLabel
                            } else {
                                singleItemSortBarLabel
                            },
                            items.size,
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) {
        when {
            items.isEmpty() -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
            ) {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_damn_this_is_so_empty ),
                    fontWeight = FontWeight.Bold,
                )
            }
            else -> {
                Box {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive( minSize = 150.dp ),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 70.dp
                        )
                    ) {
                        items(
                            items,
                            key = { onGetItemKeyFor( it ) }
                        ) { item ->
                            GenericTile(
                                imageUri = onGetArtworkUriFor( item ),
                                title = onGetTitleFor( item ),
                                subTitle = onGetSubTitleFor( item )
                                    ?: stringResource( id = i8nR.string.core_i8n_untitled ),
                                headerDescription = onGetHeaderDescriptionFor( item ),
                                onGetPlaylists = onGetPlaylists,
                                onPlay = { onPlaySongsForItem( item ) },
                                onClick = { onViewItem( item ) },
                                onShufflePlay = { onShuffleAndPlaySongsForItem( item ) },
                                onAddToQueue = { onAddSongsForItemToQueue( item ) },
                                onGetSongs = { onGetSongsForItem( item ) },
                                onCreatePlaylist = onCreatePlaylist,
                                onAddSongsToPlaylist = onAddSongsToPlaylist,
                                onShowSnackBar = onShowSnackBar,
                                onShowAddToQueueOption = { onShowAddToQueueOptionFor( item ) },
                                onRemoveFromQueue = { onRemoveSongsForItemFromQueue( item ) },
                                onPlayNext = { onPlaySongsForItemNext( item ) },
                                additionalBottomSheetMenuItems = { onDismissRequest ->
                                    additionalBottomSheetMenuItems?.let {
                                        it(
                                            item,
                                            onDismissRequest
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding( 4.dp )
                                    .animateItem()
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align( Alignment.BottomCenter )
                            .padding(
                                bottom = 70.dp
                            )
                    ) {
                        AnimatedVisibility(
                            visible = showScrollToTopButton
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        // Animate scroll to the first item
                                        gridState.scrollToItem( index = 0 )
                                    }
                                }
                            ) {
                                Text(
                                    text = stringResource( id = i8nR.string.core_i8n_scroll_to_top ),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}