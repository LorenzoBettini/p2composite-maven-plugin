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
 * Goal which touches a timestamp file.
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.PACKAGE)
public class P2CompositeMojo extends AbstractMojo {
	/**
	 * Location of the generated composite repository.
	 */
	@Parameter(defaultValue = "${project.build.directory}/compositerepo", property = "outputDir", required = true)
	private File outputDirectory;

	/**
	 * Location of the generated composite repository.
	 */
	@Parameter
	private List<String> childrenToAdd = new ArrayList<>();

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
			destination.setAtomic("false");
			destination.setCompressed(false);
			app.addDestination(destination);
			for (String child : childrenToAdd) {
				var childRepo = new RepositoryDescriptor();
				childRepo.setLocation(URIUtil.fromString(child));
				app.addChild(childRepo);
			}
			app.run(new NullProgressMonitor());
			Files.writeString(new File(outputDirectory, "p2.index").toPath(),
					P2_INDEX_CONTENTS);
		} catch (ProvisionException | URISyntaxException | IOException e) {
			throw new MojoExecutionException("Error creating composite repository", e);
		}
	}
}
