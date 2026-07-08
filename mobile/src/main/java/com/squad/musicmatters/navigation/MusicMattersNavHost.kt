package com.squad.musicmatters.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.squad.musicmatters.core.i8n.R
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.feature.album.navigation.albumScreen
import com.squad.musicmatters.feature.album.navigation.navigateToAlbum
import com.squad.musicmatters.feature.albums.navigation.albumsScreen
import com.squad.musicmatters.feature.artist.navigation.artistScreen
import com.squad.musicmatters.feature.artist.navigation.navigateToArtist
import com.squad.musicmatters.feature.artists.navigation.artistsScreen
import com.squad.musicmatters.feature.lyrics.navigation.lyricsScreen
import com.squad.musicmatters.feature.queue.navigation.queueScreen
import com.squad.musicmatters.feature.settings.navigation.settingsScreen
import com.squad.musicmatters.feature.songs.navigation.SongsRoute
import com.squad.musicmatters.feature.songs.navigation.songsScreen
import com.squad.musicmatters.ui.utils.shareSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun MusicMattersNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onDeleteSong: ( Song ) -> Unit,
    snackBarHostState: SnackbarHostState,
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = SongsRoute,
    ) {
//                composable(
//                    Route.ForYou.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val forYouScreenViewModel: ForYouScreenViewModel = viewModel(
//                        factory = ForYouViewModelFactory(
//                            musicServiceConnection = musicServiceConnection,
//                            playlistRepository = playlistRepository,
//                            settingsRepository = settingsRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                        )
//                    )
//                    ForYouScreen(
//                        viewModel = forYouScreenViewModel,
//                        onSettingsClicked = { navController.navigate( Route.Settings.name ) },
//                        onNavigateToSearch = { navController.navigateToSearchScreen( "--" ) },
//                        onSuggestedAlbumClick = navController::navigateToAlbumScreen,
//                        onViewArtist = navController::navigateToArtistScreen,
//                    )
//                }

        songsScreen(
            onShareSong = {
                shareSong(
                    context = context,
                    uri = it,
                    localizedErrorMessage = context
                        .getString( i8nR.string.core_i8n_sharing_song_failed )
                )
            },
            onDeleteSong = onDeleteSong,
            onViewArtist = {},
            onViewAlbum = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        queueScreen(
            onNavigateBack = { navController.navigateUp() },
            onShareSong = {
                shareSong(
                    context = context,
                    uri = it,
                    localizedErrorMessage = context
                        .getString( i8nR.string.core_i8n_sharing_song_failed )
                )
            },
            onDeleteSong = onDeleteSong,
            onViewArtist = {},
            onViewAlbum = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        lyricsScreen(
            onNavigateBack = { navController.navigateUp() }
        )
        settingsScreen(
            onNavigateBack = { navController.navigateUp() }
        )
        albumsScreen(
            onViewAlbum = {
                navController.navigateToAlbum(
                    albumId = it.id,
                    navOptions = nonTopLevelDestinationNavOptions()
                )
            },
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        albumScreen(
            onViewAlbum = {},
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onDeleteSong = onDeleteSong,
            onShareSong = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        artistsScreen(
            onNavigateBack = { navController.navigateUp() },
            onViewArtist = {
                navController.navigateToArtist(
                    artistId = it,
                    navOptions = nonTopLevelDestinationNavOptions()
                )
            },
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        artistScreen(
            onViewAlbum = {},
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onDeleteSong = onDeleteSong,
            onShareSong = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )

//                composable(
//                    route = Route.Artists.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val artistsScreenViewModel: ArtistsScreenViewModel = viewModel(
//                        factory = ArtistsViewModelFactory(
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                        )
//                    )
//                    ArtistsScreen(
//                        viewModel = artistsScreenViewModel,
//                        onArtistClick = navController::navigateToArtistScreen,
//                        onNavigateToSearch = { navController.navigateToSearchScreen( SearchFilter.ARTIST.name ) },
//                        onSettingsClicked = { navController.navigate( Route.Settings.name ) }
//                    )
//                }
//                composable(
//                    route = Artist.route.name,
//                    arguments = Artist.arguments,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) { navBackStackEntry ->
//                    // Retrieve the passed argument
//                    val artistName = navBackStackEntry.getRouteArgument(
//                        RouteParameters.ARTIST_ROUTE_ARTIST_NAME
//                    ) ?: ""
//                    val artistScreenViewModel: ArtistScreenViewModel = viewModel(
//                        factory = ArtistScreenViewModelFactory(
//                            artistName = artistName,
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                        )
//                    )
//                    ArtistScreen(
//                        artistName = artistName,
//                        viewModel = artistScreenViewModel,
//                        onViewAlbum = navController::navigateToAlbumScreen,
//                        onViewArtist = navController::navigateToArtistScreen,
//                        onNavigateBack = { navController.navigateUp() },
//                        onShareSong = { uri, errorMessage -> shareSong( context, uri, errorMessage ) },
//                        onDeleteSong = {
//                            mainActivity.deleteSong( it )
//                        }
//                    )
//                }
//                composable(
//                    route = Route.Genres.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val genresScreenViewModel: GenresScreenViewModel = viewModel(
//                        factory = GenresScreenViewModelFactory(
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                        )
//                    )
//                    GenresScreen(
//                        viewModel = genresScreenViewModel,
//                        onGenreClick = navController::navigateToGenreScreen,
//                        onNavigateToSearch = { navController.navigateToSearchScreen( SearchFilter.GENRE.name ) },
//                        onSettingsClicked = { navController.navigate( Route.Settings.name ) }
//                    )
//                }
//                composable(
//                    route = Genre.routeWithArgs,
//                    arguments = Genre.arguments,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) { navBackStackEntry ->
//                    // Retrieve the passed argument
//                    val genreName = navBackStackEntry.getRouteArgument(
//                        RouteParameters.GENRE_ROUTE_GENRE_NAME ) ?: ""
//
//                    val genreScreenViewModel: GenreScreenViewModel = viewModel(
//                        factory = GenreScreenViewModelFactory(
//                            genreName = genreName,
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                        )
//                    )
//                    GenreScreen(
//                        genreName = genreName,
//                        viewModel = genreScreenViewModel,
//                        onViewAlbum = navController::navigateToAlbumScreen,
//                        onViewArtist = navController::navigateToArtistScreen,
//                        onNavigateBack = { navController.navigateUp() },
//                        onShareSong = { uri, errorMessage -> shareSong( context, uri, errorMessage ) },
//                        onDeleteSong = {
//                            mainActivity.deleteSong( it )
//                        }
//                    )
//                }
//                composable(
//                    route = Route.Playlists.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val playlistsViewModel: PlaylistsViewModel = viewModel(
//                        factory = PlaylistsViewModelFactory(
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//                        )
//                    )
//                    PlaylistsScreen(
//                        viewModel = playlistsViewModel,
//                        onPlaylistClick = { playlistId, playlistName -> navController.navigateToPlaylistScreen( playlistId, playlistName ) },
//                        onNavigateToSearch = { navController.navigateToSearchScreen( SearchFilter.PLAYLIST.name ) },
//                        onSettingsClicked = { navController.navigate( Route.Settings.name ) }
//                    )
//                }
//                composable(
//                    route = Playlist.routeWithArgs,
//                    arguments = Playlist.arguments,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) { navBackStackEntry ->
//                    val playlistId = navBackStackEntry.getRouteArgument(
//                        RouteParameters.PLAYLIST_ROUTE_PLAYLIST_ID
//                    ) ?: ""
//                    val playlistName = navBackStackEntry.getRouteArgument(
//                        RouteParameters.PLAYLIST_ROUTE_PLAYLIST_NAME
//                    ) ?: ""
//                    val playlistScreenViewModel: PlaylistScreenViewModel = viewModel(
//                        factory = PlaylistScreenViewModelFactory(
//                            playlistId = playlistId,
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistsRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//                        )
//                    )
//
//                    PlaylistScreen(
//                        playlistTitle = playlistName,
//                        viewModel = playlistScreenViewModel,
//                        onViewAlbum = navController::navigateToAlbumScreen,
//                        onViewArtist = navController::navigateToArtistScreen,
//                        onNavigateBack = { navController.navigateUp() },
//                        onShareSong = { uri, errorMessage -> shareSong( context, uri, errorMessage ) },
//                        onDeleteSong = {
//                            mainActivity.deleteSong( it )
//                        }
//                    )
//                }
//                composable(
//                    route = Route.Tree.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val treeScreenViewModel: TreeScreenViewModel = viewModel(
//                        factory = TreeViewModelFactory(
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                        )
//                    )
//                    TreeScreen(
//                        viewModel = treeScreenViewModel,
//                        onViewArtist = navController::navigateToArtistScreen,
//                        onViewAlbum = navController::navigateToAlbumScreen,
//                        onShareSong = { uri, errorMessage -> shareSong( context, uri, errorMessage ) },
//                        onNavigateToSearch = { navController.navigateToSearchScreen( "--" ) },
//                        onSettingsClicked = { navController.navigate( Route.Settings.name ) },
//                        onDeleteSong = {
//                            mainActivity.deleteSong( it )
//                        }
//                    )
//                }
//                composable(
//                    route = Search.routeWithArgs,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) { navBackStackEntry ->
//                    val searchFilterName = navBackStackEntry.getRouteArgument(
//                        RouteParameters.SEARCH_ROUTE_SEARCH_FILTER
//                    ) ?: ""
//
//                    val searchScreenViewModel: SearchScreenViewModel = viewModel(
//                        factory = SearchScreenViewModelFactory(
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            searchHistoryRepository = searchHistoryRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//                        )
//                    )
//                    SearchScreen(
//                        viewModel = searchScreenViewModel,
//                        initialSearchFilter = getSearchFilterFrom( searchFilterName ),
//                        onAlbumClick = { navController.navigateToAlbumScreen( it.title ) },
//                        onArtistClick = { navController.navigateToArtistScreen( it.name ) },
//                        onGenreClick = { navController.navigateToGenreScreen( it.name ) },
//                        onPlaylistClick = { navController.navigateToPlaylistScreen( it.id, it.title ) }
//                    ) {
//                        navController.navigateUp()
//                    }
//                }
//                composable(
//                    Route.Settings.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val settingsViewModel: SettingsViewModel = viewModel(
//                        factory = SettingsViewModelFactory(
//                            settingsRepository
//                        )
//                    )
//                    SettingsScreen(
//                        viewModel = settingsViewModel,
//                        onBackPressed = { navController.popBackStack() },
//                        goToRedditCommunity = {
//                            mainActivity.startActivity(
//                                Intent( Intent.ACTION_VIEW )
//                                    .setData(
//                                        Uri.parse(
//                                            mainActivity.getString( R.string.reddit_community )
//                                        )
//                                    )
//                            )
//                        },
//                        goToDiscordServer = {
//                            mainActivity.startActivity(
//                                Intent( Intent.ACTION_VIEW )
//                                    .setData(
//                                        Uri.parse(
//                                            mainActivity.getString( R.string.discord_server_url )
//                                        )
//                                    )
//                            )
//                        },
//                        goToTelegramChannel = {
//                            mainActivity.startActivity(
//                                Intent( Intent.ACTION_VIEW )
//                                    .setData(
//                                        Uri.parse(
//                                            mainActivity.getString( R.string.telegram_channel_link )
//                                        )
//                                    )
//                            )
//                        },
//                        goToGithubProfile = {
//                            mainActivity.startActivity(
//                                Intent( Intent.ACTION_VIEW )
//                                    .setData(
//                                        Uri.parse(
//                                            mainActivity.getString( R.string.github_profile_url )
//                                        )
//                                    )
//                            )
//                        },
//                        goToAppGithubRepository = {
//                            mainActivity.startActivity(
//                                Intent( Intent.ACTION_VIEW )
//                                    .setData(
//                                        Uri.parse(
//                                            mainActivity.getString( R.string.app_github_repo )
//                                        )
//                                    )
//                            )
//                        }
//                    )
//                }
    }
}

private fun SnackbarHostState.showSnackBar(
    coroutineScope: CoroutineScope,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
) {
    // Instantly dismiss the active snackbar if one exists
    currentSnackbarData?.dismiss()

    // Launch the new one immediately
    coroutineScope.launch {
        showSnackbar(
            message = message,
            duration = duration
        )
    }
}

private fun nonTopLevelDestinationNavOptions() = navOptions {
    launchSingleTop = true
    restoreState = true
}