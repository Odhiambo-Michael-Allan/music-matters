package com.squad.musicmatters.ui.navigation

//class MusicallyNavHostKtTest {
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//    private lateinit var navController: TestNavHostController
//    private lateinit var settingsRepository: SettingsRepository
//    private lateinit var playlistRepository: PlaylistRepository
//    private lateinit var searchHistoryRepository: SearchHistoryRepository
//    private lateinit var musicServiceConnection: MusicServiceConnection
//
//    @Before
//    fun setup() {
//        settingsRepository = FakeSettingsRepository()
//        playlistRepository = FakePlaylistRepository()
//        searchHistoryRepository = FakeSearchHistoryRepository()
//        musicServiceConnection = TestMusicServiceConnection()
//
//        composeTestRule.setContent {
//            navController = TestNavHostController( LocalContext.current )
//            navController.navigatorProvider.addNavigator( ComposeNavigator() )
//            MusicMattersNavHost(
//                navController = navController,
//                settingsRepository = settingsRepository,
//                musicServiceConnection = musicServiceConnection,
//                labelVisibility = SettingsDefaults.homePageBottomBarLabelVisibility,
//                language = SettingsDefaults.language,
//                playlistRepository = playlistRepository,
//                searchHistoryRepository = searchHistoryRepository,
//            )
//        }
//    }
//
//    @Test
//    fun testForYouIsTheStartDestination() {
//        composeTestRule
//            .onNodeWithText( ForYou.getLabel( English ) )
//            .assertIsDisplayed()
//    }
//
//    @Test
//    fun testNavigateToSongsScreen() {
//        composeTestRule.onRoot().printToLog( "test-navigate-to-songs-screen" )
//        clickNodeWithDescription( Songs.iconContentDescription )
//        testNodeIsDisplayedGiven(
//            hasText( Songs.getLabel( English ) ) and
//                    hasParent( hasContentDescription( "top-app-bar" ) )
//        )
//    }
//
//    @Test
//    fun testNavigateToAlbumsScreen() {
//        showMoreDestinations()
//        clickNodeWithDescription( Albums.iconContentDescription )
//        testNodeIsDisplayedGiven(
//            hasText( Albums.getLabel( English ) ) and
//                    hasParent( hasContentDescription( "top-app-bar" ) )
//        )
//    }
//
//    @Test
//    fun testNavigateToArtistsScreen() {
//        showMoreDestinations()
//        clickNodeWithDescription( Artists.iconContentDescription )
//        testNodeIsDisplayedGiven( hasText( Artists.getLabel( English ) ) and
//                hasParent( hasContentDescription( "top-app-bar" ) ) )
//    }
//
//    @Test
//    fun testNavigateToGenresScreen() {
//        showMoreDestinations()
//        clickNodeWithDescription( Genres.iconContentDescription )
//        testNodeIsDisplayedGiven(
//            hasText( Genres.getLabel( English ) ) and
//                    hasParent( hasContentDescription( "top-app-bar" ) )
//        )
//    }
//
//    @Test
//    fun testNavigateToPlaylistsScreen() {
//        showMoreDestinations()
//        clickNodeWithDescription( Playlists.iconContentDescription )
//        testNodeIsDisplayedGiven(
//            hasText( Playlists.getLabel( English ) ) and
//                    hasParent( hasContentDescription( "top-app-bar" ) )
//        )
//    }
//
//    @Test
//    fun testNavigateToTreeScreen() {
//        showMoreDestinations()
//        clickNodeWithDescription( Tree.iconContentDescription )
//        testNodeIsDisplayedGiven(
//            hasText( Tree.getLabel( English ) ) and
//                    hasParent( hasContentDescription( "top-app-bar" ) )
//        )
//    }
//
//    private fun clickNodeWithDescription( description: String ) {
//        composeTestRule
//            .onNodeWithContentDescription(
//                description,
//                useUnmergedTree = true
//            ).performClick()
//    }
//
//    private fun testNodeIsDisplayedGiven( matcher: SemanticsMatcher ) {
//        composeTestRule.onNode(
//            matcher,
//            useUnmergedTree = true
//        ).assertIsDisplayed()
//    }
//
//    private fun showMoreDestinations() {
//        composeTestRule
//            .onNodeWithContentDescription(
//                Library.iconContentDescription,
//                useUnmergedTree = true
//            ).performClick()
//    }
//}
