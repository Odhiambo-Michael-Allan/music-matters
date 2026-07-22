plugins {
    alias( libs.plugins.musicmatters.android.feature )
}

android {
    namespace = "com.squad.musicmatters.glancewidget"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {
    implementation( libs.androidx.glance.appwidget )
    implementation( libs.androidx.glance.material )
    implementation( libs.androidx.glance.material3 )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.material )
    testImplementation( libs.junit )
    androidTestImplementation( libs.androidx.junit )
    androidTestImplementation( libs.androidx.espresso.core )
}