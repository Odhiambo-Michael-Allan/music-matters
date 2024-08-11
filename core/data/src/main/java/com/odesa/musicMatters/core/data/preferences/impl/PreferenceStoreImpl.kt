package com.odesa.musicMatters.core.data.preferences.impl

import android.content.Context
import androidx.core.content.edit
import com.odesa.musicMatters.core.data.preferences.HomePageBottomBarLabelVisibility
import com.odesa.musicMatters.core.data.preferences.LoopMode
import com.odesa.musicMatters.core.data.preferences.NowPlayingLyricsLayout
import com.odesa.musicMatters.core.data.preferences.PreferenceStore
import com.odesa.musicMatters.core.data.preferences.SortAlbumsBy
import com.odesa.musicMatters.core.data.preferences.SortArtistsBy
import com.odesa.musicMatters.core.data.preferences.SortGenresBy
import com.odesa.musicMatters.core.data.preferences.SortPathsBy
import com.odesa.musicMatters.core.data.preferences.SortPlaylistsBy
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.data.preferences.getEnum
import com.odesa.musicMatters.core.data.preferences.putEnum
import com.odesa.musicMatters.core.designsystem.theme.SupportedFonts
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.English
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PreferenceStoreImpl( private val context: Context ) : PreferenceStore {

    override suspend fun setLanguage( localeCode: String ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putString(SettingsKeys.LANGUAGE, localeCode )
            }
        }
    }

    override fun getLanguage(): String {
        val language = getSharedPreferences().getString(
            SettingsKeys.LANGUAGE, null
        )
        return language ?: SettingsDefaults.language.locale
    }

    override suspend fun setFontName( fontName: String ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putString(SettingsKeys.FONT_NAME, fontName )
            }
        }
    }

    override fun getFontName() = getSharedPreferences().getString(
        SettingsKeys.FONT_NAME, SettingsDefaults.font.name
    ) ?: SettingsDefaults.font.name

    override suspend fun setFontScale( fontScale: Float ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putFloat(SettingsKeys.FONT_SCALE, fontScale )
            }
        }
    }

    override fun getFontScale() = getSharedPreferences().getFloat(
        SettingsKeys.FONT_SCALE, SettingsDefaults.FONT_SCALE
    )

    override suspend fun setThemeMode( themeMode: ThemeMode ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putString(SettingsKeys.THEME_MODE, themeMode.name )
            }
        }
    }

    override fun getThemeMode() = getSharedPreferences().getString(SettingsKeys.THEME_MODE, null )
        ?.let { ThemeMode.valueOf( it ) }
        ?: SettingsDefaults.themeMode

    override fun getUseMaterialYou() = getSharedPreferences().getBoolean(
        SettingsKeys.USE_MATERIAL_YOU, SettingsDefaults.USE_MATERIAL_YOU
    )

    override suspend fun setUseMaterialYou( useMaterialYou: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.USE_MATERIAL_YOU, useMaterialYou )
            }
        }
    }

    override suspend fun setPrimaryColorName( primaryColorName: String ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putString(SettingsKeys.PRIMARY_COLOR_NAME, primaryColorName )
            }
        }
    }

    override fun getPrimaryColorName() = getSharedPreferences()
        .getString(SettingsKeys.PRIMARY_COLOR_NAME, null ) ?: SettingsDefaults.PRIMARY_COLOR_NAME

    override fun getHomePageBottomBarLabelVisibility() = getSharedPreferences()
        .getEnum( SettingsKeys.HOME_PAGE_BOTTOM_BAR_LABEL_VISIBILITY, null )
        ?: SettingsDefaults.homePageBottomBarLabelVisibility

    override suspend fun setHomePageBottomBarLabelVisibility( value: HomePageBottomBarLabelVisibility ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(SettingsKeys.HOME_PAGE_BOTTOM_BAR_LABEL_VISIBILITY, value )
            }
        }
    }

    override fun getFadePlayback() = getSharedPreferences().getBoolean(
        SettingsKeys.FADE_PLAYBACK,
        SettingsDefaults.FADE_PLAYBACK
    )

    override suspend fun setFadePlayback( fadePlayback: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.FADE_PLAYBACK, fadePlayback )
            }
        }
    }

    override fun getFadePlaybackDuration() = getSharedPreferences().getFloat(
        SettingsKeys.FADE_PLAYBACK_DURATION,
        SettingsDefaults.FADE_PLAYBACK_DURATION
    )

    override suspend fun setFadePlaybackDuration( fadePlaybackDuration: Float ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putFloat(SettingsKeys.FADE_PLAYBACK_DURATION, fadePlaybackDuration )
            }
        }
    }

    override fun getRequireAudioFocus() = getSharedPreferences()
        .getBoolean(SettingsKeys.REQUIRE_AUDIO_FOCUS, SettingsDefaults.REQUIRE_AUDIO_FOCUS)

    override suspend fun setRequireAudioFocus( requireAudioFocus: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.REQUIRE_AUDIO_FOCUS, requireAudioFocus )
            }
        }
    }

    override fun getIgnoreAudioFocusLoss() = getSharedPreferences()
        .getBoolean(SettingsKeys.IGNORE_AUDIO_FOCUS_LOSS, SettingsDefaults.IGNORE_AUDIO_FOCUS_LOSS)

    override suspend fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.IGNORE_AUDIO_FOCUS_LOSS, ignoreAudioFocusLoss )
            }
        }
    }

    override fun getPlayOnHeadphonesConnect() = getSharedPreferences()
        .getBoolean(
            SettingsKeys.PLAY_ON_HEADPHONES_CONNECT,
            SettingsDefaults.PLAY_ON_HEADPHONES_CONNECT
        )

    override suspend fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.PLAY_ON_HEADPHONES_CONNECT, playOnHeadphonesConnect )
            }
        }
    }

    override fun getPauseOnHeadphonesDisconnect() = getSharedPreferences()
        .getBoolean(
            SettingsKeys.PAUSE_ON_HEADPHONES_DISCONNECT,
            SettingsDefaults.PAUSE_ON_HEADPHONES_DISCONNECT
        )

    override suspend fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(
                    SettingsKeys.PAUSE_ON_HEADPHONES_DISCONNECT,
                    pauseOnHeadphonesDisconnect )
            }
        }
    }

    override fun getFastRewindDuration() = getSharedPreferences().getInt(
        SettingsKeys.FAST_REWIND_DURATION, SettingsDefaults.FAST_REWIND_DURATION
    )

    override suspend fun setFastRewindDuration( fastRewindDuration: Int ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putInt(SettingsKeys.FAST_REWIND_DURATION, fastRewindDuration )
            }
        }
    }

    override fun getFastForwardDuration() = getSharedPreferences().getInt(
        SettingsKeys.FAST_FORWARD_DURATION, SettingsDefaults.FAST_FORWARD_DURATION
    )

    override suspend fun setFastForwardDuration( fastForwardDuration: Int ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putInt(SettingsKeys.FAST_FORWARD_DURATION, fastForwardDuration )
            }
        }
    }

    override fun getMiniPlayerShowTrackControls() = getSharedPreferences()
        .getBoolean(
            SettingsKeys.MINI_PLAYER_SHOW_TRACK_CONTROLS,
            SettingsDefaults.MINI_PLAYER_SHOW_TRACK_CONTROLS
        )

    override suspend fun setMiniPlayerShowTrackControls( showTrackControls: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(
                    SettingsKeys.MINI_PLAYER_SHOW_TRACK_CONTROLS,
                    showTrackControls )
            }
        }
    }

    override fun getMiniPlayerShowSeekControls() = getSharedPreferences()
        .getBoolean(
            SettingsKeys.MINI_PLAYER_SHOW_SEEK_CONTROLS,
            SettingsDefaults.MINI_PLAYERS_SHOW_SEEK_CONTROLS
        )

    override suspend fun setMiniPlayerShowSeekControls( showSeekControls: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(
                    SettingsKeys.MINI_PLAYER_SHOW_SEEK_CONTROLS,
                    showSeekControls )
            }
        }
    }

    override fun getMiniPlayerTextMarquee() = getSharedPreferences()
        .getBoolean(
            SettingsKeys.MINI_PLAYER_TEXT_MARQUEE,
            SettingsDefaults.MINI_PLAYER_TEXT_MARQUEE
        )

    override suspend fun setMiniPlayerTextMarquee( textMarquee: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.MINI_PLAYER_TEXT_MARQUEE, textMarquee )
            }
        }
    }

    override fun getNowPlayingLyricsLayout() = getSharedPreferences().getEnum(
        SettingsKeys.NOW_PLAYING_LYRICS_LAYOUT, null
    ) ?: SettingsDefaults.nowPlayingLyricsLayout

    override suspend fun setNowPlayingLyricsLayout( nowPlayingLyricsLayout: NowPlayingLyricsLayout ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(SettingsKeys.NOW_PLAYING_LYRICS_LAYOUT, nowPlayingLyricsLayout )
            }
        }
    }

    override fun getShowNowPlayingAudioInformation() = getSharedPreferences().getBoolean(
        SettingsKeys.SHOW_NOW_PLAYING_AUDIO_INFORMATION,
        SettingsDefaults.SHOW_NOW_PLAYING_AUDIO_INFORMATION
    )

    override suspend fun setShowNowPlayingAudioInformation( showNowPlayingAudioInformation: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(
                    SettingsKeys.SHOW_NOW_PLAYING_AUDIO_INFORMATION,
                    showNowPlayingAudioInformation
                )
            }
        }
    }

    override fun getShowNowPlayingSeekControls() = getSharedPreferences().getBoolean(
        SettingsKeys.SHOW_NOW_PLAYING_SEEK_CONTROLS, SettingsDefaults.SHOW_NOW_PLAYING_SEEK_CONTROLS
    )

    override suspend fun setShowNowPlayingSeekControls( showNowPlayingSeekControls: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.SHOW_NOW_PLAYING_SEEK_CONTROLS, showNowPlayingSeekControls )
            }
        }
    }

    override suspend fun setSortSongsBy( sortSongsBy: SortSongsBy ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(SettingsKeys.SORT_SONGS_BY, sortSongsBy )
            }
        }
    }

    override fun getSortSongsBy() = getSharedPreferences()
        .getEnum(SettingsKeys.SORT_SONGS_BY, null ) ?: SettingsDefaults.sortSongsBy

    override suspend fun setSortSongsInReverse(sortSongsInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.SORT_SONGS_IN_REVERSE, sortSongsInReverse )
            }
        }
    }

    override fun getSortSongsInReverse() = getSharedPreferences()
        .getBoolean(SettingsKeys.SORT_SONGS_IN_REVERSE, SettingsDefaults.SORT_SONGS_IN_REVERSE)

    override suspend fun setSortArtistsBy( sortArtistsBy: SortArtistsBy ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(SettingsKeys.SORT_ARTISTS_BY, sortArtistsBy )
            }
        }
    }

    override fun getSortArtistsBy() = getSharedPreferences().getEnum(
        SettingsKeys.SORT_ARTISTS_BY,
        null
    ) ?: SettingsDefaults.sortArtistsBy

    override suspend fun setSortArtistsInReverse( sortArtistsInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.SORT_ARTISTS_IN_REVERSE, sortArtistsInReverse )
            }
        }
    }

    override fun getSortArtistsInReverse() = getSharedPreferences()
        .getBoolean(SettingsKeys.SORT_ARTISTS_IN_REVERSE, SettingsDefaults.SORT_ARTISTS_IN_REVERSE)

    override suspend fun setCurrentPlayingSpeed( currentPlayingSpeed: Float ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putFloat(SettingsKeys.CURRENT_PLAYING_SPEED, currentPlayingSpeed )
            }
        }
    }

    override fun getCurrentPlayingSpeed() = getSharedPreferences()
        .getFloat(SettingsKeys.CURRENT_PLAYING_SPEED, SettingsDefaults.CURRENT_PLAYING_SPEED)

    override suspend fun setCurrentPlayingPitch( currentPlayingPitch: Float ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putFloat(SettingsKeys.CURRENT_PLAYING_PITCH, currentPlayingPitch )
            }
        }
    }

    override fun getCurrentPlayingPitch() = getSharedPreferences()
        .getFloat(SettingsKeys.CURRENT_PLAYING_PITCH, SettingsDefaults.CURRENT_PLAYING_PITCH)

    override suspend fun setCurrentLoopMode( loopMode: LoopMode ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(SettingsKeys.LOOP_MODE, loopMode )
            }
        }
    }

    override fun getCurrentLoopMode() = getSharedPreferences().getEnum(
        SettingsKeys.LOOP_MODE, null
    ) ?: SettingsDefaults.loopMode

    override suspend fun setShuffle( shuffle: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.SHUFFLE, shuffle )
            }
        }
    }

    override fun getShuffle() = getSharedPreferences().getBoolean(
        SettingsKeys.SHUFFLE, SettingsDefaults.SHUFFLE
    )

    override suspend fun setShowLyrics( showLyrics: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(SettingsKeys.SHOW_LYRICS, showLyrics )
            }
        }
    }

    override fun getShowLyrics() = getSharedPreferences().getBoolean(
        SettingsKeys.SHOW_LYRICS, SettingsDefaults.SHOW_LYRICS
    )

    override fun getControlsLayoutIsDefault() = getSharedPreferences().getBoolean(
        SettingsKeys.NOW_PLAYING_CONTROLS_LAYOUT_IS_DEFAULT,
        SettingsDefaults.CONTROLS_LAYOUT_IS_DEFAULT
    )

    override suspend fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean( SettingsKeys.NOW_PLAYING_CONTROLS_LAYOUT_IS_DEFAULT, controlsLayoutIsDefault )
            }
        }
    }

    override fun getCurrentlyDisabledTreePaths(): List<String> = getSharedPreferences()
        .getStringSet( SettingsKeys.CURRENTLY_DISABLED_TREE_PATHS, null )
        ?.toList() ?: emptyList()

    override suspend fun setSortGenresBy( sortGenresBy: SortGenresBy ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(
                    SettingsKeys.SORT_GENRES_BY,
                    sortGenresBy
                )
            }
        }
    }

    override fun getSortGenresBy() = getSharedPreferences().getEnum(
        SettingsKeys.SORT_GENRES_BY,
        null,
    ) ?: SettingsDefaults.sortGenresBy

    override suspend fun setSortGenresInReverse( sortGenresInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(
                    SettingsKeys.SORT_GENRES_IN_REVERSE,
                    sortGenresInReverse
                )
            }
        }
    }

    override fun getSortGenresInReverse() = getSharedPreferences().getBoolean(
        SettingsKeys.SORT_GENRES_IN_REVERSE,
        SettingsDefaults.SORT_GENRES_IN_REVERSE
    )

    override suspend fun setSortPlaylistsBy( sortPlaylistsBy: SortPlaylistsBy ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(
                    SettingsKeys.SORT_PLAYLISTS_BY,
                    sortPlaylistsBy
                )
            }
        }
    }

    override fun getSortPlaylistsBy() = getSharedPreferences().getEnum(
        SettingsKeys.SORT_PLAYLISTS_BY,
        null
    ) ?: SettingsDefaults.sortPlaylistsBy

    override suspend fun setSortPlaylistsInReverse( sortPlaylistsInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(
                    SettingsKeys.SORT_PLAYLISTS_IN_REVERSE,
                    sortPlaylistsInReverse
                )
            }
        }
    }

    override fun getSortPlaylistsInReverse() = getSharedPreferences().getBoolean(
        SettingsKeys.SORT_PLAYLISTS_IN_REVERSE,
        SettingsDefaults.SORT_PLAYLISTS_IN_REVERSE
    )

    override suspend fun setSortAlbumsBy( sortAlbumsBy: SortAlbumsBy ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(
                    SettingsKeys.SORT_ALBUMS_BY,
                    sortAlbumsBy
                )
            }
        }
    }

    override fun getSortAlbumsBy() = getSharedPreferences().getEnum(
        SettingsKeys.SORT_ALBUMS_BY,
        null
    ) ?: SettingsDefaults.sortAlbumsBy

    override suspend fun setSortAlbumsInReverse( sortAlbumsInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(
                    SettingsKeys.SORT_ALBUMS_IN_REVERSE,
                    sortAlbumsInReverse
                )
            }
        }
    }

    override fun getSortAlbumsInReverse() = getSharedPreferences().getBoolean(
        SettingsKeys.SORT_ALBUMS_IN_REVERSE,
        SettingsDefaults.SORT_ALBUMS_IN_REVERSE
    )

    override suspend fun setSortPathsBy( sortPathsBy: SortPathsBy)  {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putEnum(
                    SettingsKeys.SORT_PATHS_BY,
                    sortPathsBy,
                )
            }
        }
    }

    override fun getSortPathsBy() = getSharedPreferences().getEnum(
        SettingsKeys.SORT_PATHS_BY,
        null
    ) ?: SettingsDefaults.sortPathsBy

    override suspend fun setSortPathsInReverse( reverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putBoolean(
                    SettingsKeys.SORT_PATHS_IN_REVERSE,
                    reverse
                )
            }
        }
    }

    override fun getSortPathsInReverse() = getSharedPreferences().getBoolean(
        SettingsKeys.SORT_PATHS_IN_REVERSE,
        SettingsDefaults.SORT_PATHS_IN_REVERSE
    )

    override suspend fun setCurrentlyPlayingSongId( songId: String ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putString(
                    SettingsKeys.CURRENTLY_PLAYING_SONG_ID,
                    songId
                )
            }
        }
    }

    override fun getCurrentlyPlayingSongId() = getSharedPreferences().getString(
        SettingsKeys.CURRENTLY_PLAYING_SONG_ID,
        ""
    ) ?: ""

    override suspend fun setCurrentlyDisabledTreePaths(paths: List<String> ) {
        withContext( Dispatchers.IO ) {
            getSharedPreferences().edit {
                putStringSet(SettingsKeys.CURRENTLY_DISABLED_TREE_PATHS, paths.toSet() )
            }
        }

    }

    private fun getSharedPreferences() = context.getSharedPreferences(
        SettingsKeys.IDENTIFIER,
        Context.MODE_PRIVATE
    )
}

