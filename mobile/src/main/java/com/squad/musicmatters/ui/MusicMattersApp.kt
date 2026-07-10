package com.squad.musicmatters.ui

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.window.core.layout.WindowSizeClass
import com.squad.musicmatters.MainActivityUiState
import com.squad.musicmatters.R
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
import com.squad.musicmatters.core.ui.TopAppBar
import com.squad.musicmatters.feature.albums.navigation.albumsScreen
import com.squad.musicmatters.feature.albums.navigation.navigateToAlbums
import com.squad.musicmatters.feature.artists.navigation.navigateToArtists
import com.squad.musicmatters.feature.genres.navigation.navigateToGenres
import com.squad.musicmatters.feature.lyrics.navigation.lyricsScreen
import com.squad.musicmatters.feature.lyrics.navigation.navigateToLyricsScreen
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreen
import com.squad.musicmatters.feature.nowplaying.components.MiniPlayer
import com.squad.musicmatters.feature.playlists.navigation.navigateToPlaylists
import com.squad.musicmatters.feature.queue.navigation.navigateToQueueScreen
import com.squad.musicmatters.feature.queue.navigation.queueScreen
import com.squad.musicmatters.feature.settings.navigation.navigateToSettings
import com.squad.musicmatters.feature.settings.navigation.settingsScreen
import com.squad.musicmatters.feature.songs.navigation.SongsRoute
import com.squad.musicmatters.feature.songs.navigation.navigateToSongs
import com.squad.musicmatters.feature.songs.navigation.songsScreen
import com.squad.musicmatters.navigation.LibraryDestination
import com.squad.musicmatters.navigation.MusicMattersNavHost
import com.squad.musicmatters.navigation.TopLevelDestination
import com.squad.musicmatters.ui.utils.shareSong
import com.squad.musicmatters.utils.ScreenOrientation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.OptIn
import com.squad.musicmatters.core.i8n.R as i8nR


@Composable
fun MusicMattersApp(
    uiState: MainActivityUiState,
    navController: NavHostController = rememberNavController(),
    onDeleteSong: ( Song ) -> Unit,
) {
    MusicMattersAppContent(
        uiState = uiState,
        navController = navController,
        onDeleteSong = onDeleteSong,
    )
}

@Composable
fun MusicMattersAppContent(
    uiState: MainActivityUiState,
    navController: NavHostController,
    onDeleteSong: ( Song ) -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        when ( uiState ) {
            MainActivityUiState.Loading -> {}
            is MainActivityUiState.Success -> {
                MusicMattersAppContent(
                    navController = navController,
                    labelVisibility = uiState.userData.bottomBarLabelVisibility,
                    onDeleteSong = onDeleteSong,
                )
            }
        }
    }
}

