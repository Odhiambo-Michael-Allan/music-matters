plugins {
    alias( libs.plugins.musicmatters.android.library )
}

android {
    namespace = "com.squad.musicmatters.core.testing"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }
}

dependencies {

    implementation( projects.core.model )
    implementation( projects.core.media )
    implementation( projects.core.data )
    implementation( projects.core.designsystem )
    implementation( projects.core.i8n )

    implementation( libs.androidx.test.rules )
    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.media3.common )
    api( libs.kotlinx.coroutines.test )

    
}