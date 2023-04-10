package io.github.lorenzobettini.p2utils.p2composite;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.internal.repository.tools.CompositeRepositoryApplication;
import org.eclipse.equinox.p2.internal.repository.tools.RepositoryDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link CompositeRepositoryApplication} to be injected in a mojo, with
 * some easier to use methods.
 * 
 * It must NOT be a {@link Singleton} because {@link CompositeRepositoryApplication}
 * resets the lists of children to add/remove to null so using the same instance again
 * would lead to a {@link NullPointerException}.
 * 
 * @author Lorenzo Bettini
 *
 */
@Named
public class CustomCompositeRepositoryApplication extends CompositeRepositoryApplication {

	private static final String P2_INDEX_CONTENTS = """
			version=1
			metadata.repository.factory.order=compositeContent.xml,\\!
			artifact.repository.factory.order=compositeArtifacts.xml,\\!""";

	private File outputDirectory;

	private final Logger logger = LoggerFactory.getLogger(CustomCompositeRepositoryApplication.class);

	@Inject
	public CustomCompositeRepositoryApplication(IProvisioningAgent agent) {
		super(agent);
	}

	public void addDestination(File outputDirectory, String name, boolean atomic, boolean compressed) {
		this.outputDirectory = outputDirectory;
		var destination = new RepositoryDescriptor();
		destination.setLocation(outputDirectory.toURI());
		destination.setAtomic("" + atomic);
		destination.setCompressed(compressed);
		destination.setName(name);
		addDestination(destination);
	}

	public void addChild(String child) throws URISyntaxException {
		logger.info("Adding " + child);
		super.addChild(fromStringToRepositoryDescriptor(child));
	}

	public void removeChild(String child) throws URISyntaxException {
		logger.info("Removing " + child);
		super.removeChild(fromStringToRepositoryDescriptor(child));
	}

	public void run() throws ProvisionException, IOException {
		super.run(new NullProgressMonitor());
		logger.info("Generating p2.index");
		Files.writeString(new File(outputDirectory, "p2.index").toPath(),
				P2_INDEX_CONTENTS);
		logger.info("Done");
	}

	private RepositoryDescriptor fromStringToRepositoryDescriptor(String child) throws URISyntaxException {
		var childRepo = new RepositoryDescriptor();
		childRepo.setLocation(URIUtil.fromString(child));
		return childRepo;
	}

}
