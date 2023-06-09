package io.github.lorenzobettini.p2utils.p2composite;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which copies the child repository to the final output directory with a
 * specified destination name. With default values, it assumes to use the p2
 * repository generated by Tycho.
 * 
 * @author Lorenzo Bettini
 */
@Mojo(name = "copy-repository", defaultPhase = LifecyclePhase.PACKAGE)
public class P2CopyRepositoryMojo extends AbstractMojo {
	/**
	 * Directory of the p2 repository to copy.
	 */
	@Parameter(defaultValue = "${project.build.directory}/repository", property = "sourceDir", required = true)
	private File sourceDirectory;

	/**
	 * Name of the destination directory.
	 */
	@Parameter(required = true)
	private String copyAs;

	/**
	 * Where to copy the repository.
	 */
	@Parameter(defaultValue = "${project.build.directory}/compositerepo", property = "outputDir", required = true)
	private File outputDirectory;

	public void execute() throws MojoExecutionException {
		try {
			P2CopyUtils.copyDirectoryContentsAs(sourceDirectory, outputDirectory, copyAs);
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating composite repository", e);
		}
	}

}
