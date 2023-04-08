/**
 * 
 */
package io.github.lorenzobettini.p2utils.p2composite;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to copy a directory contents into a destination
 * directory, which will be created.
 * 
 * @author Lorenzo Bettini
 *
 */
public class P2CopyUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(P2CopyUtils.class);

	private P2CopyUtils() {
		// just static methods
	}

	/**
	 * Copy the contents of the sourceDirectory into outputDirectory
	 * as a new directory with name copyAs.
	 * 
	 * @param sourceDirectory
	 * @param outputDirectory
	 * @param copyAs
	 * @throws IOException 
	 */
	public static void copyDirectoryContentsAs(File sourceDirectory, File outputDirectory, String copyAs) throws IOException {
		File destination = new File(outputDirectory, copyAs);
		LOGGER.info("Copying contents of " + sourceDirectory);
		LOGGER.info("Into " + destination);
		FileUtils.copyDirectory(sourceDirectory, destination);
		LOGGER.info("Done");
	}
}
