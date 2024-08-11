package com.odesa.musicMatters.core.data.preferences

import android.content.SharedPreferences
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode

interface PreferenceStore {

    suspend fun setLanguage( localeCode: String )
    fun getLanguage(): String

    suspend fun setFontName( fontName: String )
    fun getFontName(): String

    suspend fun setFontScale( fontScale: Float )
    fun getFontScale(): Float

    suspend fun setThemeMode( themeMode: ThemeMode )
    fun getThemeMode(): ThemeMode

    fun getUseMaterialYou(): Boolean
    suspend fun setUseMaterialYou( useMaterialYou: Boolean )

    suspend fun setPrimaryColorName( primaryColorName: String )
    fun getPrimaryColorName(): String

    fun getHomePageBottomBarLabelVisibility(): HomePageBottomBarLabelVisibility
    suspend fun setHomePageBottomBarLabelVisibility( value: HomePageBottomBarLabelVisibility)

    fun getFadePlayback(): Boolean
    suspend fun setFadePlayback( fadePlayback: Boolean )

    fun getFadePlaybackDuration(): Float
    suspend fun setFadePlaybackDuration( fadePlaybackDuration: Float )

    fun getRequireAudioFocus(): Boolean
    suspend fun setRequireAudioFocus( requireAudioFocus: Boolean )

    fun getIgnoreAudioFocusLoss(): Boolean
    suspend fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean )

    fun getPlayOnHeadphonesConnect(): Boolean
    suspend fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean )

    fun getPauseOnHeadphonesDisconnect(): Boolean
    suspend fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean )

    fun getFastRewindDuration(): Int
    suspend fun setFastRewindDuration( fastRewindDuration: Int )

    fun getFastForwardDuration(): Int
    suspend fun setFastForwardDuration( fastForwardDuration: Int )

    fun getMiniPlayerShowTrackControls(): Boolean
    suspend fun setMiniPlayerShowTrackControls( showTrackControls: Boolean )

    fun getMiniPlayerShowSeekControls(): Boolean
    suspend fun setMiniPlayerShowSeekControls( showSeekControls: Boolean )

    fun getMiniPlayerTextMarquee(): Boolean
    suspend fun setMiniPlayerTextMarquee( textMarquee: Boolean )

    fun getControlsLayoutIsDefault(): Boolean
    suspend fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean )

    fun getNowPlayingLyricsLayout(): NowPlayingLyricsLayout
    suspend fun setNowPlayingLyricsLayout( nowPlayingLyricsLayout: NowPlayingLyricsLayout)

    fun getShowNowPlayingAudioInformation(): Boolean
    suspend fun setShowNowPlayingAudioInformation( showNowPlayingAudioInformation: Boolean )

    fun getShowNowPlayingSeekControls(): Boolean
    suspend fun setShowNowPlayingSeekControls( showNowPlayingSeekControls: Boolean )

    // -------------------------------------------------------------------------------------
    suspend fun setSortSongsBy( sortSongsBy: SortSongsBy)
    fun getSortSongsBy(): SortSongsBy

    suspend fun setSortSongsInReverse( sortSongsInReverse: Boolean )
    fun getSortSongsInReverse(): Boolean

    suspend fun setSortArtistsBy( sortArtistsBy: SortArtistsBy )
    fun getSortArtistsBy(): SortArtistsBy

    suspend fun setSortArtistsInReverse( sortArtistsInReverse: Boolean )
    fun getSortArtistsInReverse(): Boolean

    // --------------------------------------------------------------------------------------

    suspend fun setCurrentPlayingSpeed( currentPlayingSpeed: Float )
    fun getCurrentPlayingSpeed(): Float

    suspend fun setCurrentPlayingPitch( currentPlayingPitch: Float )
    fun getCurrentPlayingPitch(): Float

    suspend fun setCurrentLoopMode( loopMode: LoopMode  )
    fun getCurrentLoopMode(): LoopMode

    suspend fun setShuffle( shuffle: Boolean )
    fun getShuffle(): Boolean

    suspend fun setShowLyrics( showLyrics: Boolean )
    fun getShowLyrics(): Boolean

    suspend fun setCurrentlyDisabledTreePaths( paths: List<String> )
    fun getCurrentlyDisabledTreePaths(): List<String>

    suspend fun setSortGenresBy( sortGenresBy: SortGenresBy )
    fun getSortGenresBy(): SortGenresBy

    suspend fun setSortGenresInReverse( sortGenresInReverse: Boolean )
    fun getSortGenresInReverse(): Boolean

    suspend fun setSortPlaylistsBy( sortPlaylistsBy: SortPlaylistsBy )
    fun getSortPlaylistsBy(): SortPlaylistsBy

    suspend fun setSortPlaylistsInReverse( sortPlaylistsInReverse: Boolean )
    fun getSortPlaylistsInReverse(): Boolean

    suspend fun setSortAlbumsBy( sortAlbumsBy: SortAlbumsBy )
    fun getSortAlbumsBy(): SortAlbumsBy

    suspend fun setSortAlbumsInReverse( sortAlbumsInReverse: Boolean )
    fun getSortAlbumsInReverse(): Boolean

    suspend fun setSortPathsBy( sortPathsBy: SortPathsBy )
    fun getSortPathsBy(): SortPathsBy

    suspend fun setSortPathsInReverse( reverse: Boolean )
    fun getSortPathsInReverse(): Boolean

    suspend fun setCurrentlyPlayingSongId( songId: String )
    fun getCurrentlyPlayingSongId(): String

}

