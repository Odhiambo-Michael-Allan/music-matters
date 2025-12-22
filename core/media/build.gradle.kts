plugins {
    alias( libs.plugins.musicmatters.android.library )
    alias( libs.plugins.musicmatters.hilt )
}

android {
    namespace = "com.squad.musicmatters.core.media"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

}

dependencies {

    implementation( projects.core.i8n )
    implementation( projects.core.data )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.androidx.media )

    // Androidx Media3 Dependencies
    api( libs.androidx.media3.common )
    // For exposing and controlling media sessions
    implementation( libs.androidx.media3.session )
    // For media playback using ExoPlayer
    implementation( libs.androidx.media3.exoplayer )

    implementation( libs.kotlinx.coroutines.guava )
    implementation( libs.timber )

    testImplementation( libs.junit )
    testImplementation( projects.core.testing )
    testImplementation( libs.kotlinx.coroutines.test )
}