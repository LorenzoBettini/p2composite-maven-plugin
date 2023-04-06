package io.github.lorenzobettini.p2utils.p2composite;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.internal.repository.tools.CompositeRepositoryApplication;
import org.eclipse.equinox.p2.internal.repository.tools.RepositoryDescriptor;

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
	 * Whether the composite repository will fail to load if any of its children fail to load.
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

	@Component
	private IProvisioningAgent agent;

	private static final String P2_INDEX_CONTENTS = """
			version=1
			metadata.repository.factory.order=compositeContent.xml,\\!
			artifact.repository.factory.order=compositeArtifacts.xml,\\!""";

	public void execute() throws MojoExecutionException {
		try {
			CompositeRepositoryApplication app = new CompositeRepositoryApplication(agent);
			var destination = new RepositoryDescriptor();
			destination.setLocation(outputDirectory.toURI());
			destination.setAtomic("" + atomic);
			destination.setCompressed(compressed);
			destination.setName(name);
			app.addDestination(destination);
			for (String child : childrenToAdd) {
				getLog().info("Adding " + child);
				app.addChild(fromStringToRepositoryDescriptor(child));
			}
			for (String child : childrenToRemove) {
				getLog().info("Removing " + child);
				app.removeChild(fromStringToRepositoryDescriptor(child));
			}
			app.run(new NullProgressMonitor());
			getLog().info("Generating p2.index");
			Files.writeString(new File(outputDirectory, "p2.index").toPath(),
					P2_INDEX_CONTENTS);
			getLog().info("Done");
		} catch (ProvisionException | URISyntaxException | IOException e) {
			throw new MojoExecutionException("Error creating composite repository", e);
		}
	}

	private RepositoryDescriptor fromStringToRepositoryDescriptor(String child) throws URISyntaxException {
		var childRepo = new RepositoryDescriptor();
		childRepo.setLocation(URIUtil.fromString(child));
		return childRepo;
	}
}
