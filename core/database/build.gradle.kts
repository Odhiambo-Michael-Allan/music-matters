plugins {
    alias( libs.plugins.musicmatters.android.library )
    alias( libs.plugins.musicmatters.android.room )
    alias( libs.plugins.musicmatters.hilt )
}

android {
    namespace = "com.squad.musicmatters.core.database"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {
    implementation( projects.core.model )

    androidTestImplementation( libs.androidx.test.core )
    androidTestImplementation( libs.androidx.test.runner )
    androidTestImplementation( libs.kotlinx.coroutines.test )
}