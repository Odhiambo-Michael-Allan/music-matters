import androidx.room.gradle.RoomExtension
import com.google.devtools.ksp.gradle.KspExtension
import com.squad.musicmatters.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply( target: Project ) {
        with ( target ) {
            pluginManager.apply( "androidx.room" )
            pluginManager.apply( "com.google.devtools.ksp" )

            extensions.configure<KspExtension> {
                arg( "room.generateKotlin", "true" )
            }

            extensions.configure<RoomExtension> {
                // The schemas directory contains a schema file for each version of the Room database.
                // This is required to enable room auto migrations.
                // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                schemaDirectory( "$projectDir/schemas" )
            }

            dependencies {
                add( "implementation", libs.findLibrary( "androidx.room.runtime" ).get() )
                add( "implementation", libs.findLibrary( "androidx.room.ktx" ).get() )
                add( "ksp", libs.findLibrary( "androidx.room.compiler" ).get() )
            }
        }
    }

}