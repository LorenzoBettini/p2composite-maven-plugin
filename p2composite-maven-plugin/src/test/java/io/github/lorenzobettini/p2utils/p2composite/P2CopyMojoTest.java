package io.github.lorenzobettini.p2utils.p2composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

public class P2CopyMojoTest {
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
	public void testDefaultCopy() throws Exception {
		String projectPath = "project-copy-repository";
		String outputFolder = "target/compositerepo";
		File pom = getPom(projectPath);
		prepareSourceDirectory(TEST_REPOS, projectPath, "target", "repository");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		File expectedCopiedDirectory = new File(expectedOutputDirectory, "repository");
		assertThat(expectedCopiedDirectory)
			.isNotEmptyDirectory();
	}

	private void prepareSourceDirectory(String sourceFolder, String projectPath, String outputFolder, String childDirName) throws IOException {
		FileUtils.copyDirectoryToDirectory(
			new File(sourceFolder, childDirName),
			new File(TARGET_TEST_CLASSES + projectPath, outputFolder));
	}

	private P2CopyMojo runMojo(File pom) throws Exception {
		P2CopyMojo myMojo = rule.lookupConfiguredMojo(pom, "copy");
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
