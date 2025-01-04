import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class SpotlessConventionPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        // Apply the Spotless plugin
        target.pluginManager.apply(SpotlessPlugin)

        // Configure the Spotless plugin
        target.extensions.configure(SpotlessExtension) { extension ->
            extension.java {
                removeUnusedImports()
                googleJavaFormat().reflowLongStrings().reorderImports(true)
                formatAnnotations()
            }
        }
    }
}
