package com.squad.musicmatters.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.squad.musicmatters.core.database.dao.PlayHistoryDao
import com.squad.musicmatters.core.database.dao.PlaylistDao
import com.squad.musicmatters.core.database.dao.PlaylistEntryDao
import com.squad.musicmatters.core.database.dao.QueueDao
import com.squad.musicmatters.core.database.dao.SongAdditionalMetadataDao
import com.squad.musicmatters.core.database.dao.SongPlayCountEntryDao
import com.squad.musicmatters.core.database.model.PlayHistoryEntity
import com.squad.musicmatters.core.database.model.PlaylistEntity
import com.squad.musicmatters.core.database.model.PlaylistEntryEntity
import com.squad.musicmatters.core.database.model.QueueEntity
import com.squad.musicmatters.core.database.model.SongAdditionalMetadataEntity
import com.squad.musicmatters.core.database.model.SongPlayCountEntity
import com.squad.musicmatters.core.database.typeconverter.InstantConverter

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistEntryEntity::class,
        SongPlayCountEntity::class,
        SongAdditionalMetadataEntity::class,
        QueueEntity::class,
        PlayHistoryEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(InstantConverter::class )
abstract class MusicMattersDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistEntryDao(): PlaylistEntryDao
    abstract fun songPlayCountEntryDao(): SongPlayCountEntryDao
    abstract fun songAdditionalMetadataDao(): SongAdditionalMetadataDao
    abstract fun queueDao(): QueueDao
    abstract fun playHistoryDao(): PlayHistoryDao

}

