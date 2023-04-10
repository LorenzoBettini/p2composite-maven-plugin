package io.github.lorenzobettini.p2utils.p2composite;

import static io.github.lorenzobettini.p2utils.p2composite.TestUtils.assertGeneratedChildren;
import static io.github.lorenzobettini.p2utils.p2composite.TestUtils.assertGeneratedChildrenCompressed;
import static io.github.lorenzobettini.p2utils.p2composite.TestUtils.assertGeneratedCompositeFiles;
import static io.github.lorenzobettini.p2utils.p2composite.TestUtils.assertGeneratedCompositeFilesCompressed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

public class P2CompositeAddRepositoryMojoTest {
	private static final String TARGET_TEST_CLASSES = "target/test-classes/";
	private static final String TEST_REPOS = TARGET_TEST_CLASSES + "test-repos";

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
	public void testDefaultAddRepository() throws Exception {
		String projectPath = "project-add-repository";
		String outputFolder = "target/compositerepo";
		File pom = getPom(projectPath);
		prepareSourceDirectory(TEST_REPOS, projectPath, "target", "repository");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		File expectedCopiedDirectory = new File(expectedOutputDirectory, "child1");
		assertThat(expectedCopiedDirectory)
			.isNotEmptyDirectory();
		assertGeneratedCompositeFiles(expectedOutputDirectory);
		assertGeneratedChildren(expectedOutputDirectory, false, "Composite Artifact Repository", "child1");
	}

	@Test
	public void testAddRepositoryWithOptions() throws Exception {
		String projectPath = "project-add-repository-with-options";
		String outputFolder = "target/customrepo";
		File pom = getPom(projectPath);
		prepareSourceDirectory(TEST_REPOS, projectPath, "target", "myrepo");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		File expectedCopiedDirectory = new File(expectedOutputDirectory, "child1");
		assertThat(expectedCopiedDirectory)
			.isNotEmptyDirectory();
		assertGeneratedCompositeFilesCompressed(expectedOutputDirectory);
		assertGeneratedChildrenCompressed(expectedOutputDirectory, true, "child1");
	}

	private void prepareSourceDirectory(String sourceFolder, String projectPath, String outputFolder, String childDirName) throws IOException {
		FileUtils.copyDirectoryToDirectory(
			new File(sourceFolder, childDirName),
			new File(TARGET_TEST_CLASSES + projectPath, outputFolder));
	}

	private P2CompositeAddRepositoryMojo runMojo(File pom) throws Exception {
		P2CompositeAddRepositoryMojo myMojo = rule.lookupConfiguredMojo(pom, "add-repository");
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
		assertThat(pom).exists();
		return pom;
	}

}
