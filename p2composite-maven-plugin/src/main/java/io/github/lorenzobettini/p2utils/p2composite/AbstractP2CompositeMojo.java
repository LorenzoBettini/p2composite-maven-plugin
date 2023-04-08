/**
 * 
 */
package io.github.lorenzobettini.p2utils.p2composite;

import java.io.File;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Abstract base class for mojos dealing with p2 composite repositories.
 * 
 * @author Lorenzo Bettini
 *
 */
public abstract class AbstractP2CompositeMojo extends AbstractMojo {

	/**
	 * Location of the generated composite repository.
	 */
	@Parameter(defaultValue = "${project.build.directory}/compositerepo", property = "outputDir", required = true)
	protected File outputDirectory;

	/**
	 * Name for the composite repository.
	 */
	@Parameter(defaultValue = "Composite Artifact Repository")
	protected String name;

	/**
	 * Whether to compress the composite content/artifact xml into a jar.
	 */
	@Parameter(defaultValue = "false")
	protected boolean compressed;

	/**
	 * Whether the composite repository will fail to load if any of its children
	 * fail to load.
	 */
	@Parameter(defaultValue = "false")
	protected boolean atomic;

	@Inject
	protected CustomCompositeRepositoryApplication compositeRepositoryApplication;

}
