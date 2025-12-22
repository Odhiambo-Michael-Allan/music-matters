plugins {
    alias( libs.plugins.musicmatters.android.library )
    alias( libs.plugins.musicmatters.hilt )
}

android {
    namespace = "com.squad.musicmatters.core.data"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {

    api( projects.core.datastore )

    implementation( projects.core.i8n )
    implementation( projects.core.database )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.media3.common )

    testImplementation( libs.junit )
    testImplementation( libs.androidx.junit )
    testImplementation( libs.kotlinx.coroutines.test )

    testImplementation( projects.core.testing )
}