object SettingsKeys {
    const val IDENTIFIER = "music_matters_settings"
    const val LANGUAGE = "language"
    const val FONT_NAME = "font_name"

    const val FONT_SCALE = "font_scale"
    const val THEME_MODE = "theme_mode"
    const val USE_MATERIAL_YOU = "use_material_you"

    const val PRIMARY_COLOR_NAME = "primary_color_name"
    const val HOME_PAGE_BOTTOM_BAR_LABEL_VISIBILITY = "home_page_bottom_bar_visibility"
    const val FADE_PLAYBACK = "fade_playback"

    const val FADE_PLAYBACK_DURATION = "fade_playback_duration"
    const val REQUIRE_AUDIO_FOCUS = "require_audio_focus"
    const val IGNORE_AUDIO_FOCUS_LOSS = "ignore_audio_focus_loss"

    const val PLAY_ON_HEADPHONES_CONNECT = "play_on_headphones_connect"
    const val PAUSE_ON_HEADPHONES_DISCONNECT = "pause_on_headphones_disconnect"
    const val FAST_FORWARD_DURATION = "fast_forward_duration"

    const val FAST_REWIND_DURATION = "fast_rewind_duration"
    const val MINI_PLAYER_SHOW_TRACK_CONTROLS = "mini_player_show_track_controls"
    const val MINI_PLAYER_SHOW_SEEK_CONTROLS = "mini_player_show_seek_controls"

