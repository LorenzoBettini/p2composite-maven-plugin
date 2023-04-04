package io.github.lorenzobettini.p2utils.p2composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

public class P2CompositMojoMojoTest {
	private static final String TARGET_TEST_CLASSES = "target/test-classes/";
	@Rule
	public MojoRule rule = new MojoRule() {
		@Override
		protected void before() throws Throwable {
		}

		@Override
		protected void after() {
		}
	};

	@Test
	public void testWithEmptyOutputFolder() throws Exception {
		File pom = getPom("project-to-test");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), "target/test-harness/project-to-test");
		assertGeneratedCompositeFiles(expectedOutputDirectory);
	}

	@Test
	public void testAddChild() throws Exception {
		String projectPath = "project-add-child";
		String outputFolder = "target/compositerepo";
		File pom = getPom(projectPath);
		prepareChildDirectory(projectPath, outputFolder, "child1");
		prepareChildDirectory(projectPath, outputFolder, "child2");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		assertGeneratedCompositeFiles(expectedOutputDirectory);
		assertThat(new File(expectedOutputDirectory, "compositeArtifacts.xml"))
			.content()
			.contains("child1").contains("child2");
		assertThat(new File(expectedOutputDirectory, "compositeContent.xml"))
			.content()
			.contains("child1").contains("child2");
	}

	private void prepareChildDirectory(String projectPath, String outputFolder, String childDirName) throws IOException {
		FileUtils.copyDirectoryToDirectory(
			new File(TARGET_TEST_CLASSES + projectPath, childDirName),
			new File(TARGET_TEST_CLASSES + projectPath, outputFolder));
	}

	private P2CompositeMojo runMojo(File pom) throws Exception {
		P2CompositeMojo myMojo = rule.lookupConfiguredMojo(pom, "run");
		assertNotNull(myMojo);
		myMojo.execute();
		return myMojo;
	}

	private File getPom(String projectPath) throws Exception {
		var targetDirectory = new File(
			TARGET_TEST_CLASSES + projectPath + "/target");
		FileUtils.deleteDirectory(targetDirectory);
		assertThat(targetDirectory)
			.doesNotExist();
		File pom = new File(TARGET_TEST_CLASSES + projectPath);
		assertNotNull(pom);
		assertTrue(pom.exists());
		return pom;
	}

	private void assertGeneratedCompositeFiles(File expectedOutputDirectory) {
		assertThat(expectedOutputDirectory)
			.isDirectoryContaining("glob:**compositeArtifacts.xml")
			.isDirectoryContaining("glob:**compositeContent.xml")
			.isDirectoryContaining("glob:**p2.index");
	}
}
