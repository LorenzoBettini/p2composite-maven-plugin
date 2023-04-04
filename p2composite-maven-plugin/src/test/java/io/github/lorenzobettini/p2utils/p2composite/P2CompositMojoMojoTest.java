package io.github.lorenzobettini.p2utils.p2composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.stream.Stream;

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

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(),
				"target/test-harness/project-to-test");
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
		assertGeneratedChildren(expectedOutputDirectory, false, "child1", "child2");
	}

	@Test
	public void testAddChildWithOptions() throws Exception {
		String projectPath = "project-add-child-with-options";
		String outputFolder = "target/compositerepo";
		File pom = getPom(projectPath);
		prepareChildDirectory(projectPath, outputFolder, "child1");
		prepareChildDirectory(projectPath, outputFolder, "child2");
		runMojo(pom);

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), outputFolder);
		assertGeneratedCompositeFilesCompressed(expectedOutputDirectory);
		assertGeneratedChildrenCompressed(expectedOutputDirectory, true, "child1", "child2");
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

	private void assertGeneratedCompositeFilesCompressed(File expectedOutputDirectory) {
		assertThat(expectedOutputDirectory)
			.isDirectoryContaining("glob:**compositeArtifacts.jar")
			.isDirectoryContaining("glob:**compositeContent.jar")
			.isDirectoryContaining("glob:**p2.index");
	}

	private void assertGeneratedChildren(File expectedOutputDirectory, boolean expectedAtomic, String... expectedChildren) {
		var compositeArtifactsContents = assertThat(new File(expectedOutputDirectory, "compositeArtifacts.xml"))
			.content();
		var compositeContentContents = assertThat(new File(expectedOutputDirectory, "compositeContent.xml"))
			.content();
		var expectedChildrenLocations = Stream.of(expectedChildren)
				.map(it -> String.format("<child location='%s'/>", it))
				.toList();
		compositeArtifactsContents
			.contains(expectedChildrenLocations)
			.contains(String.format("<property name='p2.atomic.composite.loading' value='%s'/>",
				expectedAtomic));
		compositeContentContents
			.contains(expectedChildrenLocations)
			.contains(String.format("<property name='p2.atomic.composite.loading' value='%s'/>",
				expectedAtomic));
	}

	private void assertGeneratedChildrenCompressed(File expectedOutputDirectory, boolean expectedAtomic, String... expectedChildren) throws Exception {
		var expectedChildrenLocations = Stream.of(expectedChildren)
				.map(it -> String.format("<child location='%s'/>", it))
				.toList();
		assertThat(getJarContents(expectedOutputDirectory, "compositeArtifacts.jar"))
			.contains(expectedChildrenLocations)
			.contains(String.format("<property name='p2.atomic.composite.loading' value='%s'/>",
					expectedAtomic));
		assertThat(getJarContents(expectedOutputDirectory, "compositeContent.jar"))
			.contains(expectedChildrenLocations)
			.contains(String.format("<property name='p2.atomic.composite.loading' value='%s'/>",
					expectedAtomic));
	}

	private String getJarContents(File expectedOutputDirectory, String jarFileName) throws IOException {
		var jarFile = new JarFile(new File(expectedOutputDirectory, jarFileName));
		var jarEntry = jarFile.getJarEntry(jarFile.entries().nextElement().getName());
		var is = jarFile.getInputStream(jarEntry);
		return new String(is.readAllBytes());
	}
}
