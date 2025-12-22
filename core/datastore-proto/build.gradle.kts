plugins {
    alias( libs.plugins.musicmatters.android.library )
    alias( libs.plugins.protobuf )
}

android {
    namespace = "com.squad.musicmatters.core.datastore_proto"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles( "consumer-rules.pro" )
    }

}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register( "java" ) {
                    option( "lite" )
                }
                register( "kotlin" ) {
                    option( "lite" )
                }
            }
        }
    }
}

dependencies {
    api( libs.protobuf.kotlin.lite )
}