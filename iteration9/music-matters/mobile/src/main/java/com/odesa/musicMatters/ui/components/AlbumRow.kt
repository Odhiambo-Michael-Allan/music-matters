package com.odesa.musicMatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.datatesting.albums.testAlbums
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Album
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song

@Composable
fun AlbumRow(
    modifier: Modifier = Modifier,
    albums: List<Album>,
    language: Language,
    @DrawableRes fallbackResourceId: Int,
    onPlaySongsInAlbum: ( Album ) -> Unit,
    onAddToQueue: ( Album ) -> Unit,
    onPlayNext: ( Album ) -> Unit,
    onShufflePlay: ( Album ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onGetSongsInAlbum: ( Album ) -> List<Song>,
    onGetSongsInPlaylist: (PlaylistInfo) -> List<Song>,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
) {
    BoxWithConstraints {
        val maxSize = min( maxHeight, maxWidth ).div( 2f )
        val width = min( maxSize, 200.dp )

        LazyRow (
            modifier = modifier,
            contentPadding = PaddingValues( 8.dp, 0.dp )
        ) {
            items( albums ) {
                Box(
                    modifier = Modifier.width( width.minus( 15.dp ) )
                ) {
                    AlbumTile(
                        modifier = Modifier.width( width ),
                        album = it,
                        language = language,
                        fallbackResourceId = fallbackResourceId,
                        onPlayAlbum = { onPlaySongsInAlbum( it ) },
                        onAddToQueue = { onAddToQueue( it ) },
                        onPlayNext = { onPlayNext( it ) },
                        onShufflePlay = { onShufflePlay( it ) },
                        onViewArtist = onViewArtist,
                        onClick = { onViewAlbum( it.title ) },
                        onAddSongsToPlaylist = onAddSongsToPlaylist,
                        onCreatePlaylist = onCreatePlaylist,
                        onGetPlaylists = onGetPlaylists,
                        onGetSongsInAlbum = onGetSongsInAlbum,
                        onGetSongsInPlaylist = onGetSongsInPlaylist,
                        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                    )
                }
            }
        }
    }
}

@Preview( showSystemUi = true )
@Composable
fun AlbumRowPreview() {
    MusicMattersTheme(
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = SettingsDefaults.USE_MATERIAL_YOU
    ) {
        AlbumRow(
            albums = testAlbums,
            language = English,
            fallbackResourceId = R.drawable.placeholder_light,
            onPlaySongsInAlbum = {},
            onAddToQueue = { /*TODO*/ },
            onPlayNext = { /*TODO*/ },
            onShufflePlay = { /*TODO*/ },
            onViewAlbum = {},
            onViewArtist = {},
            onAddSongsToPlaylist = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onGetPlaylists = { emptyList() },
            onGetSongsInAlbum = { emptyList() },
            onGetSongsInPlaylist = { emptyList() },
            onSearchSongsMatchingQuery = { emptyList() },
        )
    }
}