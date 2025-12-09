import com.android.build.api.dsl.LibraryExtension
import com.squad.musicmatters.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply( target: Project ) {
        with ( target ) {
            pluginManager.apply {
                apply( "musicmatters.android.library" )
                apply( "musicmatters.hilt" )
                apply( "musicmatters.android.library.compose" )
            }
            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
            }

            dependencies {
//                add( "implementation", project( ":core:ui" ) )
//                add( "implementation", project( ":core:designsystem" ) )
//                add( "implementation", project( ":core:media" ) )

                add( "implementation", libs.findLibrary( "androidx.hilt.navigation.compose" ).get() )
                add( "implementation", libs.findLibrary( "androidx.lifecycle.runtime.compose" ).get() )
                add( "implementation", libs.findLibrary( "androidx.navigation.compose" ).get() )

                add( "testImplementation", libs.findLibrary( "androidx.navigation.testing" ).get() )
//                add( "androidTestImplementation", libs.findLibrary( "androidx.lifecycle.runtimeTesting" ).get() )
            }
        }
    }

}