    const val MINI_PLAYER_TEXT_MARQUEE = "mini_player_text_marquee"
    const val NOW_PLAYING_LYRICS_LAYOUT = "now_playing_lyrics_layout"
    const val SHOW_NOW_PLAYING_AUDIO_INFORMATION = "show_now_playing_audio_information"

    const val SHOW_NOW_PLAYING_SEEK_CONTROLS = "show_now_playing_seek_controls"
    const val SORT_SONGS_BY = "sort_songs_by"
    const val SORT_SONGS_IN_REVERSE = "sort_songs_in_reverse"

    const val SORT_ARTISTS_BY = "sort_artists_by"
    const val SORT_ARTISTS_IN_REVERSE = "sort_artists_in_reverse"
    const val NOW_PLAYING_CONTROLS_LAYOUT_IS_DEFAULT = "now_playing_controls_layout_is_default"

    const val SHOW_LYRICS = "show_lyrics"
    const val SHUFFLE = "shuffle"
    const val LOOP_MODE = "loop_mode"

    const val CURRENT_PLAYING_SPEED = "current_playing_speed"
    const val CURRENT_PLAYING_PITCH = "current_playing_pitch"
    const val CURRENTLY_DISABLED_TREE_PATHS = "currently_disabled_tree_paths"

    const val SORT_GENRES_BY = "sort_genres_by"
    const val SORT_GENRES_IN_REVERSE = "sort_genres_in_reverse"

