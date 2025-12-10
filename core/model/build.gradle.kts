
plugins {
    alias( libs.plugins.musicmatters.android.library )
}

android {
    namespace = "com.odesa.musicMatters.core.model"

    defaultConfig {

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {

    implementation( libs.androidx.core.ktx )

    // Androidx Media3 Dependencies
    implementation( libs.androidx.media3.common )
    implementation( projects.core.i8n )
}