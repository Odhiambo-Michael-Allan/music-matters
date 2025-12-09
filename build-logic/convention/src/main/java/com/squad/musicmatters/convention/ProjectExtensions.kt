package com.squad.musicmatters.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): org.gradle.api.artifacts.VersionCatalog =
        extensions.getByType<VersionCatalogsExtension>().named( "libs" )