    const val SORT_PLAYLISTS_BY = "sort_playlists_by"
    const val SORT_PLAYLISTS_IN_REVERSE = "sort_playlists_in_reverse"

    const val SORT_ALBUMS_BY = "sort_albums_by"
    const val SORT_ALBUMS_IN_REVERSE = "sort_albums_in_reverse"

    const val SORT_PATHS_BY = "sort_paths_by"
    const val SORT_PATHS_IN_REVERSE = "sort_paths_in_reverse"

    const val CURRENTLY_PLAYING_SONG_ID = "currently_playing_song_id"
}

object SettingsDefaults {
    val language = English
    val font = SupportedFonts.ProductSans
    const val FONT_SCALE = 1.0f

    val themeMode = ThemeMode.SYSTEM
    const val USE_MATERIAL_YOU = true
    const val PRIMARY_COLOR_NAME = "Green"

    val homePageBottomBarLabelVisibility = HomePageBottomBarLabelVisibility.ALWAYS_VISIBLE
    const val FADE_PLAYBACK = false
    const val FADE_PLAYBACK_DURATION = 1f

    const val REQUIRE_AUDIO_FOCUS = true
    const val IGNORE_AUDIO_FOCUS_LOSS = false
    const val PLAY_ON_HEADPHONES_CONNECT = false

