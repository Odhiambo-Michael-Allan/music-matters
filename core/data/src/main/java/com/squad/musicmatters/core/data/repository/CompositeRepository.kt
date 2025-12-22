package com.squad.musicmatters.core.data.repository

interface CompositeRepository {
    suspend fun deleteSongWithId( songId: String )
}