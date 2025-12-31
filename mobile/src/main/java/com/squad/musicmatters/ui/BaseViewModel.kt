package com.squad.musicmatters.ui

//open class BaseViewModel(
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//    private val playlistRepository: PlaylistRepository,
//    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
//) : ViewModel() {
//
//    private val songsFuzzySearcher: FuzzySearcher<String> = FuzzySearcher(
//        options = listOf(
//            FuzzySearchOption( { v -> getSongWithId( v )?.title?.let { compareString( it ) } }, 3 ),
//            FuzzySearchOption( { v -> getSongWithId( v )?.path?.let { compareString( it ) } }, 2 ),
//            FuzzySearchOption( { v -> getSongWithId( v )?.artists?.let { compareCollection( it ) } } ),
//            FuzzySearchOption( { v -> getSongWithId( v )?.albumTitle?.let { compareString( it ) } } )
//        )
//    )
//
//    private val playlistsChangeListeners: MutableList<( List<PlaylistInfo> )->Unit> = mutableListOf()
//    private val sortSongsByChangeListeners: MutableList<( SortSongsBy, Boolean )->Unit> = mutableListOf()
//    private val additionalMetadataListeners: MutableList<( List<SongAdditionalMetadataInfo> ) -> Unit> = mutableListOf()
//    private var songsAdditionalMetadataList = listOf<SongAdditionalMetadataInfo>()
//
//    init {
//        viewModelScope.launch { observePlaylists() }
//        viewModelScope.launch { observeSortSongsBy() }
//        viewModelScope.launch { observeSortSongsInReverse() }
//        viewModelScope.launch { observeSongsAdditionalMetadata() }
//    }
//
//    private suspend fun observePlaylists() {
////        playlistRepository.playlists.collect {
////            playlistsChangeListeners.forEach { listener ->
////                listener.invoke( getEditablePlaylists() )
////            }
////        }
//    }
//
//    private suspend fun observeSortSongsBy() {
//        settingsRepository.sortSongsBy.collect { sortSongsBy ->
//            sortSongsByChangeListeners.forEach { listener ->
//                listener.invoke( sortSongsBy, settingsRepository.sortSongsInReverse.value )
//            }
//        }
//    }
//
//    private suspend fun observeSortSongsInReverse() {
//        settingsRepository.sortSongsInReverse.collect { sortSongsInReverse ->
//            sortSongsByChangeListeners.forEach { listener ->
//                listener.invoke( settingsRepository.sortSongsBy.value, sortSongsInReverse )
//            }
//        }
//    }
//
//    private suspend fun observeSongsAdditionalMetadata() {
////        withContext( Dispatchers.IO ) {
////            songsAdditionalMetadataRepository.fetchAdditionalMetadataEntries().collect { additionalMetadata ->
////                songsAdditionalMetadataList = additionalMetadata.mapNotNull { it.asDomain() }
////                additionalMetadataListeners.forEach {
////                    it.invoke( songsAdditionalMetadataList )
////                }
////            }
////        }
//    }
//
//    fun addOnPlaylistsChangeListener( listener: ( List<PlaylistInfo>) -> Unit ) {
//        playlistsChangeListeners.add( listener )
//        // Supply the newly added listener with the currently editable playlists..
//        listener.invoke( getEditablePlaylists() )
//    }
//
//    fun addOnSongsMetadataListChangeListener( listener: ( List<SongAdditionalMetadataInfo>) -> Unit ) {
//        additionalMetadataListeners.add( listener )
//        listener.invoke( songsAdditionalMetadataList )
//    }
//
//    fun addOnSortSongsByChangeListener( listener: ( SortSongsBy, Boolean ) -> Unit ) {
//        sortSongsByChangeListeners.add( listener )
//    }
//
////    fun deleteSong( song: Song ) {}
//
//    fun setSortSongsBy( sortSongsBy: SortSongsBy ) {
//        viewModelScope.launch { settingsRepository.setSortSongsBy( sortSongsBy ) }
//    }
//
//    fun setSortSongsInReverse( reverse: Boolean ) {
//        viewModelScope.launch { settingsRepository.setSortSongsInReverse( reverse ) }
//    }
//
//    fun isPlaylistDeletable(playlistInfo: PlaylistInfo ) = getDeletablePlaylists().contains( playlistInfo )
//
//    private fun getDeletablePlaylists(): List<PlaylistInfo> = emptyList()
////        playlistRepository.playlists.value.filter {
////            it.id != playlistRepository.mostPlayedSongsPlaylistInfo.value.id &&
////                    it.id != playlistRepository.recentlyPlayedSongsPlaylistInfo.value.id &&
////                    it.id != playlistRepository.favoritesPlaylistInfo.value.id
////        }
//
//    fun renamePlaylist( playlistInfo: PlaylistInfo, newName: String ) {
//        viewModelScope.launch { playlistRepository.renamePlaylist( playlistInfo, newName ) }
//    }
//
//    fun deletePlaylist(playlistInfo: PlaylistInfo ) {
//        viewModelScope.launch { playlistRepository.deletePlaylist( playlistInfo ) }
//    }
//
//    fun addToFavorites( songId: String ) {
//        viewModelScope.launch { playlistRepository.addToFavorites( songId ) }
//    }
//
//    fun addSongsToPlaylist(
//        playlistInfo: PlaylistInfo,
//        songs: List<Song>
//    ) {
//        viewModelScope.launch {
//            songs.forEach {
//                playlistRepository.addSongIdToPlaylist( it.id, playlistInfo.id )
//            }
//        }
//    }
//
//    fun playSongs(
//        selectedSong: Song,
//        songsInPlaylist: List<Song>
//    ) {
//        musicServiceConnection.playMediaItem(
//            mediaItem = selectedSong.toMediaItem(),
//            mediaItems = songsInPlaylist.map { it.toMediaItem() },
//            shuffle = settingsRepository.shuffle.value
//        )
//    }
//
//    fun shufflePlaySongsInAlbum( album: Album ) {
//        shuffleAndPlay(
//            songs = getSongsInAlbum( album )
//        )
//    }
//
//    fun shufflePlaySongsByArtist( artist: Artist ) {
//        shuffleAndPlay(
//            songs = getSongsByArtist( artist )
//        )
//    }
//
//    fun shuffleAndPlay(
//        songs: List<Song>,
//    ) {
//        if ( songs.isEmpty() ) return
//        musicServiceConnection.shuffleAndPlay(
//            songs.map { it.toMediaItem() }
//        )
//    }
//
//    fun playSong( song: Song ) {
//        musicServiceConnection.playMediaItem(
//            mediaItem = song.toMediaItem(),
//            mediaItems = listOf( song.toMediaItem() ),
//            shuffle = false // Its only one, no need to shuffle..
//        )
//    }
//
//    fun playSongsNext( songs: List<Song> ) {
//        songs.forEach { playSongNext( it ) }
//    }
//
//    fun playSongNext(
//        song: Song
//    ) {
//        musicServiceConnection.playNext( song.toMediaItem() )
//    }
//
//    fun addSongsToQueue( songs: List<Song> ) {
//        songs.forEach { addSongToQueue( it ) }
//    }
//
//    fun addSongToQueue( song: Song ) {
//        musicServiceConnection.addToQueue( song.toMediaItem() )
//    }
//
//    fun searchSongsMatching( query: String ) =
//        songsFuzzySearcher.search(
//            terms = query,
//            entities = musicServiceConnection.cachedSongs.value.map { it.id }
//        ).mapNotNull { getSongWithId( it.entity ) }
//
//    private fun getEditablePlaylists() = emptyList<PlaylistInfo>()
////        playlistRepository.playlists.value.filter {
////        it.id != playlistRepository.mostPlayedSongsPlaylistInfo.value.id &&
////                it.id != playlistRepository.recentlyPlayedSongsPlaylistInfo.value.id
////    }
//
//    fun createPlaylist(
//        playlistTitle: String,
//        songsToAddToPlaylist: List<Song>
//    ) {
//        viewModelScope.launch {
//            playlistRepository.savePlaylist(
//                PlaylistInfo(
//                    id = UUID.randomUUID().toString(),
//                    title = playlistTitle,
//                    songIds = songsToAddToPlaylist.map { it.id }
//                )
//            )
//        }
//    }
//
//    private fun getSongWithId( id: String ) =
//        musicServiceConnection.cachedSongs.value.find { it.id == id }
//
//    fun playSongsInAlbum( album: Album ) {
//        playSongs(
//            songs = getSongsInAlbumAsMediaItems( album ),
//            shuffle = settingsRepository.shuffle.value
//        )
//    }
//
//    fun playSongsInAlbumNext( album: Album ) {
//        playMediaItemsNext( getSongsInAlbumAsMediaItems( album ) )
//    }
//
//
//    fun addSongsInAlbumToQueue( album: Album ) {
//        addMediaItemsToQueue( getSongsInAlbumAsMediaItems( album ) )
//    }
//
//    fun playSongsInPlaylist(playlistInfo: PlaylistInfo ) {
//        playSongs(
//            songs = getSongsInPlaylist( playlistInfo ).map { it.toMediaItem() },
//            shuffle = settingsRepository.shuffle.value
//        )
//    }
//
//    fun getSongsInPlaylist(playlistInfo: PlaylistInfo ) =
//        musicServiceConnection.cachedSongs.value.filter {
//            playlistInfo.songIds.contains( it.id )
//        }
//
//    fun playSongsByArtist(
//        artist: Artist
//    ) {
//        playSongsByArtist(
//            artist = artist,
//            shuffle = settingsRepository.shuffle.value
//        )
//    }
//
//    private fun playSongsByArtist(
//        artist: Artist,
//        shuffle: Boolean = false
//    ) {
//        playSongs(
//            songs = getSongsByArtistAsMediaItems( artist ),
//            shuffle = shuffle
//        )
//    }
//
//    fun addSongsByArtistToQueue( artist: Artist) {
//        addMediaItemsToQueue( getSongsByArtistAsMediaItems( artist ) )
//    }
//
//    private fun playSongs(
//        songs: List<MediaItem>,
//        shuffle: Boolean
//    ) {
//        if ( songs.isEmpty() ) return
//        musicServiceConnection.playMediaItem(
//            mediaItem = if( shuffle ) songs.random() else songs.first(),
//            mediaItems = songs,
//            shuffle = shuffle
//        )
//    }
//
//    private fun playMediaItemsNext(
//        songs: List<MediaItem>
//    ) {
//        songs.forEach {
//            musicServiceConnection.playNext( it )
//        }
//    }
//
//    fun playSongsByArtistNext( artist: Artist ) {
//        playMediaItemsNext(
//            songs = getSongsByArtistAsMediaItems( artist )
//        )
//    }
//
//    private fun addMediaItemsToQueue(
//        songs: List<MediaItem>
//    ) {
//        songs.forEach {
//            musicServiceConnection.addToQueue( it )
//        }
//    }
//
//    private fun getSongsByArtistAsMediaItems( artist: Artist ) =
//        getSongsByArtist( artist )
//            .map { it.toMediaItem() }
//
//    fun getSongsByArtist( artist: Artist ) =
//        musicServiceConnection.cachedSongs.value
//            .filter { it.artists.contains( artist.name ) }
//
//    private fun getSongsInAlbumAsMediaItems( album: Album ) =
//        getSongsInAlbum( album ).map { it.toMediaItem() }
//
//    fun getSongsInAlbum( album: Album ) =
//        musicServiceConnection.cachedSongs.value
//            .filter{ it.albumTitle == album.title }
//}