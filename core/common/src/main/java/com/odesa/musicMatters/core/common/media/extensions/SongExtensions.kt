package com.odesa.musicMatters.core.common.media.extensions

import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Song
import java.util.Date

val Song.sizeString
    get() = "%.2f MB".format( size.toDouble() / ( 1024 * 1024 ) )
val Song.dateModifiedString: String
    get() = java.text.SimpleDateFormat.getInstance().format( Date( dateModified * 1000 ) )



val artistTagSeparators = setOf( "Feat", "feat.", "Ft", "ft", ",", "&" )