@androidx.annotation.OptIn( UnstableApi::class )
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicMattersAppContent(
    navController: NavHostController,
    labelVisibility: BottomBarLabelVisibility,
    onDeleteSong: ( Song ) -> Unit,
) {

    var currentTopLevelDestinationName by rememberSaveable { mutableStateOf( TopLevelDestination.SONGS.route.qualifiedName ) }
    var currentlySelectedLibraryDestinationName by rememberSaveable { mutableStateOf( "" ) }

    var showNowPlayingScreen by rememberSaveable { mutableStateOf( false ) }
    var showMoreDestinationsBottomSheet by remember { mutableStateOf( false ) }
    var shouldShowTopAppBar by remember { mutableStateOf( false ) }

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

    if ( showNowPlayingScreen ) {
        // This forces the Activity to Portrait as long as this block is in the composition
        TransitionScreenToPortraitMode()
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        TopLevelDestination.entries.forEach {
            if ( destination.route == it.route.qualifiedName ) {
                currentTopLevelDestinationName = it.route.qualifiedName
            }
        }
        currentlySelectedLibraryDestinationName = ""
        LibraryDestination.entries.forEach {
            if ( destination.route == it.route.qualifiedName ) {
                currentlySelectedLibraryDestinationName = it.route.qualifiedName ?: ""
            }
        }
        shouldShowTopAppBar = destination.route in TopLevelDestination.entries.map {
            it.route.qualifiedName
        }
    }

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val screenOrientation = ScreenOrientation.fromConfiguration(  LocalConfiguration.current )
    val customNavSuiteType = if ( screenOrientation == ScreenOrientation.LANDSCAPE ) {
        NavigationSuiteType.NavigationRail
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo( adaptiveInfo )
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()



    NavigationSuiteScaffold(
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationRailContainerColor = Color.Transparent,
            navigationBarContainerColor = Color.Transparent,
        ),
        layoutType = customNavSuiteType,
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                val isSelected = currentTopLevelDestinationName == destination.route.qualifiedName
                item(
                    selected = isSelected,
                    alwaysShowLabel = labelVisibility == BottomBarLabelVisibility.ALWAYS_VISIBLE,
                    onClick = {
                        if ( destination == TopLevelDestination.LIBRARY ) {
                            showMoreDestinationsBottomSheet = true
                        }
                        else if ( isSelected.not() ) {
                            navController.navigateToTopLevelDestination( destination )
                        }
                    },
                    icon = {
                        Crossfade(
                            targetState = isSelected,
                            label = "home-bottom-bar"
                        ) {
                            Icon(
                                imageVector = if ( it ) {
                                    destination.selectedIcon
                                } else {
                                    destination.unselectedIcon
                                },
                                contentDescription = stringResource( id = destination.iconTextId )
                            )
                        }
                    },
                    label = when ( labelVisibility ) {
                        BottomBarLabelVisibility.INVISIBLE -> null
                        else -> ( {
                            Text(
                                text = stringResource( id = destination.iconTextId ),
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

        Scaffold(
            topBar = {
                if ( shouldShowTopAppBar ) {
                    TopAppBar(
                        title = stringResource( id = i8nR.string.core_i8n_songs ),
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        onNavigationIconClicked = {},
                        onSettingsClicked = {
                            navController.navigateToSettings(
                                navOptions = navOptions {
                                    launchSingleTop = true
                                }
                            )
                        }
                    )
                }

            },
            snackbarHost = {
                SnackbarHost(
                    snackBarHostState,
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.exclude(
                            WindowInsets.ime,
                        ),
                    ),
                )
            },
            contentWindowInsets = WindowInsets( 0, 0, 0, 0 ),
            modifier = Modifier
                .nestedScroll( topAppBarScrollBehavior.nestedScrollConnection )
        ) { innerPadding ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding( innerPadding )
            ) {

                MusicMattersNavHost(
                    navController = navController,
                    onDeleteSong = onDeleteSong,
                    snackBarHostState = snackBarHostState,
                    onNavigateToSettings = {
                        navController.navigateToSettings(
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    }
                )


                if ( showMoreDestinationsBottomSheet ) {
                    ModalBottomSheet(
                        sheetState = rememberModalBottomSheetState( skipPartiallyExpanded = true ),
                        onDismissRequest = { showMoreDestinationsBottomSheet = false }
                    ) {
                        LibraryDestination.entries.forEach {
                            val isSelected = currentlySelectedLibraryDestinationName ==
                                    it.route.qualifiedName
                            BottomSheetMenuItem(
                                leadingIcon = it.icon,
                                leadingIconContentDescription = stringResource( it.titleTextId ),
                                label = stringResource( it.titleTextId ),
                                isSelected = isSelected
                            ) {
                                showMoreDestinationsBottomSheet = false
                                currentTopLevelDestinationName = TopLevelDestination.LIBRARY
                                    .route.qualifiedName
                                navController.navigateToLibraryDestination( it )
                            }
                        }
                        Spacer( modifier = Modifier.size( 36.dp ) )
                    }
                }

                MiniPlayer (
                    onShowNowPlayingBottomSheet = { showNowPlayingScreen = true },
                    modifier = Modifier.align( Alignment.BottomCenter ),
                )

                if ( showNowPlayingScreen ) {
                    ModalBottomSheet(
                        sheetState = rememberModalBottomSheetState( skipPartiallyExpanded = true ),
                        onDismissRequest = {
                            showNowPlayingScreen = false
                        },
                        containerColor = CardDefaults.cardColors().containerColor,
                        sheetMaxWidth = Dp.Unspecified
                    ) {
                        NowPlayingScreen(
                            onViewAlbum = {},
                            onViewArtist = {},
                            onHideBottomSheet = {
                                showNowPlayingScreen = false
                            },

                            onNavigateToQueueScreen = {
                                navController.navigateToQueueScreen(
                                    navOptions = navOptions {
                                        launchSingleTop = true
                                    }
                                )
                            },
                            onNavigateToLyricsScreen = {
                                navController.navigateToLyricsScreen(
                                    navOptions = navOptions {
                                        launchSingleTop = true
                                    }
                                )
                            },
                            onLaunchEqualizerActivity = {
                                try {
                                    equalizerActivity.launch()
                                } catch ( exception: Exception ) {
                                    Timber.tag( "NOW-PLAYING-BOTTOM-BAR" ).d(
                                        "Launching equalizer failed: $exception"
                                    )
                                }
                            }
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun TransitionScreenToPortraitMode() {
    val context = LocalContext.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    // 1. Check if the screen is wide enough to be a tablet/foldable landscape view (>= 600dp)
    val isMediumOrWiderWidth = windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND )

    // 2. Check if the screen is tall enough to be a standard portrait viewport (>= 480dp)
    val isMediumOrTallerHeight = windowSizeClass
        .isHeightAtLeastBreakpoint( WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND )

    // 3. A window belongs to a normal mobile phone if:
    //    - It's narrow in portrait (!isMediumOrWiderWidth)
    //    - OR it's extremely short in landscape (!isMediumOrTallerHeight)
    val isNormalPhone = !isMediumOrWiderWidth || !isMediumOrTallerHeight

    DisposableEffect( isNormalPhone ) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation

        // Lock normal phones to portrait, but leave tablets/foldables alone
        if (isNormalPhone) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun NavHostController.navigateToTopLevelDestination( topLevelDestination: TopLevelDestination ) {
    when ( topLevelDestination ) {
        TopLevelDestination.SONGS -> navigateToSongs( navOptions = topLevelNavOptions() )
        TopLevelDestination.LIBRARY -> {}
    }
}

fun NavHostController.navigateToLibraryDestination( libraryDestination: LibraryDestination ) {
    when ( libraryDestination ) {
        LibraryDestination.ALBUMS -> navigateToAlbums( navOptions = topLevelNavOptions() )
        LibraryDestination.ARTISTS -> navigateToArtists( navOptions = topLevelNavOptions() )
        LibraryDestination.GENRES -> navigateToGenres( navOptions = topLevelNavOptions() )
        LibraryDestination.PLAYLIST -> navigateToPlaylists( navOptions = topLevelNavOptions() )
    }
}

fun NavHostController.topLevelNavOptions() = navOptions {
    // Pop up to the start destination of the graph to avoid building up a large stack
    // of destinations on the back stack as users select items.
    popUpTo( graph.findStartDestination().id ) {
//        saveState = true
    }
    // Avoid multiple copies of the same destination when re-selecting the same item.
    launchSingleTop = true
    // Restore state when re-selecting a previously selected item.
//    restoreState = true
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}






