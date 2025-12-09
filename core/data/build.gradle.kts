plugins {
    alias( libs.plugins.android.library )
    alias( libs.plugins.jetbrains.kotlin.android )
    alias( libs.plugins.ksp )
}

android {
    namespace = "com.odesa.musicMatters.core.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.material )

    implementation( libs.androidx.media3.common )
    implementation( libs.timber )
    implementation( projects.core.designsystem )

    implementation( projects.core.i8n )
    implementation( projects.core.model )

    // Database
    implementation( libs.androidx.room.runtime )
    implementation( libs.androidx.room.ktx )
    ksp( libs.androidx.room.compiler )

    testImplementation( libs.junit )
    testImplementation( libs.androidx.junit )
    testImplementation( libs.robolectric )

    testImplementation( libs.kotlinx.coroutines.test )
    testImplementation( projects.core.testing )

    androidTestImplementation( libs.androidx.junit )
    androidTestImplementation( libs.androidx.espresso.core )
}