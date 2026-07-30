plugins {
    alias( libs.plugins.musicmatters.android.feature )
}

android {
    namespace = "com.squad.musicmatters.feature.genre"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {
    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.material )

    testImplementation( libs.junit )
    testImplementation( libs.robolectric )
}