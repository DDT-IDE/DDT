package mmrnmhrm.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import dtool.tests.DToolTestResources;

/**
 * Initialize {@link DToolTestResources} working dir to use the Eclipse/OSGI instance location
 */
public class DToolResourcesPluginAdapter 
	extends DToolTestResources // for visibility purposes 
{
	
	public static void initialize() {
		
		Location instanceLocation = Platform.getInstanceLocation();
		URI uri;
		try {
			uri = instanceLocation.getURL().toURI();
		} catch (URISyntaxException e) {
			throw assertFail();
		}
		
		String workingDirPath = new File(uri).getAbsolutePath();
		DToolTestResources.initialize(workingDirPath);
		
		assertEquals(workingDirPath, DToolTestResources.getInstance().getWorkingDir().getAbsolutePath());
	}
	
}
