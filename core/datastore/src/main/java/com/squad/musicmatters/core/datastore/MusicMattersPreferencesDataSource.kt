package com.squad.musicmatters.core.datastore

import androidx.datastore.core.DataStore
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.LyricsLayout
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.model.SortGenresBy
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.model.UserData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusicMattersPreferencesDataSource @Inject constructor(
    private val userPreferencesDataStore: DataStore<UserPreferences>
) : PreferencesDataSource {

    override val userData = userPreferencesDataStore.data.map {
        UserData(
            language = English,
            fontName = it.fontName.takeIf { fontName -> fontName.isNotEmpty() } ?: DefaultPreferences.FONT_NAME,
            fontScale = it.fontScale.takeIf { fontScale -> fontScale > 0 } ?: DefaultPreferences.FONT_SCALE,
            themeMode = when ( it.themeMode ) {
                null, ThemeModeProto.THEME_MODE_UNSPECIFIED, ThemeModeProto.UNRECOGNIZED, ThemeModeProto.THEME_MODE_FOLLOW_SYSTEM -> DefaultPreferences.THEME_MODE
                ThemeModeProto.THEME_MODE_DARK -> ThemeMode.DARK
                ThemeModeProto.THEME_MODE_BLACK -> ThemeMode.BLACK
                ThemeModeProto.THEME_MODE_LIGHT -> ThemeMode.LIGHT
            },
            useMaterialYou = if ( it.useMaterialYouSet.not() ) { true } else it.useMaterialYou,
            primaryColorName = it.primaryColorName.takeIf { name -> name.isNotEmpty() } ?: DefaultPreferences.PRIMARY_COLOR_NAME,
            bottomBarLabelVisibility = when ( it.bottomBarLabelVisibility ) {
                null, BottomBarLabelVisibilityProto.VISIBILITY_UNSPECIFIED, BottomBarLabelVisibilityProto.UNRECOGNIZED -> DefaultPreferences.BOTTOM_BAR_LABEL_VISIBILITY
                BottomBarLabelVisibilityProto.ALWAYS_VISIBLE -> BottomBarLabelVisibility.ALWAYS_VISIBLE
                BottomBarLabelVisibilityProto.VISIBLE_WHEN_ACTIVE -> BottomBarLabelVisibility.VISIBLE_WHEN_ACTIVE
                BottomBarLabelVisibilityProto.INVISIBLE -> BottomBarLabelVisibility.INVISIBLE
            },
            fadePlayback = it.fadePlayback,
            fadePlaybackDuration = it.fadePlaybackDuration.takeIf { duration -> duration > 0 } ?: DefaultPreferences.FADE_PLAYBACK_DURATION,
            requireAudioFocus = it.requireAudioFocus,
            ignoreAudioFocusLoss = it.ignoreAudioFocusLoss,
            playOnHeadphonesConnect = it.playOnHeadphonesConnect,
            pauseOnHeadphonesDisconnect = it.pauseOnHeadphonesDisconnect,
            fastRewindDuration = it.fastRewindDuration.takeIf { duration -> duration > 0 } ?: DefaultPreferences.FAST_REWIND_DURATION,
            fastForwardDuration = it.fastForwardDuration.takeIf { duration -> duration > 0 } ?: DefaultPreferences.FAST_FORWARD_DURATION,
            miniPlayerShowTrackControls = it.miniPlayerShowTrackControls,
            miniPlayerShowSeekControls = it.miniPlayerShowSeekControls,
            miniPlayerTextMarquee = it.miniPlayerTextMarquee,
            lyricsLayout = when ( it.lyricsLayout ) {
                null, LyricsLayoutProto.LYRICS_LAYOUT_UNSPECIFIED, LyricsLayoutProto.UNRECOGNIZED -> DefaultPreferences.LYRICS_LAYOUT
                LyricsLayoutProto.REPLACE_ARTWORK -> LyricsLayout.REPLACE_ARTWORK
                LyricsLayoutProto.SEPARATE_PAGE -> LyricsLayout.SEPARATE_PAGE
            },
            showNowPlayingAudioInformation = it.showNowPlayingAudioInformation,
            showNowPlayingSeekControls = it.showNowPlayingSeekControls,
            playbackSpeed = it.playbackSpeed.takeIf { speed -> speed > 0 } ?: DefaultPreferences.PLAYBACK_SPEED,
            playbackPitch = it.playbackPitch.takeIf { pitch -> pitch > 0 } ?: DefaultPreferences.PLAYBACK_PITCH,
            loopMode = when ( it.loopMode ) {
                null, LoopModeProto.LOOP_MODE_UNSPECIFIED, LoopModeProto.UNRECOGNIZED -> DefaultPreferences.LOOP_MODE
                LoopModeProto.NONE -> LoopMode.None
                LoopModeProto.SONG -> LoopMode.Song
                LoopModeProto.QUEUE -> LoopMode.Queue
            },
            shuffle = it.shuffle,
            showLyrics = it.showLyrics,
            controlsLayoutDefault = it.nowPlayingControlsLayoutDefault,
            currentlyDisabledTreePaths = it.disabledTreePathsMap.keys,
            sortSongsBy = when ( it.sortSongsBy ) {
                null, SortSongsByProto.SORT_SONGS_UNSPECIFIED, SortSongsByProto.UNRECOGNIZED -> DefaultPreferences.SORT_SONGS_BY
                SortSongsByProto.SONGS_CUSTOM -> SortSongsBy.CUSTOM
                SortSongsByProto.SONGS_ARTIST -> SortSongsBy.ARTIST
                SortSongsByProto.SONGS_COMPOSER -> SortSongsBy.COMPOSER
                SortSongsByProto.SONGS_DURATION -> SortSongsBy.DURATION
                SortSongsByProto.SONGS_DATE_ADDED -> SortSongsBy.DATE_ADDED
                SortSongsByProto.SONGS_ALBUM -> SortSongsBy.ALBUM
                SortSongsByProto.SONGS_FILENAME -> SortSongsBy.FILENAME
                SortSongsByProto.SONGS_TITLE -> SortSongsBy.TITLE
                SortSongsByProto.SONGS_TRACK_NUMBER -> SortSongsBy.TRACK_NUMBER
                SortSongsByProto.SONGS_YEAR -> SortSongsBy.YEAR
            },
            sortSongsReverse = it.sortSongsReverse,
            sortArtistsBy = when ( it.sortArtistsBy ) {
                null, SortArtistsByProto.ARTISTS_UNSPECIFIED, SortArtistsByProto.UNRECOGNIZED -> DefaultPreferences.SORT_ARTISTS_BY
                SortArtistsByProto.ARTISTS_CUSTOM -> SortArtistsBy.CUSTOM
                SortArtistsByProto.ARTISTS_ARTIST_NAME -> SortArtistsBy.ARTIST_NAME
                SortArtistsByProto.ARTISTS_ALBUM_COUNT -> SortArtistsBy.ALBUM_COUNT
                SortArtistsByProto.ARTISTS_TRACK_COUNT -> SortArtistsBy.TRACK_COUNT
            },
            sortArtistsReverse = it.sortArtistsReverse,
            sortGenresBy = when ( it.sortGenresBy ) {
                null, SortGenresByProto.GENRES_UNSPECIFIED, SortGenresByProto.UNRECOGNIZED -> DefaultPreferences.SORT_GENRES_BY
                SortGenresByProto.GENRES_CUSTOM -> SortGenresBy.CUSTOM
                SortGenresByProto.GENRES_NAME -> SortGenresBy.NAME
                SortGenresByProto.GENRES_TRACK_COUNT -> SortGenresBy.TRACK_COUNT
            },
            sortGenresReverse = it.sortGenresReverse,
            sortPlaylistsBy = when ( it.sortPlaylistsBy ) {
                null, SortPlaylistsByProto.PLAYLISTS_UNSPECIFIED, SortPlaylistsByProto.UNRECOGNIZED -> DefaultPreferences.SORT_PLAYLISTS_BY
                SortPlaylistsByProto.PLAYLISTS_CUSTOM -> SortPlaylistsBy.CUSTOM
                SortPlaylistsByProto.PLAYLISTS_TITLE -> SortPlaylistsBy.TITLE
                SortPlaylistsByProto.PLAYLISTS_TRACK_COUNT -> SortPlaylistsBy.TRACK_COUNT
            },
            sortPlaylistsReverse = it.sortPlaylistsReverse,
            sortAlbumsBy = when ( it.sortAlbumsBy ) {
                null, SortAlbumsByProto.SORT_ALBUMS_BY_UNSPECIFIED, SortAlbumsByProto.UNRECOGNIZED -> DefaultPreferences.SORT_ALBUMS_BY
                SortAlbumsByProto.ALBUMS_CUSTOM -> SortAlbumsBy.CUSTOM
                SortAlbumsByProto.ALBUMS_ALBUM_NAME -> SortAlbumsBy.ALBUM_NAME
                SortAlbumsByProto.ALBUMS_ARTIST_NAME -> SortAlbumsBy.ARTIST_NAME
                SortAlbumsByProto.ALBUMS_TRACK_COUNT -> SortAlbumsBy.TRACK_COUNT
            },
            sortAlbumsReverse = it.sortAlbumsReverse,
            sortPathsBy = when ( it.sortPathsBy ) {
                null, SortPathsByProto.PATHS_UNSPECIFIED, SortPathsByProto.UNRECOGNIZED -> DefaultPreferences.SORT_PATHS_BY
                SortPathsByProto.PATHS_CUSTOM -> SortPathsBy.CUSTOM
                SortPathsByProto.PATHS_NAME -> SortPathsBy.NAME
                SortPathsByProto.PATHS_TRACK_COUNT -> SortPathsBy.TRACK_COUNT
            },
            sortPathsReverse = it.sortPathsReverse,
            currentlyPlayingSongId = it.currentlyPlayingSongId,
        )
    }

    override suspend fun setFontName( fontName: String ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.fontName = fontName
            }
        }
    }

    override suspend fun setFontScale( fontScale: Float ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.fontScale = fontScale
            }
        }
    }

    override suspend fun setThemeMode( themeMode: ThemeMode ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.themeMode = when ( themeMode ) {
                    ThemeMode.FOLLOW_SYSTEM -> ThemeModeProto.THEME_MODE_FOLLOW_SYSTEM
                    ThemeMode.LIGHT -> ThemeModeProto.THEME_MODE_LIGHT
                    ThemeMode.DARK -> ThemeModeProto.THEME_MODE_DARK
                    ThemeMode.BLACK -> ThemeModeProto.THEME_MODE_BLACK
                }
            }
        }
    }

    override suspend fun useMaterialYou( useMaterialYou: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.useMaterialYou = useMaterialYou
                this.useMaterialYouSet = true
            }
        }
    }

    override suspend fun setPrimaryColorName( primaryColorName: String ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.primaryColorName = primaryColorName
            }
        }
    }

    override suspend fun setBottomBarLabelVisibility( visibility: BottomBarLabelVisibility ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.bottomBarLabelVisibility = when ( visibility ) {
                    BottomBarLabelVisibility.ALWAYS_VISIBLE -> BottomBarLabelVisibilityProto.ALWAYS_VISIBLE
                    BottomBarLabelVisibility.VISIBLE_WHEN_ACTIVE -> BottomBarLabelVisibilityProto.VISIBLE_WHEN_ACTIVE
                    BottomBarLabelVisibility.INVISIBLE -> BottomBarLabelVisibilityProto.INVISIBLE
                }
            }
        }
    }

    override suspend fun setFadePlayback( fadePlayback: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.fadePlayback = fadePlayback
            }
        }
    }

    override suspend fun setFadePlaybackDuration( fadePlaybackDuration: Float ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.fadePlaybackDuration = fadePlaybackDuration
            }
        }
    }

    override suspend fun setRequireAudioFocus( requireAudioFocus: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.requireAudioFocus = requireAudioFocus
            }
        }
    }

    override suspend fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.ignoreAudioFocusLoss = ignoreAudioFocusLoss
            }
        }
    }

    override suspend fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.playOnHeadphonesConnect = playOnHeadphonesConnect
            }
        }
    }

    override suspend fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.pauseOnHeadphonesDisconnect = pauseOnHeadphonesDisconnect
            }
        }
    }

    override suspend fun setFastRewindDuration( fastRewindDuration: Int ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.fastRewindDuration = fastRewindDuration
            }
        }
    }

    override suspend fun setFastForwardDuration( fastForwardDuration: Int ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.fastForwardDuration = fastForwardDuration
            }
        }
    }

    override suspend fun setMiniPlayerTextMarquee( miniPlayerTextMarquee: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.miniPlayerTextMarquee = miniPlayerTextMarquee
            }
        }
    }

    override suspend fun setMiniPlayerShowSeekControls( miniPlayerShowSeekControls: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.miniPlayerShowSeekControls = miniPlayerShowSeekControls
            }
        }
    }

    override suspend fun setMiniPlayerShowTrackControls( miniPlayerShowTrackControls: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.miniPlayerShowTrackControls = miniPlayerShowTrackControls
            }
        }
    }

    override suspend fun setLyricsLayout( lyricsLayout: LyricsLayout ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.lyricsLayout = when ( lyricsLayout ) {
                    LyricsLayout.SEPARATE_PAGE -> LyricsLayoutProto.SEPARATE_PAGE
                    LyricsLayout.REPLACE_ARTWORK -> LyricsLayoutProto.REPLACE_ARTWORK
                }
            }
        }
    }

    override suspend fun setShowNowPlayingAudioInformation( showNowPlayingAudioInformation: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.showNowPlayingAudioInformation = showNowPlayingAudioInformation
            }
        }
    }

    override suspend fun setShowNowPlayingSeekControls( showNowPlayingSeekControls: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.showNowPlayingSeekControls = showNowPlayingSeekControls
            }
        }
    }

    override suspend fun setPlaybackSpeed( playbackSpeed: Float ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.playbackSpeed = playbackSpeed
            }
        }
    }

    override suspend fun setPlaybackPitch( playbackPitch: Float ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.playbackPitch = playbackPitch
            }
        }
    }

    override suspend fun setLoopMode( loopMode: LoopMode ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.loopMode = when ( loopMode ) {
                    LoopMode.None -> LoopModeProto.NONE
                    LoopMode.Song -> LoopModeProto.SONG
                    LoopMode.Queue -> LoopModeProto.QUEUE
                }
            }
        }
    }

    override suspend fun setShuffle( shuffle: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.shuffle = shuffle
            }
        }
    }

    override suspend fun setShowLyrics( showLyrics: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy { this.showLyrics = showLyrics }
        }
    }

    override suspend fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy { this.nowPlayingControlsLayoutDefault = controlsLayoutIsDefault }
        }
    }

    override suspend fun setDisabledTreePaths( disabledTreePaths: Set<String> ) {
        userPreferencesDataStore.updateData {
            it.copy {
                disabledTreePaths.forEach {
                    this.disabledTreePaths.put( it, true )
                }
            }
        }
    }

    override suspend fun setSortSongsBy( by: SortSongsBy ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortSongsBy = when ( by ) {
                    SortSongsBy.YEAR -> SortSongsByProto.SONGS_YEAR
                    SortSongsBy.CUSTOM -> SortSongsByProto.SONGS_CUSTOM
                    SortSongsBy.FILENAME -> SortSongsByProto.SONGS_FILENAME
                    SortSongsBy.DURATION -> SortSongsByProto.SONGS_DURATION
                    SortSongsBy.COMPOSER -> SortSongsByProto.SONGS_COMPOSER
                    SortSongsBy.TITLE -> SortSongsByProto.SONGS_TITLE
                    SortSongsBy.TRACK_NUMBER -> SortSongsByProto.SONGS_TRACK_NUMBER
                    SortSongsBy.DATE_ADDED -> SortSongsByProto.SONGS_DATE_ADDED
                    SortSongsBy.ALBUM -> SortSongsByProto.SONGS_ALBUM
                    SortSongsBy.ARTIST -> SortSongsByProto.SONGS_ARTIST
                }
            }
        }
    }

    override suspend fun setSortSongsInReverse( sortSongsInReverse: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortSongsReverse = sortSongsInReverse
            }
        }
    }

    override suspend fun setSortArtistsBy( by: SortArtistsBy ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortArtistsBy = when ( by ) {
                    SortArtistsBy.CUSTOM -> SortArtistsByProto.ARTISTS_CUSTOM
                    SortArtistsBy.ARTIST_NAME -> SortArtistsByProto.ARTISTS_ARTIST_NAME
                    SortArtistsBy.TRACK_COUNT -> SortArtistsByProto.ARTISTS_TRACK_COUNT
                    SortArtistsBy.ALBUM_COUNT -> SortArtistsByProto.ARTISTS_ALBUM_COUNT
                }
            }
        }
    }

    override suspend fun setSortArtistsInReverse( reverse: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortArtistsReverse = reverse
            }
        }
    }

    override suspend fun setSortGenresBy( by: SortGenresBy ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortGenresBy = when ( by ) {
                    SortGenresBy.CUSTOM -> SortGenresByProto.GENRES_CUSTOM
                    SortGenresBy.NAME -> SortGenresByProto.GENRES_NAME
                    SortGenresBy.TRACK_COUNT -> SortGenresByProto.GENRES_TRACK_COUNT
                }
            }
        }
    }

    override suspend fun setSortGenresInReverse( reverse: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortGenresReverse = reverse
            }
        }
    }

    override suspend fun setSortPlaylistsBy( by: SortPlaylistsBy ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortPlaylistsBy = when ( by ) {
                    SortPlaylistsBy.CUSTOM -> SortPlaylistsByProto.PLAYLISTS_CUSTOM
                    SortPlaylistsBy.TITLE -> SortPlaylistsByProto.PLAYLISTS_TITLE
                    SortPlaylistsBy.TRACK_COUNT -> SortPlaylistsByProto.PLAYLISTS_TRACK_COUNT
                }
            }
        }
    }

    override suspend fun setSortPlaylistsInReverse( reverse: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortPlaylistsReverse = reverse
            }
        }
    }

    override suspend fun setSortAlbumsBy( by: SortAlbumsBy ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortAlbumsBy = when ( by ) {
                    SortAlbumsBy.CUSTOM -> SortAlbumsByProto.ALBUMS_CUSTOM
                    SortAlbumsBy.ALBUM_NAME -> SortAlbumsByProto.ALBUMS_ALBUM_NAME
                    SortAlbumsBy.ARTIST_NAME -> SortAlbumsByProto.ALBUMS_ARTIST_NAME
                    SortAlbumsBy.TRACK_COUNT -> SortAlbumsByProto.ALBUMS_TRACK_COUNT
                }
            }
        }
    }

    override suspend fun setSortAlbumsInReverse( reverse: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortAlbumsReverse = reverse
            }
        }
    }

    override suspend fun setSortPathsBy( by: SortPathsBy ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortPathsBy = when ( by ) {
                    SortPathsBy.CUSTOM -> SortPathsByProto.PATHS_CUSTOM
                    SortPathsBy.NAME -> SortPathsByProto.PATHS_NAME
                    SortPathsBy.TRACK_COUNT -> SortPathsByProto.PATHS_TRACK_COUNT
                }
            }
        }
    }

    override suspend fun setSortPathsInReverse( reverse: Boolean ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.sortPathsReverse = reverse
            }
        }
    }

    override suspend fun setCurrentlyPlayingSongId( songId: String ) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.currentlyPlayingSongId = songId
            }
        }
    }

}

object DefaultPreferences {
    const val FONT_NAME = "Google Sans"
    const val FONT_SCALE = 1.0f
    val THEME_MODE = ThemeMode.FOLLOW_SYSTEM
    const val PRIMARY_COLOR_NAME = "Blue"
    val BOTTOM_BAR_LABEL_VISIBILITY = BottomBarLabelVisibility.ALWAYS_VISIBLE
    const val FADE_PLAYBACK_DURATION = 1f
    const val FAST_FORWARD_DURATION = 30
    const val FAST_REWIND_DURATION = 15
    val LYRICS_LAYOUT = LyricsLayout.REPLACE_ARTWORK
    val SORT_SONGS_BY = SortSongsBy.TITLE

    val SORT_ARTISTS_BY = SortArtistsBy.ARTIST_NAME
    val LOOP_MODE = LoopMode.None
    const val PLAYBACK_SPEED = 1f
    const val PLAYBACK_PITCH = 1f
    val SORT_GENRES_BY = SortGenresBy.NAME
    val SORT_PLAYLISTS_BY = SortPlaylistsBy.TITLE
    val SORT_ALBUMS_BY = SortAlbumsBy.ALBUM_NAME
    val SORT_PATHS_BY = SortPathsBy.NAME
}