package com.squad.musicmatters.feature.settings

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTypography
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userPreferencesDataSource: FakeUserDataRepository
    private lateinit var subject: SettingsScreenViewModel

    @Before
    fun setup() {
        userPreferencesDataSource = FakeUserDataRepository()
        subject = SettingsScreenViewModel( userPreferencesDataSource )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            SettingsScreenUiState.Loading,
            subject.uiState.value,
        )
    }

    @Test
    fun testFontChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )

        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData
            ),
            subject.uiState.value
        )

        MusicMattersTypography.all.values.forEach {
            subject.setFont( it )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        fontName = it.name
                    ),
                ),
                subject.uiState.value
            )
        }
    }

    @Test
    fun testFontScaleChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )

        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData,
            ),
            subject.uiState.value
        )
        setOf( "1", "1.5", "2", "2.5", "3", "123435" ).forEach {
            subject.setFontScale( it )
            val userData = emptyUserData.copy(
                fontScale = if ( it == "123435" ) 3f else it.toFloat()
            )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = userData
                ),
                subject.uiState.value,
            )
        }
    }

    @Test
    fun testThemeModeChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData
            ),
            subject.uiState.value
        )

        ThemeMode.entries.forEach {
            subject.setThemeMode( it )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        themeMode = it,
                    )
                ),
                subject.uiState.value
            )
        }
    }

    @Test
    fun testUseMaterialYouChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData
            ),
            subject.uiState.value
        )

        setOf( true, false ).forEach {
            subject.setUseMaterialYou( it )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        useMaterialYou = it
                    )
                ),
                subject.uiState.value
            )
        }
    }

    @Test
    fun testPrimaryColorNameChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData,
            ),
            subject.uiState.value
        )

        PrimaryThemeColors.entries.forEach {
            subject.setPrimaryColorName( it.name )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        primaryColorName = it.name
                    )
                ),
                subject.uiState.value
            )
        }
    }

    @Test
    fun testBottomBarLabelVisibilityChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData
            ),
            subject.uiState.value
        )

        BottomBarLabelVisibility.entries.forEach {
            subject.setBottomBarLabelVisibility( it )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        bottomBarLabelVisibility = it
                    )
                ),
                subject.uiState.value
            )
        }
    }

    @Test
    fun testPlayOnHeadphonesConnect() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData
            ),
            subject.uiState.value
        )

        setOf( true, false ).forEach {
            subject.setPlayOnHeadphonesConnect( it )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        playOnHeadphonesConnect = it
                    )
                ),
                subject.uiState.value
            )
        }
    }

    @Test
    fun testPauseOnHeadphonesDisconnect() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData
            ),
            subject.uiState.value
        )

        setOf( true, false ).forEach {
            subject.setPauseOnHeadphonesDisconnect( it )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        pauseOnHeadphonesDisconnect = it
                    )
                ),
                subject.uiState.value
            )
        }
    }

    @Test
    fun testMiniPlayerTextMarqueeChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData
            ),
            subject.uiState.value
        )

        setOf( true, false ).forEach {
            subject.setMiniPlayerTextMarquee( it )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        miniPlayerTextMarquee = it
                    )
                ),
                subject.uiState.value
            )
        }
    }

    @Test
    fun testShowLyricsOnSeparateScreenChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        userPreferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            SettingsScreenUiState.Success(
                userData = emptyUserData
            ),
            subject.uiState.value
        )

        setOf( true, false ).forEach {
            subject.setShowLyricsOnSeparateScreen( it )
            assertEquals(
                SettingsScreenUiState.Success(
                    userData = emptyUserData.copy(
                        showLyricsOnSeparateScreen = it
                    )
                ),
                subject.uiState.value
            )
        }
    }
}