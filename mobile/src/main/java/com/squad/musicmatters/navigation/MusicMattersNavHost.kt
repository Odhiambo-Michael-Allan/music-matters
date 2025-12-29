package com.squad.musicmatters.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.audiofx.AudioEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.feature.nowplaying.NowPlayingBottomBar
import com.squad.musicmatters.feature.nowplaying.NowPlayingBottomSheet
import com.squad.musicmatters.feature.songs.navigation.SongsRoute
import com.squad.musicmatters.feature.songs.navigation.songsScreen
import com.squad.musicmatters.utils.ScreenOrientation
import kotlinx.coroutines.launch
import timber.log.Timber

@androidx.annotation.OptIn( UnstableApi::class )
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicMattersNavHost(
//    mainActivity: MainActivity,
//    nowPlayingViewModel: NowPlayingViewModel,
    navController: NavHostController,
//    preferencesDataSource: PreferencesDataSource,
//    playlistRepository: PlaylistRepository,
//    searchHistoryRepository: SearchHistoryRepository,
//    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//    musicServiceConnection: MusicServiceConnection,
    language: Language,
    labelVisibility: BottomBarLabelVisibility,
) {

    val coroutineScope = rememberCoroutineScope()
    var currentTabName by rememberSaveable { mutableStateOf( Songs.route.name ) }
    var currentlySelectedMoreTab by rememberSaveable { mutableStateOf( "" ) }
    val nowPlayingScreenBottomSheetState = rememberModalBottomSheetState( skipPartiallyExpanded = true )

    var showMoreDestinationsBottomSheet by remember { mutableStateOf( false ) }
//    var showNowPlayingScreen by remember { mutableStateOf( false ) }
    val context = LocalContext.current

    val packageName = LocalContext.current.packageName
    val equalizerActivity = rememberLauncherForActivityResult( object : ActivityResultContract<Unit, Unit>() {
        override fun createIntent( context: Context, input: Unit ) = Intent(
            AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL
        ).apply {
            putExtra( AudioEffect.EXTRA_PACKAGE_NAME, packageName )
            putExtra( AudioEffect.EXTRA_AUDIO_SESSION, 0 )
            putExtra( AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC )
        }

        override fun parseResult( resultCode: Int, intent: Intent? ) {}

    } ) {}

    if ( nowPlayingScreenBottomSheetState.isVisible ) {
        // This forces the Activity to Portrait as long as this block is in the composition
        LockScreenOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        TOP_LEVEL_DESTINATIONS.forEach {
            if ( destination.route == it.route.name )
                currentTabName = it.route.name
        }
        currentlySelectedMoreTab = ""
        MORE_DESTINATIONS.forEach {
            if ( destination.route == it.route.name )
                currentlySelectedMoreTab = it.route.name
        }
    }

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val screenOrientation = ScreenOrientation.fromConfiguration(  LocalConfiguration.current )
    val customNavSuiteType = if ( screenOrientation == ScreenOrientation.LANDSCAPE ) {
        NavigationSuiteType.NavigationRail
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo( adaptiveInfo )
    }

    NavigationSuiteScaffold(
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationRailContainerColor = Color.Transparent,
        ),
        layoutType = customNavSuiteType,
        navigationSuiteItems = {
            TOP_LEVEL_DESTINATIONS.forEach { tab ->
                val isSelected = currentTabName == tab.route.name
                val label = tab.getLabel( language )
                item(
                    selected = isSelected,
                    alwaysShowLabel = labelVisibility == BottomBarLabelVisibility.ALWAYS_VISIBLE,
                    onClick = {
                        if ( tab == Library ) showMoreDestinationsBottomSheet = true
                        else if ( currentTabName != tab.route.name ) {
                            currentTabName = tab.route.name
                            navController.navigate( tab.route )
                        }
                    },
                    icon = {
                        Crossfade(
                            targetState = isSelected,
                            label = "home-bottom-bar"
                        ) {
                            Icon(
                                imageVector = if ( it ) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.iconContentDescription
                            )
                        }
                    },
                    label = when ( labelVisibility ) {
                        BottomBarLabelVisibility.INVISIBLE -> null
                        else -> ( {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = false,
                            )
                        } )
                    }
                )
            }
        }
    ) {
        Column {
            NavHost(
                modifier = Modifier
                    .weight( 1f ),
                navController = navController,
                startDestination = SongsRoute
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
                    onShareSong = { _, _ -> },
                    onDeleteSong = {},
                    onViewArtist = {},
                    onNavigateToSearch = {},
                    onViewAlbum = {},
                    onNavigateToSettings = {}
                )
//                composable(
//                    route = Route.Songs.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val songsScreenViewModel: SongsScreenViewModel = viewModel(
//                        factory = SongsViewModelFactory(
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            musicServiceConnection = musicServiceConnection,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                        )
//                    )
//                    SongsScreen(
//                        viewModel = songsScreenViewModel,
//                        onSettingsClicked = { navController.navigate( Route.Settings.name ) },
//                        onViewAlbum = navController::navigateToAlbumScreen,
//                        onViewArtist = navController::navigateToArtistScreen,
//                        onShareSong = { uri, errorMessage -> shareSong( context, uri, errorMessage ) },
//                        onNavigateToSearch = { navController.navigateToSearchScreen( SearchFilter.SONG.name ) },
//                        onDeleteSong = {
//                            mainActivity.deleteSong( it )
//                        }
//                    )
//                }
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
//                    route = Route.Albums.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val albumsScreenViewModel: AlbumsScreenViewModel = viewModel(
//                        factory = AlbumsViewModelFactory(
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                        )
//                    )
//                    AlbumsScreen(
//                        viewModel = albumsScreenViewModel,
//                        onAlbumClick = navController::navigateToAlbumScreen,
//                        onNavigateToSearch = { navController.navigateToSearchScreen( SearchFilter.ALBUM.name ) },
//                        onSettingsClicked = { navController.navigate( Route.Settings.name ) },
//                        onViewArtist = navController::navigateToArtistScreen
//                    )
//                }
//                composable(
//                    route = Album.routeWithArgs,
//                    arguments = Album.arguments,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) { navBackStackEntry ->
//                    // Retrieve the passed argument
//                    val albumName = navBackStackEntry.getRouteArgument(
//                        RouteParameters.ALBUM_ROUTE_ALBUM_NAME ) ?: ""
//                    val albumScreenViewModel: AlbumScreenViewModel = viewModel(
//                        factory = AlbumScreenViewModelFactory(
//                            albumName = albumName,
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                        )
//                    )
//
//                    AlbumScreen(
//                        albumName = albumName,
//                        viewModel = albumScreenViewModel,
//                        onNavigateBack = { navController.navigateUp() },
//                        onViewAlbum = navController::navigateToAlbumScreen,
//                        onViewArtist = navController::navigateToArtistScreen,
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
//                    route = Route.Queue.name,
//                    enterTransition = { SlideTransition.slideUp.enterTransition() },
//                    exitTransition = { FadeTransition.exitTransition() }
//                ) {
//                    val queueScreenViewModel: QueueScreenViewModel = viewModel(
//                        factory = QueueScreenViewModelFactory(
//                            musicServiceConnection = musicServiceConnection,
//                            settingsRepository = settingsRepository,
//                            playlistRepository = playlistRepository,
//                            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//                        )
//                    )
//                    QueueScreen(
//                        viewModel = queueScreenViewModel,
//                        onViewArtist = navController::navigateToArtistScreen,
//                        onViewAlbum = navController::navigateToAlbumScreen,
//                        onNavigateBack = navController::navigateUp,
//                        onShareSong = { uri, errorMessage ->
//                            shareSong( context, uri, errorMessage )
//                        },
//                        onDeleteSong = { mainActivity.deleteSong( it ) }
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

            Column {
                NowPlayingBottomBar {
                    coroutineScope.launch {
                        nowPlayingScreenBottomSheetState.show()
                    }
                }

                if ( nowPlayingScreenBottomSheetState.isVisible ) {
                    ModalBottomSheet(
                        sheetState = nowPlayingScreenBottomSheetState,
                        onDismissRequest = {
                            coroutineScope.launch {
                                nowPlayingScreenBottomSheetState.hide()
                            }
                        },
                        dragHandle = {}
                    ) {
                        NowPlayingBottomSheet(
                            onViewAlbum = navController::navigateToAlbumScreen,
                            onViewArtist = navController::navigateToArtistScreen,
                            onHideBottomSheet = {
                                coroutineScope.launch {
                                    nowPlayingScreenBottomSheetState.hide()
                                }
                            },
                            onNavigateToQueueScreen = {
                                navController.navigate(Route.Queue.name) {
                                    launchSingleTop = true
                                }
                            },
                            onLaunchEqualizerActivity = {
                                try {
                                    equalizerActivity.launch()
                                } catch (exception: Exception) {
                                    Timber.tag("NOW-PLAYING-BOTTOM-BAR").d(
                                        "Launching equalizer failed: $exception"
                                    )
                                }
                            }
                        )
                    }
                }
            }

