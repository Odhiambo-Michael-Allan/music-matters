plugins {
    alias( libs.plugins.musicmatters.android.feature )
}

android {
    namespace = "com.squad.musicmatters.feature.nowplaying"


    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {
    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.material )

    implementation( libs.kmpalette.core )
    implementation( libs.materialkolor )

    testImplementation( libs.junit )
}