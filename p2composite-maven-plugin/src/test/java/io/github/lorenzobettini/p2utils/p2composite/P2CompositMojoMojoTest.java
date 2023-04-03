package io.github.lorenzobettini.p2utils.p2composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

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
		assertThat(expectedOutputDirectory)
			.isDirectoryContaining("glob:**compositeArtifacts.xml")
			.isDirectoryContaining("glob:**compositeContent.xml");
	}

	@Test
	public void testAddChild() throws Exception {
		File pom = getPom("project-add-child");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), "target/compositerepo");
		assertThat(expectedOutputDirectory)
			.isDirectoryContaining("glob:**compositeArtifacts.xml")
			.isDirectoryContaining("glob:**compositeContent.xml");
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
}
