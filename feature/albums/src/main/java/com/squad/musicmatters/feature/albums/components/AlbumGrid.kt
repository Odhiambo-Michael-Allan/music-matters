package com.squad.musicmatters.feature.albums.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.IconTextBody
import com.squad.musicmatters.core.ui.MediaSortBar
import com.squad.musicmatters.core.ui.MediaSortBarScaffold
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun AlbumGrid(
    albums: List<Album>,
    sortBy: SortAlbumsBy,
    sortInReverse: Boolean,
    onSortTypeChange: ( SortAlbumsBy ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onViewAlbum: ( Album ) -> Unit,
    onPlaySongsInAlbum: ( Album ) -> Unit,
    onAddSongsInAlbumToQueue: ( Album ) -> Unit,
    onPlaySongsInAlbumNext: ( Album ) -> Unit,
    onShuffleAndPlaySongsInAlbum: ( Album ) -> Unit,
    onViewAlbumArtist: ( String ) -> Unit,
    onGetPlaylists: () -> List<Playlist>,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetSongsInAlbum: ( Album ) -> List<Song>,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: ( Album ) -> Boolean,
    onRemoveSongsInAlbumFromQueue: ( Album ) -> Unit,
) {

    MediaSortBarScaffold(
        mediaSortBar = {
            MediaSortBar(
                sortBy = sortBy,
                sortTypes = SortAlbumsBy.entries.associateBy(
                    { it },
                    { it.label() }
                ),
                onSortTypeChange = onSortTypeChange,
                sortInReverse = sortInReverse,
                onSortReverseChange = onSortInReverseChange,
                label = {
                    Text(
                        text = stringResource(
                            id = i8nR.string.core_i8n_n_albums,
                            albums.size
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) {
        when {
            albums.isEmpty() -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = MusicMattersIcons.Album,
                        contentDescription = null,
                    )
                }
            ) {
                Text( text = stringResource( id = i8nR.string.core_i8n_damn_this_is_so_empty ) )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive( 128.dp ),
                    contentPadding = PaddingValues( 8.dp )
                ) {
                    items(
                        albums,
                        key = { it.id }
                    ) {
                        AlbumTile(
                            album = it,
                            onViewAlbum = { onViewAlbum( it ) },
                            onPlaySongsInAlbum = { onPlaySongsInAlbum( it ) },
                            onAddSongsInAlbumToQueue = { onAddSongsInAlbumToQueue( it ) },
                            onPlaySongsInAlbumNext = { onPlaySongsInAlbumNext( it ) },
                            onShuffleAndPlaySongsInAlbum = { onShuffleAndPlaySongsInAlbum( it ) },
                            onViewAlbumArtist = onViewAlbumArtist,
                            onGetPlaylists = onGetPlaylists,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onCreatePlaylist = onCreatePlaylist,
                            onGetSongsInAlbum = { onGetSongsInAlbum( it ) },
                            onShowSnackBar = onShowSnackBar,
                            onShowAddToQueueOption = { onShowAddToQueueOption( it ) },
                            onRemoveSongsInAlbumFromQueue = { onRemoveSongsInAlbumFromQueue( it ) }
                        )
                    }
                }
            }
        }
    }

}

private fun SortAlbumsBy.label(): Int =
    when ( this ) {
        SortAlbumsBy.ALBUM_NAME -> i8nR.string.core_i8n_album_name
        SortAlbumsBy.CUSTOM -> i8nR.string.core_i8n_album_name
        SortAlbumsBy.ARTIST_NAME -> i8nR.string.core_i8n_album_name
        SortAlbumsBy.TRACK_COUNT -> i8nR.string.core_i8n_album_name
    }

@PreviewScreenSizes
@Composable
private fun AlbumGridPreview(
    @PreviewParameter(MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true,
        fontScale = 1.25f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        AlbumGrid(
            albums = previewData.albums,
            sortBy = SortAlbumsBy.ALBUM_NAME,
            sortInReverse = false,
            onSortTypeChange = {},
            onSortInReverseChange = {},
            onViewAlbum = {},
            onGetSongsInAlbum = { emptyList() },
            onViewAlbumArtist = {},
            onGetPlaylists = { emptyList() },
            onCreatePlaylist = {_, _ -> },
            onPlaySongsInAlbum = {},
            onShowSnackBar = {},
            onShowAddToQueueOption = { true },
            onRemoveSongsInAlbumFromQueue = {},
            onPlaySongsInAlbumNext = {},
            onAddSongsInAlbumToQueue = {},
            onAddSongsToPlaylist = { _, _ -> },
            onShuffleAndPlaySongsInAlbum = {}
        )
    }
}