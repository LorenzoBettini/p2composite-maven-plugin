package io.github.lorenzobettini.p2utils.p2composite;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.core.runtime.NullProgressMonitor;
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
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}/compositerepo", property = "outputDir", required = true)
	private File outputDirectory;

	@Component
	private IProvisioningAgent agent;

	public void execute() throws MojoExecutionException {
		try {
			CompositeRepositoryApplication app = new CompositeRepositoryApplication(agent);
			var destination = new RepositoryDescriptor();
			destination.setLocation(outputDirectory.toURI());
			app.addDestination(destination);
			app.run(new NullProgressMonitor());
		} catch (ProvisionException e) {
			throw new MojoExecutionException("Error creating composite repository", e);
		}
	}
}
