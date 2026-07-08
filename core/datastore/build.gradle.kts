plugins {
    alias( libs.plugins.musicmatters.android.library )
    alias( libs.plugins.musicmatters.hilt )
}

android {
    namespace = "com.squad.musicmatters.core.datastore"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {
    api( projects.core.model )
    api( projects.core.common )

    implementation( projects.core.datastoreProto )

    implementation( libs.androidx.dataStore )
    implementation( libs.androidx.core.ktx )

    testImplementation( libs.kotlinx.coroutines.test )
    testImplementation( libs.junit )
}