//                if ( showMoreDestinationsBottomSheet ) {
//                    ModalBottomSheet(
//                        sheetState = rememberModalBottomSheetState( skipPartiallyExpanded = true ),
//                        onDismissRequest = { showMoreDestinationsBottomSheet = false }
//                    ) {
//                        MORE_DESTINATIONS.forEach {
//                            BottomSheetMenuItem(
//                                leadingIcon = it.selectedIcon,
//                                leadingIconContentDescription = it.iconContentDescription,
//                                label = it.getLabel( language ),
//                                isSelected = currentlySelectedMoreTab == it.route.name
//                            ) {
//                                currentlySelectedMoreTab = it.route.name
//                                showMoreDestinationsBottomSheet = false
//                                currentTabName = Library.route.name
//                                navController.navigate( it.route )
//                            }
//                        }
//                        Spacer( modifier = Modifier.size( 36.dp ) )
//                    }
//                }
//            }
        }
    }
}

@Composable
private fun LockScreenOrientation( orientation: Int ) {
    val context = LocalContext.current
    DisposableEffect( orientation ) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}

        // Save the original orientation to restore it later
        val originalOrientation = activity.requestedOrientation

        // Force the new orientation
        activity.requestedOrientation = orientation

        onDispose {
            // Restore to the original or "Unspecified" (auto-rotate)
            activity.requestedOrientation = originalOrientation
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}





