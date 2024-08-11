package com.odesa.musicMatters.core.i8n

interface Language {
    val locale: String
    val nativeName: String
    val englishName: String
    val unknownSymbol: String
    val helloThere: String
    val introductoryMessage: String
    val songs: String
    val artists: String
    val albums: String
    val settings: String
    val details: String
    val path: String
    val filename: String
    val size: String
    val dateAdded: String
    val lastModified: String
    val length: String
    val bitrate: String
    val trackName: String
    val artist: String
    val album: String
    val albumArtist: String
    val composer: String
    val nothingIsBeingPlayedRightNow: String
    val addToQueue: String
    val queue: String
    val playNext: String
    val nowPlaying: String
    val language: String
    val materialYou: String
    val system: String
    val light: String
    val dark: String
    val black: String
    val viewArtist: String
    val title: String
    val duration: String
    val year: String
    val viewAlbum: String
    val searchYourMusic: String
    val noResultsFound: String
    val albumCount: String
    val trackCount: String
    val filteringResults: String
    val appearance: String
    val about: String
    val github: String
    val play: String
    val previous: String
    val next: String
    val pause: String
    val done: String
    val groove: String
    val songsFilterPattern: String
    val reset: String
    val theme: String
    val checkForUpdates: String
    val version: String
    val shufflePlay: String
    val viewAlbumArtist: String
    val stop: String
    val all: String
    val fadePlaybackInOut: String
    val requireAudioFocus: String
    val ignoreAudioFocusLoss: String
    val player: String
    val playOnHeadphonesConnect: String
    val pauseOnHeadphonesDisconnect: String
    val genre: String
    val damnThisIsSoEmpty: String
    val primaryColor: String
    val playAll: String
    val forYou: String
    val suggestedAlbums: String
    val suggestedArtists: String
    val recentlyAddedSongs: String
    val sponsorViaGithub: String
    val clearSongCache: String
    val songCacheCleared: String
    val albumArtists: String
    val genres: String
    val cancel: String
    val homeTabs: String
    val selectAtleast2orAtmost5Tabs: String
    val folders: String
    val invisible: String
    val alwaysVisible: String
    val visibleWhenActive: String
    val bottomBarLabelVisibility: String
    val playlists: String
    val newPlaylist: String
    val importPlaylist: String
    val noInAppPlaylistsFound: String
    val noLocalPlaylistsFound: String
    val custom: String
    val playlist: String
    val addSongs: String
    val addToPlaylist: String
    val isLocalPlaylist: String
    val yes: String
    val no: String
    val manageSongs: String
    val delete: String
    val deletePlaylist: String
    val trackNumber: String
    val tree: String
    val loading: String
    val name: String
    val addFolder: String
    val blacklistFolders: String
    val whitelistFolders: String
    val pickFolder: String
    val invalidM3uFile: String
    val discord: String
    val reddit: String
    val reportAnIssue: String
    val noFoldersFound: String
    val sleepTimer: String
    val hours: String
    val minutes: String
    val quitAppOnEnd: String
    val favorite: String
    val unfavorite: String
    val bitDepth: String
    val samplingRate: String
    val showAudioInformation: String
    val fastRewindDuration: String
    val fastForwardDuration: String
    val suggestedAlbumArtists: String
    val areYouSureThatYouWantToDeleteThisPlaylist: String
    val removeFromPlaylist: String
    val speed: String
    val pitch: String
    val persistUntilQueueEnd: String
    val noLyrics: String
    val sponsorViaPatreon: String
    val fDroid: String
    val izzyOnDroid: String
    val miniPlayer: String
    val showTrackControls: String
    val showSeekControls: String
    val font: String
    val codec: String
    val controlsLayout: String
    val default: String
    val traditional: String
    val enabled: String
    val disabled: String
    val showUpdateToast: String
    val sponsorViaKofi: String
    val playlistStoreLocation: String
    val appBuiltIn: String
    val localStorage: String
    val systemLightDark: String
    val systemLightBlack: String
    val fontScale: String
    val contentScale: String
    val viewGenre: String
    val Interface: String
    val rescan: String
    val updates: String
    val considerDonating: String
    val help: String
    val shareSong: String
    val pauseOnCurrentSongEnd: String
    val export: String
    val renamePlaylist: String
    val rename: String
    val equalizer: String
    val considerContributing: String
    val lyrics: String
    val lyricsLayout: String
    val replaceArtwork: String
    val separatePage: String
    val miniPlayerTextMarquee: String
    val addItem: String
    val artistTagValueSeparators: String
    val genreTagValueSeparators: String
    val discNumber: String
    val setAsRingtone: String

    fun playingXofY( x: String, y: String ): String
    fun unknownArtistX( x: String ): String
    fun xSongs( x: String ): String
    fun unknownAlbumId( id: String ): String
    fun xArtists( x: String ): String
    fun xAlbums( x: String ): String
    fun madeByX( x: String ): String
    fun newVersionAvailableX( x: String ): String
    fun xKbps( x: String ): String
    fun xSecs( x: String ): String
    fun unknownGenreX( x: String ): String
    fun xGenres( x: String ): String
    fun xFoldersYfiles( x: String, y: String ): String
    fun xItems( x: String ): String
    fun xPlaylists( x: String ): String
    fun unknownPlaylistX( x: String ): String
    fun xFolders( x: String ): String
    fun xBit( x: String ): String
    fun xKHZ( x: String ): String
    fun shareFailedX( x: String ): String
    fun exportFailedX( x: String ): String
    fun exportedX( x: String ): String
    fun launchingEqualizerFailedX( x: String ): String
    fun unknownAlbumX( x: String ): String
    fun copiedXtoClipboard( x: String ): String
}