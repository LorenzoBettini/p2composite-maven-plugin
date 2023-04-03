package io.github.lorenzobettini.p2utils.p2composite;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;

public class P2CompositMojoMojoTest {
	@Rule
	public MojoRule rule = new MojoRule() {
		@Override
		protected void before() throws Throwable {
		}

		@Override
		protected void after() {
		}
	};

	/**
	 * @throws Exception if any
	 */
	@Test
	public void testSomething() throws Exception {
		File pom = new File("target/test-classes/project-to-test/");
		assertNotNull(pom);
		assertTrue(pom.exists());

		P2CompositeMojo myMojo = (P2CompositeMojo) rule.lookupConfiguredMojo(pom, "run");
		assertNotNull(myMojo);
		myMojo.execute();

		File outputDirectory = (File) rule.getVariableValueFromObject(myMojo, "outputDirectory");
		assertNotNull(outputDirectory);
		assertTrue(outputDirectory.exists());

		File touch = new File(outputDirectory, "touch.txt");
		assertTrue(touch.exists());

		File expectedOutputDirectory = new File(pom.getAbsoluteFile(), "target/test-harness/project-to-test");
		assertEquals(expectedOutputDirectory, outputDirectory);
	}

}
