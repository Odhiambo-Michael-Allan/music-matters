package com.squad.musicmatters.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.squad.musicmatters.core.database.model.SongPlayCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongPlayCountEntryDao {

    @Upsert
    suspend fun upsertEntity( entity: SongPlayCountEntity )

    @Query( "SELECT * FROM song_play_count_entries ORDER BY number_of_times_played DESC" )
    fun fetchEntriesSortedByPlayCount(): Flow<List<SongPlayCountEntity>>

    @Query( "SELECT * FROM song_play_count_entries WHERE song_id = :songId" )
    suspend fun getPlayCountBySongId( songId: String ): SongPlayCountEntity?

    @Query( "UPDATE song_play_count_entries SET number_of_times_played = number_of_times_played + 1 WHERE song_id = :songId" )
    suspend fun incrementPlayCount( songId: String )

    @Query( "DELETE FROM song_play_count_entries WHERE song_id = :songId" )
    suspend fun deleteEntryWithSongId( songId: String )
}