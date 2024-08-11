package com.odesa.musicMatters.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language

import timber.log.Timber

object RouteParameters {
    const val GENRE_ROUTE_GENRE_NAME = "genre-name"
    const val ARTIST_ROUTE_ARTIST_NAME = "artist-name"
    const val ALBUM_ROUTE_ALBUM_NAME = "album-name"
    const val PLAYLIST_ROUTE_PLAYLIST_ID = "playlist-id"
    const val PLAYLIST_ROUTE_PLAYLIST_NAME = "playlist-name"
    const val SEARCH_ROUTE_SEARCH_FILTER = "search-filter"
}

sealed class Route( val name: String ) {
    data object ForYou : Route( "for-you" )
    data object Songs : Route( "songs" )
    data object Artists : Route( "artists" )
    data object Artist : Route( RoutesBuilder.buildArtistRoute( "{${RouteParameters.ARTIST_ROUTE_ARTIST_NAME}}" ) )
    data object Albums : Route( "albums" )
    data object Album : Route( "album" )
    data object Genres : Route( "genres" )
    data object Genre : Route( "genre" )
    data object Playlists : Route( "playlists" )
    data object Playlist : Route( "playlist" )
    data object Tree : Route( "tree" )
    data object Queue : Route( "queue" )
    data object Search : Route( "search" )
    data object Settings : Route( "settings" )
    data object More : Route( "More" )
}

object RoutesBuilder {
    private val encodeParamChars = object {
        val percent = "%" to "%25"
        val slash = "/" to "%2F"
    }
    fun buildAlbumRoute( albumName: String ) = "album/${encodeParam( albumName ) }"
    fun buildPlaylistRoute( playlistId: String, playlistName: String ) = "playlist/${encodeParam( playlistId )}/${encodeParam( playlistName )}"
    fun buildArtistRoute( artistName: String ) = "artist/${encodeParam( artistName)}"

    private fun encodeParam(value: String ): String {
        Timber.tag( DESTINATIONS_TAG ).d( "ENCODING PARAM: $value" )
        return value.replace( encodeParamChars.percent.first, encodeParamChars.percent.second )
            .replace( encodeParamChars.slash.first, encodeParamChars.slash.second )
    }


    fun decodeParam( value: String ) = value
        .replace( encodeParamChars.percent.second, encodeParamChars.percent.first )
        .replace( encodeParamChars.slash.second, encodeParamChars.slash.first )
}

/**
 * Contract for information needed on every MusicMatters destination
 */
interface MusicMattersDestination {
    val selectedIcon: ImageVector
    val unselectedIcon: ImageVector
    val route: Route
    val iconContentDescription: String

    fun getLabel( language: Language): String
}

/**
 * Music Matters app navigation destinations
 */
object ForYou : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.Face
    override val unselectedIcon = Icons.Outlined.Face
    override val route = Route.ForYou
    override val iconContentDescription = "${English.forYou}-tab-icon"

    override fun getLabel( language: Language ) = language.forYou
}

object Songs : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.MusicNote
    override val unselectedIcon = Icons.Outlined.MusicNote
    override val route = Route.Songs
    override val iconContentDescription = "${English.songs}-tab-icon"

    override fun getLabel( language: Language ) = language.songs
}

object Library : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.LibraryMusic
    override val unselectedIcon = Icons.Outlined.LibraryMusic
    override val route: Route = Route.More
    override val iconContentDescription = "library-icon"

    override fun getLabel( language: Language ) = "Library"

}

object Artists : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.Group
    override val unselectedIcon = Icons.Outlined.Group
    override val route = Route.Artists
    override val iconContentDescription = "${English.artists}-tab-icon"

    override fun getLabel( language: Language ) = language.artists
}

object Artist : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.Person
    override val unselectedIcon = Icons.Outlined.Person
    override val route = Route.Artist
    override val iconContentDescription = ""

    override fun getLabel( language: Language ) = ""

    val arguments = listOf(
        navArgument( RouteParameters.ARTIST_ROUTE_ARTIST_NAME ) {
            type = NavType.StringType
        }
    )

}

object Albums : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.Album
    override val unselectedIcon = Icons.Outlined.Album
    override val route = Route.Albums
    override val iconContentDescription = "${English.albums}-tab-icon"

    override fun getLabel( language: Language ) = language.albums
}

object Album : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.Album
    override val unselectedIcon = Icons.Outlined.Album
    override val route = Route.Album
    override val iconContentDescription = ""
    override fun getLabel( language: Language ) = ""

    val routeWithArgs = RoutesBuilder.buildAlbumRoute(
        "{${RouteParameters.ALBUM_ROUTE_ALBUM_NAME}}"
    )
    val arguments = listOf(
        navArgument( RouteParameters.ALBUM_ROUTE_ALBUM_NAME ) {
            type = NavType.StringType
        }
    )

}

