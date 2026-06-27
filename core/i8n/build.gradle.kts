plugins {
    alias( libs.plugins.musicmatters.android.library )
}

android {
    namespace = "com.squad.musicmatters.core.i8n"

    defaultConfig {

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}
