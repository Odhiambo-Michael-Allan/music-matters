pluginManagement {
    includeBuild( "build-logic" )
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MusicMatters"
include( ":mobile" )
enableFeaturePreview( "TYPESAFE_PROJECT_ACCESSORS" )
include( ":core:model" )
include( ":core:common" )
include( ":core:i8n" )
include( ":core:data" )
include( ":core:designsystem" )
include( ":core:testing" )
include( ":feature:songs" )
include( ":core:ui" )