enum class HomePageBottomBarLabelVisibility {
    ALWAYS_VISIBLE,
    VISIBLE_WHEN_ACTIVE,
    INVISIBLE
}

enum class NowPlayingLyricsLayout {
    ReplaceArtwork,
    SeparatePage
}

enum class SortSongsBy {
    CUSTOM,
    TITLE,
    ARTIST,
    ALBUM,
    DURATION,
    DATE_ADDED,
    COMPOSER,
    YEAR,
    FILENAME,
    TRACK_NUMBER,
}

enum class SortAlbumsBy {
    CUSTOM,
    ALBUM_NAME,
    ARTIST_NAME,
    TRACKS_COUNT,
}

enum class SortGenresBy {
    CUSTOM,
    NAME,
    TRACKS_COUNT,
}

enum class SortPlaylistsBy {
    CUSTOM,
    TITLE,
    TRACKS_COUNT,
}

enum class SortPathsBy {
    CUSTOM,
    NAME,
    TRACK_COUNT
}

enum class SortArtistsBy {
    CUSTOM,
    ARTIST_NAME,
    TRACKS_COUNT,
    ALBUMS_COUNT,
}

enum class LoopMode {
    None,
    Song,
    Queue;

    companion object {
        val all = entries.toTypedArray()
    }
}

fun LoopMode.toRepeatMode() = when ( this ) {
    LoopMode.None -> REPEAT_MODE_OFF
    LoopMode.Song -> REPEAT_MODE_ONE
    LoopMode.Queue -> REPEAT_MODE_ALL
}

val allowedSpeedPitchValues = listOf( 0.5f, 1f, 1.5f, 2f, )

internal inline fun <reified T : Enum<T>> parseEnumValue( value: String ): T? =
    T::class.java.enumConstants?.find { it.name == value }

internal inline fun <reified T : Enum<T>> SharedPreferences.Editor.putEnum(
    key: String,
    value: T?
) = putString( key, value?.name )

internal inline fun <reified T : Enum<T>> SharedPreferences.getEnum(
    key: String,
    defaultValue: T?
): T? {
    var result = defaultValue
    getString( key, null )?.let { value -> result = parseEnumValue<T>( value ) }
    return result
}