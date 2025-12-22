package com.squad.musicmatters.core.database.typeconverter

import androidx.room.TypeConverter
import java.time.Instant

internal class InstantConverter {

    @TypeConverter
    fun longToInstant( value: Long? ): Instant? =
        value?.let( Instant::ofEpochMilli )

    @TypeConverter
    fun instantToLong( instant: Instant? ): Long? =
        instant?.toEpochMilli()
}