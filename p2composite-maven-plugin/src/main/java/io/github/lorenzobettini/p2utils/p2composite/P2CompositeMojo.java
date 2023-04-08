package io.github.lorenzobettini.p2utils.p2composite;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.equinox.p2.core.ProvisionException;

/**
 * Goal which creates or updates an Eclipse p2 composite site, by
 * adding/removing child repositories.
 * 
 * @author Lorenzo Bettini
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.PACKAGE)
public class P2CompositeMojo extends AbstractMojo {
	/**
	 * Location of the generated composite repository.
	 */
	@Parameter(defaultValue = "${project.build.directory}/compositerepo", property = "outputDir", required = true)
	private File outputDirectory;

	/**
	 * Name for the composite repository.
	 */
	@Parameter(defaultValue = "Composite Artifact Repository")
	private String name;

	/**
	 * Whether to compress the composite content/artifact xml into a jar.
	 */
	@Parameter(defaultValue = "false")
	private boolean compressed;

	/**
	 * Whether the composite repository will fail to load if any of its children
	 * fail to load.
	 */
	@Parameter(defaultValue = "false")
	private boolean atomic;

	/**
	 * List of repositories to add to the composite.
	 */
	@Parameter
	private List<String> childrenToAdd = new ArrayList<>();

	/**
	 * List of repositories to remove from the composite.
	 */
	@Parameter
	private List<String> childrenToRemove = new ArrayList<>();

	@Inject
	private CustomCompositeRepositoryApplication compositeRepositoryApplication;

	public void execute() throws MojoExecutionException {
		try {
			compositeRepositoryApplication.addDestination(outputDirectory, name, atomic, compressed);
			for (String child : childrenToAdd) {
				compositeRepositoryApplication.addChild(child);
			}
			for (String child : childrenToRemove) {
				compositeRepositoryApplication.removeChild(child);
			}
			compositeRepositoryApplication.run();
		} catch (ProvisionException | URISyntaxException | IOException e) {
			throw new MojoExecutionException("Error creating composite repository", e);
		}
	}

}
