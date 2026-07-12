package com.squad.musicmatters.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.feature.albums.navigation.AlbumsRoute
import com.squad.musicmatters.feature.artists.navigation.ArtistsRoute
import com.squad.musicmatters.feature.folders.navigation.FoldersRoute
import com.squad.musicmatters.feature.foryou.navigation.ForYouRoute
import com.squad.musicmatters.feature.genres.navigation.GenresRoute
import com.squad.musicmatters.feature.playlists.navigation.PlaylistsRoute
import com.squad.musicmatters.feature.songs.navigation.SongsRoute
import kotlinx.serialization.Serializable
import com.squad.musicmatters.core.i8n.R as i8nR


import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @param:StringRes val iconTextId: Int,
    @param:StringRes val titleTextId: Int,
    val route: KClass<*>
) {
    FOR_YOU(
        selectedIcon = MusicMattersIcons.ForYouSelected,
        unselectedIcon = MusicMattersIcons.ForYou,
        route = ForYouRoute::class,
        titleTextId = i8nR.string.core_i8n_for_you,
        iconTextId = i8nR.string.core_i8n_for_you,
    ),
    SONGS(
        selectedIcon = MusicMattersIcons.MusicNote,
        unselectedIcon = MusicMattersIcons.MusicNoteOutlined,
        route = SongsRoute::class,
        titleTextId = i8nR.string.core_i8n_songs,
        iconTextId = i8nR.string.core_i8n_songs,
    ),
    LIBRARY(
        selectedIcon = MusicMattersIcons.Library,
        unselectedIcon = MusicMattersIcons.LibraryUnselected,
        iconTextId = i8nR.string.core_i8n_library,
        titleTextId = i8nR.string.core_i8n_library,
        route = LibraryRoute::class
    )
}

@Serializable
data object LibraryRoute



enum class LibraryDestination(
    val icon: ImageVector,
    @StringRes val titleTextId: Int,
    val route: KClass<*>
) {
    ALBUMS(
        icon = MusicMattersIcons.Album,
        titleTextId = i8nR.string.core_i8n_albums,
        route = AlbumsRoute::class,
    ),
    ARTISTS(
        icon = MusicMattersIcons.Artist,
        titleTextId = i8nR.string.core_i8n_artists,
        route = ArtistsRoute::class
    ),
    GENRES(
        icon = MusicMattersIcons.MusicNote,
        titleTextId = i8nR.string.core_i8n_genres,
        route = GenresRoute::class
    ),
    PLAYLISTS(
        icon = MusicMattersIcons.Playlist,
        titleTextId = i8nR.string.core_i8n_playlists,
        route = PlaylistsRoute::class
    ),
    FOLDERS(
        icon = MusicMattersIcons.Folder,
        titleTextId = i8nR.string.core_i8n_folders,
        route = FoldersRoute::class
    )
}