object Genres : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.Tune
    override val unselectedIcon = Icons.Outlined.Tune
    override val route = Route.Genres
    override val iconContentDescription = "${English.genres}-tab-icon"

    override fun getLabel( language: Language ) = language.genres
}

object Genre : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.Tune
    override val unselectedIcon = Icons.Outlined.Tune
    override val route = Route.Genre
    override val iconContentDescription = ""

    val routeWithArgs = "${route.name}/{${RouteParameters.GENRE_ROUTE_GENRE_NAME}}"
    val arguments = listOf(
        navArgument( RouteParameters.GENRE_ROUTE_GENRE_NAME ) {
            type = NavType.StringType
        }
    )

    override fun getLabel( language: Language ) = ""
}

object Playlists : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.QueueMusic
    override val unselectedIcon = Icons.Outlined.QueueMusic
    override val route = Route.Playlists
    override val iconContentDescription = "${English.playlists}-tab-icon"

    override fun getLabel( language: Language ) = language.playlists
}

object Playlist : MusicMattersDestination {
    override val selectedIcon = Icons.AutoMirrored.Filled.PlaylistPlay
    override val unselectedIcon = Icons.AutoMirrored.Outlined.PlaylistPlay
    override val route = Route.Playlist
    override val iconContentDescription = ""

    val routeWithArgs = RoutesBuilder.buildPlaylistRoute(
        "{${RouteParameters.PLAYLIST_ROUTE_PLAYLIST_ID}}",
        "{${RouteParameters.PLAYLIST_ROUTE_PLAYLIST_NAME}}"
    )
    override fun getLabel( language: Language ) = ""

    val arguments = listOf(
        navArgument( RouteParameters.PLAYLIST_ROUTE_PLAYLIST_ID ) {
            type = NavType.StringType
        },
        navArgument( RouteParameters.PLAYLIST_ROUTE_PLAYLIST_NAME ) {
            type = NavType.StringType
        }
    )
}

object Search : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.Search
    override val unselectedIcon = Icons.Outlined.Search
    override val route = Route.Search
    override val iconContentDescription = ""

    override fun getLabel( language: Language ) = ""

    val routeWithArgs = "${route.name}/{${RouteParameters.SEARCH_ROUTE_SEARCH_FILTER}}"
    val arguments = listOf(
        navArgument( RouteParameters.SEARCH_ROUTE_SEARCH_FILTER ) {
            type = NavType.StringType
        }
    )

}

object Tree : MusicMattersDestination {
    override val selectedIcon = Icons.Filled.AccountTree
    override val unselectedIcon = Icons.Outlined.AccountTree
    override val route = Route.Tree
    override val iconContentDescription = "${English.tree}-tab-icon"

    override fun getLabel( language: Language ) = language.tree
}

val TOP_LEVEL_DESTINATIONS = listOf(
    ForYou,
    Songs,
    Library
)

val MORE_DESTINATIONS = listOf(
    Artists,
    Albums,
    Tree,
    Playlists,
    Genres
)

fun NavHostController.navigate( route: Route ) = navigateSingleTopTo( route.name )

fun NavHostController.navigateSingleTopTo( route: String ) =
    this.navigate( route ) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

fun NavHostController.navigateToGenreScreen( genreName: String ) =
    this.navigate( "${Genre.route.name}/$genreName" )

fun NavHostController.navigateToAlbumScreen( albumName: String ) =
    this.navigate( RoutesBuilder.buildAlbumRoute( albumName ) ) {
        launchSingleTop = true
    }

fun NavHostController.navigateToPlaylistScreen( playlistId: String, playlistTitle: String ) =
    this.navigate( RoutesBuilder.buildPlaylistRoute( playlistId, playlistTitle ) )

fun NavHostController.navigateToArtistScreen( artistName: String ) =
    this.navigate( RoutesBuilder.buildArtistRoute( artistName ) )

fun NavHostController.navigateToSearchScreen( searchFilterName: String ) =
    this.navigate( "${Search.route.name}/$searchFilterName" )

fun NavBackStackEntry.getRouteArgument( key: String ) = arguments?.getString( key )?.let {
    Timber.tag( DESTINATIONS_TAG ).d( "DECODING PARAM: $key -> $it" )
    RoutesBuilder.decodeParam( it )
}

const val DESTINATIONS_TAG = "DESTINATIONS-TAG"