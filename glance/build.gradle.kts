plugins {
    alias( libs.plugins.musicmatters.android.feature )
}

android {
    namespace = "com.squad.musicmatters.glance"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {
    implementation( libs.androidx.glance.appwidget )
    implementation( libs.androidx.glance.material3 )
    implementation( libs.coil.kt.compose )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.material )

    debugImplementation( libs.androidx.glance.preview )
    debugImplementation( libs.androidx.glance.appwidget.preview )

    testImplementation( libs.junit )
    testImplementation( libs.kotlinx.coroutines.test )

    androidTestImplementation( libs.androidx.junit )
    androidTestImplementation( libs.androidx.espresso.core )
}