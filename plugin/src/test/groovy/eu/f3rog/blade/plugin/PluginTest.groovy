package eu.f3rog.blade.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class PluginTest extends Specification {

    @Rule
    final ProjectFolder testProjectDir = new ProjectFolder()

    private String bladeVersion
    private String bladeGroupId

    def setup() {
        bladeVersion = "2.5.0-beta1"
        bladeGroupId = BladePlugin.LIB_GROUP_ID
    }

    @Unroll
    def "fail without android plugin - for android gradle tools #gradleToolsVersion"() {
        given:
        testProjectDir.addGradleBuildFile(gradleToolsVersion, bladeVersion, false)

        when:
        Exception e = null
        try {
            GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments(':build')
                    .build()
        } catch (Exception ex) {
            e = ex
        }

        then:
        e != null
        e.getMessage().contains(BladePlugin.ERROR_ANDROID_PLUGIN_REQUIRED)

        where:
        gradleToolsVersion << ['1.5.0', '2.0.0', '2.2.0']
    }

    @Unroll
    def "fail if non-existing module name used - gradleToolsVersion #gradleToolsVersion, gradleVersion #gradleVersion"() {
        given:
        testProjectDir.addBladeFile(bladeModules)
        testProjectDir.addGradleBuildFile(gradleToolsVersion, bladeVersion, true)

        when:
        Exception e = null
        try {
            GradleRunner.create()
                    .withGradleVersion(gradleVersion)
                    .withProjectDir(testProjectDir.root)
                    .withArguments(':build')
                    .build()
        } catch (Exception ex) {
            e = ex
        }

        then:
        e != null
        e.getMessage().contains(String.format(BladePlugin.ERROR_MODULE_DOES_NOT_EXIST, "fake"))

        where:
        gradleToolsVersion << ['1.5.0', '2.0.0', '2.2.0']
        gradleVersion << ['2.9', '2.10', '2.14.1']
        bladeModules << [Arrays.asList("arg", "fake"), Arrays.asList("arg", "fake"), Arrays.asList("arg", "fake")]
    }

    @Unroll
    def "add correct dependencies - gradleToolsVersion #gradleToolsVersion, gradleVersion #gradleVersion"() {
        given:
        testProjectDir.addBladeFile(bladeModules)
        testProjectDir.addGradleBuildFile(gradleToolsVersion, bladeVersion, true, gradleToolsVersion < '2.2.0')

        when:
        BuildResult result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withArguments('dependencies')
                .build()

        then:
        // check Blade core dependencies
        result.output.contains("$bladeGroupId:core:${bladeVersion}")
        result.output.contains("$bladeGroupId:core-compiler:${bladeVersion}")
        // check other Blade module dependencies
        for (module in BladePlugin.LIB_MODULES) {
            result.output.contains("$bladeGroupId:${module}:${bladeVersion}") == bladeModules.contains(module)
            result.output.contains("$bladeGroupId:${module}-compiler:${bladeVersion}") == bladeModules.contains(module)
        }
        // check plugin
        result.output.contains("eu.f3rog.blade.plugin.BladePlugin")

        where:
        gradleToolsVersion << ['1.5.0', '2.0.0', '2.2.0']
        gradleVersion << ['2.9', '2.10', '2.14.1']
        bladeModules << [Arrays.asList("Extra", "Mvp", "State"), Arrays.asList("arg"), Arrays.asList("arg")] // also test case insensitivity
    }

    @Unroll
    def "build successfully - for android gradle tools #gradleToolsVersion"() {
        given:
        testProjectDir.addBladeFile(bladeModules)
        testProjectDir.addGradleBuildFile(gradleToolsVersion, bladeVersion, true, gradleToolsVersion < '2.2.0')

        when:
        BuildResult result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withArguments(':build')
                .build()

        then:
        result.task(":build").outcome == SUCCESS
        result.task(":transformClassesWithBladeForDebug").outcome == SUCCESS
        result.task(":transformClassesWithBladeForRelease").outcome == SUCCESS

        where:
        gradleToolsVersion << ['1.5.0', '2.0.0', '2.2.0']
        gradleVersion << ['2.9', '2.10', '2.14.1']
        bladeModules << [Arrays.asList("extra", "arg"), Arrays.asList("extra", "arg"), Arrays.asList("extra", "arg")]
    }

    @Unroll
    def "build successfully mvp module - for android gradle tools #gradleToolsVersion"() {
        given:
        testProjectDir.addBladeFile(bladeModules)
        def deps = [
                "compile 'com.google.dagger:dagger:2.0.2'"
        ]
        testProjectDir.addGradleBuildFile(gradleToolsVersion, bladeVersion, true, gradleToolsVersion < '2.2.0', deps)

        when:
        BuildResult result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withArguments(':build')
                .build()

        then:
        result.task(":build").outcome == SUCCESS
        result.task(":transformClassesWithBladeForDebug").outcome == SUCCESS
        result.task(":transformClassesWithBladeForRelease").outcome == SUCCESS

        where:
        gradleToolsVersion << ['1.5.0', '2.0.0', '2.2.0']
        gradleVersion << ['2.9', '2.10', '2.14.1']
        bladeModules << [Arrays.asList("mvp"), Arrays.asList("mvp"), Arrays.asList("mvp")]
    }
}