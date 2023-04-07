package io.github.lorenzobettini.p2utils.p2composite;

import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.internal.repository.tools.CompositeRepositoryApplication;
import org.eclipse.equinox.p2.internal.repository.tools.RepositoryDescriptor;

/**
 * Custom {@link CompositeRepositoryApplication} to be injected in a mojo, with
 * some easier to use methods.
 * 
 * @author Lorenzo Bettini
 *
 */
@Named
@Singleton
public class CustomCompositeRepositoryApplication extends CompositeRepositoryApplication {

	@Inject
	public CustomCompositeRepositoryApplication(IProvisioningAgent agent) {
		super(agent);
	}

	public void addChild(String child) throws URISyntaxException {
		super.addChild(fromStringToRepositoryDescriptor(child));
	}

	public void removeChild(String child) throws URISyntaxException {
		super.removeChild(fromStringToRepositoryDescriptor(child));
	}

	public void run() throws ProvisionException {
		super.run(new NullProgressMonitor());
	}

	private RepositoryDescriptor fromStringToRepositoryDescriptor(String child) throws URISyntaxException {
		var childRepo = new RepositoryDescriptor();
		childRepo.setLocation(URIUtil.fromString(child));
		return childRepo;
	}
}
