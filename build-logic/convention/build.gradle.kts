import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.squad.musicmatters.buildlogic"


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly( libs.android.gradlePlugin )
    compileOnly( libs.android.tools.common )
    compileOnly( libs.compose.gradlePlugin )
    compileOnly( libs.kotlin.gradlePlugin )
    compileOnly( libs.ksp.gradlePlugin )
    compileOnly( libs.room.gradlePlugin )
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register( "hilt" ) {
            id = "musicmatters.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register( "androidLibrary" ) {
            id = "musicmatters.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register( "androidRoom" ) {
            id = "musicmatters.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register( "androidLibraryCompose" ) {
            id = "musicmatters.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register( "androidFeature" ) {
            id = "musicmatters.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}