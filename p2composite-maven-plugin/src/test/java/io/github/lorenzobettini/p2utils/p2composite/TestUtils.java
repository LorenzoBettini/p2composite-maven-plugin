package io.github.lorenzobettini.p2utils.p2composite;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class TestUtils {

	public static void assertGeneratedCompositeFiles(File expectedOutputDirectory) {
		assertThat(expectedOutputDirectory)
			.isDirectoryContaining("glob:**compositeArtifacts.xml")
			.isDirectoryContaining("glob:**compositeContent.xml")
			.isDirectoryContaining("glob:**p2.index");
	}

	public static void assertGeneratedChildren(File expectedOutputDirectory, boolean expectedAtomic, String expectedName, String... expectedChildren) {
		var compositeArtifactsContents = assertThat(new File(expectedOutputDirectory, "compositeArtifacts.xml"))
			.content();
		var compositeContentContents = assertThat(new File(expectedOutputDirectory, "compositeContent.xml"))
			.content();
		var expectedChildrenLocations = Stream.of(expectedChildren)
				.map(it -> String.format("<child location='%s'/>", it))
				.toList();
		var expectedRepositoryName =
				String.format("<repository name='%s'", expectedName);
		compositeArtifactsContents
			.contains(expectedRepositoryName);
		compositeContentContents
			.contains(expectedRepositoryName);
		compositeArtifactsContents
			.contains(String.format("<property name='p2.atomic.composite.loading' value='%s'/>",
				expectedAtomic));
		compositeContentContents
			.contains(String.format("<property name='p2.atomic.composite.loading' value='%s'/>",
				expectedAtomic));
		compositeArtifactsContents
			.contains(expectedChildrenLocations);
		compositeContentContents
			.contains(expectedChildrenLocations);
	}

	public static void assertGeneratedCompositeFilesCompressed(File expectedOutputDirectory) {
		assertThat(expectedOutputDirectory)
			.isDirectoryContaining("glob:**compositeArtifacts.jar")
			.isDirectoryContaining("glob:**compositeContent.jar")
			.isDirectoryContaining("glob:**p2.index");
	}

	public static void assertGeneratedChildrenCompressed(File expectedOutputDirectory, boolean expectedAtomic, String... expectedChildren) throws Exception {
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

	public static String getJarContents(File expectedOutputDirectory, String jarFileName) throws IOException {
		var jarFile = new JarFile(new File(expectedOutputDirectory, jarFileName));
		var jarEntry = jarFile.getJarEntry(jarFile.entries().nextElement().getName());
		var is = jarFile.getInputStream(jarEntry);
		return new String(is.readAllBytes());
	}

	public static void assertGeneratedChildrenDoNotContain(File expectedOutputDirectory, String... nonExpectedChildren) {
		var compositeArtifactsContents = assertThat(new File(expectedOutputDirectory, "compositeArtifacts.xml"))
			.content();
		var compositeContentContents = assertThat(new File(expectedOutputDirectory, "compositeContent.xml"))
			.content();
		var nonExpectedChildrenLocations = Stream.of(nonExpectedChildren)
				.map(it -> String.format("<child location='%s'/>", it))
				.toList();
		compositeArtifactsContents
			.asString()
			.doesNotContain(nonExpectedChildrenLocations);
		compositeContentContents
			.asString()
			.doesNotContain(nonExpectedChildrenLocations);
	}
}
