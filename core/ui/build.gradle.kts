plugins {
    alias( libs.plugins.musicmatters.android.library )
    alias( libs.plugins.musicmatters.android.library.compose )
}

android {
    namespace = "com.squad.musicmatters.core.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {
    implementation( projects.core.designsystem )
    implementation( projects.core.i8n )
    implementation( projects.core.model )
    implementation( projects.core.common )
    implementation( projects.core.data )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.material )
    implementation( libs.coil.kt.compose )
    implementation( libs.androidx.media3.common )

    testImplementation( libs.junit )

    androidTestImplementation( libs.androidx.junit )
    androidTestImplementation( libs.androidx.espresso.core )
}