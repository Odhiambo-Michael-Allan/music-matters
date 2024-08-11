package com.odesa.musicMatters.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.odesa.musicMatters.core.data.database.model.SongPlayCountEntry

@Dao
abstract class SongPlayCountEntryDao : BaseDao<SongPlayCountEntry> {

    @Query( "SELECT * FROM song_play_count_entries" )
    abstract suspend fun fetchEntries(): List<SongPlayCountEntry>

    @Query( "SELECT * FROM song_play_count_entries WHERE song_id = :songId" )
    abstract suspend fun getPlayCountBySongId( songId: String ): SongPlayCountEntry?

    @Query( "UPDATE song_play_count_entries SET number_of_times_played = number_of_times_played + 1 WHERE song_id = :songId" )
    abstract suspend fun incrementPlayCount( songId: String )

    @Query( "DELETE FROM song_play_count_entries WHERE song_id = :songId" )
    abstract suspend fun deleteEntryWithSongId( songId: String )
}