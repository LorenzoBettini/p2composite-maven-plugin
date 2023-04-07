package io.github.lorenzobettini.p2utils.p2composite;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which copies the child repository to the final output directory.
 * 
 * @author Lorenzo Bettini
 */
@Mojo(name = "copy", defaultPhase = LifecyclePhase.PACKAGE)
public class P2CopyMojo extends AbstractMojo {
	/**
	 * Directory of the p2 repository to copy.
	 */
	@Parameter(defaultValue = "${project.build.directory}/repository", property = "sourceDir", required = true)
	private File sourceDirectory;

	/**
	 * Destination directory.
	 */
	@Parameter(defaultValue = "${project.build.directory}/compositerepo", property = "outputDir", required = true)
	private File outputDirectory;

	public void execute() throws MojoExecutionException {
		try {
			getLog().info("Copying " + sourceDirectory + " to " + outputDirectory);
			FileUtils.copyDirectoryToDirectory(sourceDirectory, outputDirectory);
			getLog().info("Done");
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating composite repository", e);
		}
	}

}
