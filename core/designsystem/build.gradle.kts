plugins {
    alias( libs.plugins.musicmatters.android.library )
    alias( libs.plugins.musicmatters.android.library.compose )
}

android {
    namespace = "com.squad.musicMatters.core.designsystem"

    defaultConfig {

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {

    api( libs.androidx.compose.foundation )
    api( libs.androidx.compose.material.iconsExtended )
    api( libs.androidx.compose.material3 )
    api( libs.androidx.compose.material3.adaptive )
    api( libs.androidx.compose.material3.adaptive.navigation.suite )
    api( libs.androidx.compose.runtime )
    api( libs.androidx.compose.ui.tooling )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.material )
    implementation( libs.coil.kt.compose )

    implementation( projects.core.i8n )
}