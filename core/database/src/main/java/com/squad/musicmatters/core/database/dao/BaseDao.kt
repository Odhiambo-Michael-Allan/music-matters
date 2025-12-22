package com.squad.musicmatters.core.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insert( entity: T )

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertAll( vararg entity: T )

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertAll( collection: Collection<T> )

    @Update( onConflict = OnConflictStrategy.REPLACE )
    suspend fun update( entity: T )

    @Delete
    suspend fun delete( entity: T )
}