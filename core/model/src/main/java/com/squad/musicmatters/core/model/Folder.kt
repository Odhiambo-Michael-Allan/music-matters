package com.squad.musicmatters.core.model

import java.nio.file.Path
import kotlin.io.path.pathString

data class Folder(
    val name: String,
    val path: String,
    val artworkUri: String? = null,
    val trackCount: Int,
)

fun Path.directoryName(): String {
    val indexOfSeparator = pathString.lastIndexOf( "/" )
    return pathString.substring( 0, indexOfSeparator )
}