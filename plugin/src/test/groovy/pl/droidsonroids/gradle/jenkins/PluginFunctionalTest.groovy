package pl.droidsonroids.gradle.jenkins

import com.google.common.io.Resources
import org.assertj.core.api.SoftAssertions
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.assertj.core.api.Assertions.assertThat

class PluginFunctionalTest {

	public static final String VARIANT_LINE_SUFFIX = 'build variant is testable by monkey'
	@Rule
	public TemporaryFolder mTemporaryFolder = new TemporaryFolder()

	@Test
	void testBuildTypeOverriding() {
		copyResource('base.gradle', 'base.gradle')
		copyResource('override.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withProjectDir(mTemporaryFolder.root)
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'productionDev', 'stagingDebug', 'stagingDev', 'stagingRelease')
	}

	@Test
	public void testAddJenkinsTestableBuildType() {
		copyResource('base.gradle', 'base.gradle')
		copyResource('buildType.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'debug')
	}

	@Test
	void testAddJenkinsTestableFlavor() {
		copyResource('base.gradle', 'base.gradle')
		copyResource('productFlavor.gradle', 'build.gradle')
		def result = GradleRunner.create()
				.withProjectDir(mTemporaryFolder.root)
				.withTestKitDir(mTemporaryFolder.newFolder())
				.withArguments('projects')
				.withPluginClasspath()
				.build()
		assertTestableVariants(result, 'proDebug', 'proRelease')
	}

	private static void assertTestableVariants(BuildResult result, String... expectedVariants) {
		assertThat(result.output.readLines().findAll { it.endsWith(VARIANT_LINE_SUFFIX) }).hasSize(expectedVariants.size())
		def softAssertions = new SoftAssertions()
		expectedVariants.each {
			softAssertions.assertThat(result.output).contains("`$it` build variant is testable by monkey").as(it)
		}
		softAssertions.assertAll()
	}

	protected void copyResource(String resourceName, String fileName) {
		new File(mTemporaryFolder.root, fileName).withOutputStream {
			Resources.copy(getClass().classLoader.getResource(resourceName), it)
		}
	}
}