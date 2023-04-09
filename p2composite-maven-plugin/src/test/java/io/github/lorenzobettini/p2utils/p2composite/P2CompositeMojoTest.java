package io.github.lorenzobettini.p2utils.p2composite;

import static io.github.lorenzobettini.p2utils.p2composite.TestUtils.assertGeneratedChildren;
import static io.github.lorenzobettini.p2utils.p2composite.TestUtils.assertGeneratedChildrenCompressed;
import static io.github.lorenzobettini.p2utils.p2composite.TestUtils.assertGeneratedChildrenDoNotContain;
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

public class P2CompositeMojoTest {
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
	public void testWithEmptyOutputFolder() throws Exception {
		File pom = getPom("project-to-test");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(),
				"target/test-harness/project-to-test");
		assertGeneratedCompositeFiles(expectedOutputDirectory);
	}

	@Test
	public void testAddChild() throws Exception {
		String projectPath = "project-add-child";
		String outputFolder = "target/compositerepo";
		File pom = getPom(projectPath);
		prepareChildDirectory(TEST_REPOS, projectPath, outputFolder, "child1");
		prepareChildDirectory(TEST_REPOS, projectPath, outputFolder, "child2");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		assertGeneratedCompositeFiles(expectedOutputDirectory);
		assertGeneratedChildren(expectedOutputDirectory, false, "Composite Artifact Repository", "child1", "child2");
	}

	@Test
	public void testCustomName() throws Exception {
		String projectPath = "project-custom-name";
		String outputFolder = "target/compositerepo";
		File pom = getPom(projectPath);
		prepareChildDirectory(TEST_REPOS, projectPath, outputFolder, "child1");
		prepareChildDirectory(TEST_REPOS, projectPath, outputFolder, "child2");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		assertGeneratedCompositeFiles(expectedOutputDirectory);
		assertGeneratedChildren(expectedOutputDirectory, false,
				"My Custom Composite Repository", "child1", "child2");
	}

	@Test
	public void testAddChildRelativePath() throws Exception {
		String projectPath = "project-add-child-relative";
		String outputFolder = "target/compositerepo";
		File pom = getPom(projectPath);
		prepareChildDirectory(TEST_REPOS, projectPath, "target", "child1");
		prepareChildDirectory(TEST_REPOS, projectPath, "target/subdir", "child2");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		assertGeneratedCompositeFiles(expectedOutputDirectory);
		assertGeneratedChildren(expectedOutputDirectory, false,
			"Composite Artifact Repository", "../child1", "../subdir/child2");
	}

	@Test
	public void testRemoveChild() throws Exception {
		String projectPath = "project-remove-child";
		String outputFolder = "target/";
		File pom = getPom(projectPath);
		prepareChildDirectory(TARGET_TEST_CLASSES + projectPath,
				projectPath,
				outputFolder, "initialrepo");
		prepareChildDirectory(TEST_REPOS, projectPath,
				outputFolder + "initialrepo", "child1");
		prepareChildDirectory(TEST_REPOS, projectPath,
				outputFolder + "initialrepo", "child2");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder + "/initialrepo");
		assertGeneratedCompositeFiles(expectedOutputDirectory);
		assertGeneratedChildren(expectedOutputDirectory, false, "Composite Artifact Repository", "child1");
		assertGeneratedChildrenDoNotContain(expectedOutputDirectory, "child2");
	}

	@Test
	public void testAddChildWithOptions() throws Exception {
		String projectPath = "project-add-child-with-options";
		String outputFolder = "target/compositerepo";
		File pom = getPom(projectPath);
		prepareChildDirectory(TEST_REPOS, projectPath, outputFolder, "child1");
		prepareChildDirectory(TEST_REPOS, projectPath, outputFolder, "child2");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		assertGeneratedCompositeFilesCompressed(expectedOutputDirectory);
		assertGeneratedChildrenCompressed(expectedOutputDirectory, true, "child1", "child2");
	}

	@Test
	public void testAtomicAddAndRemoveWithReferenceToNonExistentChild() throws Exception {
		String projectPath = "project-add-remove-child";
		String outputFolder = "target/";
		File pom = getPom(projectPath);
		prepareChildDirectory(TARGET_TEST_CLASSES + projectPath,
				projectPath, outputFolder, "initialrepo");
		prepareChildDirectory(TEST_REPOS, projectPath,
				outputFolder + "initialrepo", "child1");
		prepareChildDirectory(TEST_REPOS, projectPath,
				outputFolder + "initialrepo", "child2");
		prepareChildDirectory(TEST_REPOS, projectPath,
				outputFolder + "initialrepo", "child3");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder + "/initialrepo");
		assertGeneratedCompositeFiles(expectedOutputDirectory);
		assertGeneratedChildren(expectedOutputDirectory, true, "Composite Artifact Repository", "child1", "child3");
		assertGeneratedChildrenDoNotContain(expectedOutputDirectory, "child2");
	}

	private void prepareChildDirectory(String sourceFolder, String projectPath, String outputFolder, String childDirName) throws IOException {
		FileUtils.copyDirectoryToDirectory(
			new File(sourceFolder, childDirName),
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
		assertThat(pom).exists();
		return pom;
	}

}
