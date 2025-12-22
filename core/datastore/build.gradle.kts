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

    api( libs.androidx.dataStore )
    api( projects.core.datastoreProto )
    api( projects.core.model )
    api( projects.core.common )

    implementation( libs.androidx.core.ktx )
    implementation( projects.core.i8n )

    testImplementation( libs.kotlinx.coroutines.test )
    testImplementation( libs.junit )
}