    const val PAUSE_ON_HEADPHONES_DISCONNECT = true
    const val FAST_FORWARD_DURATION = 30
    const val FAST_REWIND_DURATION = 15

    const val MINI_PLAYER_SHOW_TRACK_CONTROLS = true
    const val MINI_PLAYERS_SHOW_SEEK_CONTROLS = false
    const val MINI_PLAYER_TEXT_MARQUEE = true

    const val CONTROLS_LAYOUT_IS_DEFAULT = true
    val nowPlayingLyricsLayout = NowPlayingLyricsLayout.ReplaceArtwork
    const val SHOW_NOW_PLAYING_AUDIO_INFORMATION = false

    const val SHOW_NOW_PLAYING_SEEK_CONTROLS = false
    val sortSongsBy = SortSongsBy.TITLE
    const val SORT_SONGS_IN_REVERSE = false

    val sortArtistsBy = SortArtistsBy.ARTIST_NAME
    const val SORT_ARTISTS_IN_REVERSE = false
    const val SHOW_LYRICS = false

    const val SHUFFLE = false
    val loopMode = LoopMode.None
    const val CURRENT_PLAYING_SPEED = 1f

    const val CURRENT_PLAYING_PITCH = 1f
    val sortGenresBy = SortGenresBy.NAME
    const val SORT_GENRES_IN_REVERSE = false

    val sortPlaylistsBy = SortPlaylistsBy.TITLE
    const val SORT_PLAYLISTS_IN_REVERSE = false

    val sortAlbumsBy = SortAlbumsBy.ALBUM_NAME
    const val SORT_ALBUMS_IN_REVERSE = false

    val sortPathsBy = SortPathsBy.NAME
    const val SORT_PATHS_IN_REVERSE = false
}

