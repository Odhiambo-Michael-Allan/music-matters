plugins {
    alias( libs.plugins.android.application )
    alias( libs.plugins.jetbrains.kotlin.android )
    alias( libs.plugins.compose.compiler )
    alias( libs.plugins.hilt )
    alias( libs.plugins.ksp )
    alias( libs.plugins.kotlin.serialization )
}

android {
    namespace = "com.squad.musicmatters"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.odesa.musicMatters"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation( projects.feature.songs )
    implementation( projects.feature.nowplaying )
    implementation( projects.feature.queue )
    implementation( projects.feature.lyrics )
    implementation( projects.feature.settings )
    implementation( projects.feature.albums )
    implementation( projects.feature.album )
    implementation( projects.feature.artists )
    implementation( projects.feature.artist )
    implementation( projects.feature.genres )
    implementation( projects.feature.genre )

    implementation( projects.core.designsystem )
    implementation( projects.core.media )
    implementation( projects.core.data )
    implementation( projects.core.database )
    implementation( projects.core.testing )
    implementation( projects.core.i8n )
    implementation( projects.core.model )
    implementation( projects.core.ui )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.androidx.lifecycle.runtime.ktx )

    implementation( libs.androidx.compose.runtime )
    implementation( libs.androidx.activity.compose )
    implementation( platform( libs.androidx.compose.bom ) )
    implementation( libs.androidx.compose.ui )
    implementation( libs.androidx.compose.animation )
    implementation( libs.androidx.compose.ui.graphics )
    implementation( libs.androidx.lifecycle.runtime.compose )


    implementation( libs.androidx.compose.ui.tooling.preview )
    implementation( libs.androidx.compose.material3 )
    implementation( libs.androidx.compose.material.iconsExtended )
    implementation( libs.androidx.navigation.compose )
    implementation( libs.coil.kt.compose )
    implementation( libs.androidx.media )
    implementation( libs.timber )
    implementation( libs.kotlinx.coroutines.guava )

    // Androidx Media3 Dependencies
    implementation( libs.androidx.media3.common )
    // For exposing and controlling media sessions
    implementation( libs.androidx.media3.session )
    // For media playback using ExoPlayer
    implementation( libs.androidx.media3.exoplayer )

    implementation( libs.androidx.compose.material3.adaptive )
    implementation( libs.androidx.compose.material3.adaptive.navigation.suite )

    implementation( libs.hilt.core )
    implementation( libs.hilt.android )
    ksp( libs.hilt.compiler )

    implementation( libs.kotlinx.serialization.json )

    // Cast
    implementation( libs.google.cast.framework )
    implementation( libs.androidx.mediarouter )

    testImplementation( libs.junit )
    testImplementation( libs.androidx.junit )
    testImplementation( libs.robolectric )
    testImplementation( libs.kotlinx.coroutines.test )

    androidTestImplementation( libs.androidx.junit )
    androidTestImplementation( libs.androidx.espresso.core )
    androidTestImplementation( platform( libs.androidx.compose.bom ) )
    androidTestImplementation( libs.androidx.compose.ui.test.junit4 )
    androidTestImplementation( libs.androidx.navigation.testing )


    debugImplementation( libs.androidx.compose.ui.tooling )
    debugImplementation( libs.androidx.compose.ui.test.manifest )

    coreLibraryDesugaring( libs.core.jdk.desugaring )
}