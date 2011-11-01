package mmrnmhrm.core.launch;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.List;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;

public class GenericInstallType extends CommonInstallType {
	
	public static final String INSTALLTYPE_ID = DeeCore.EXTENSIONS_IDPREFIX+"launching.GenericInstallType";
	
	@Override
	public String getName() {
		return "Generic compiler";
	}
	
	@Override
	public IStatus validatePossiblyName(IFileHandle installLocation) {
		return createStatus(IStatus.OK, "", null);
	}
	
	@Override
	protected String[] getPossibleInterpreterNames() {
		throw assertFail();
	}
	
	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new GenericInstall(this, id);
	}
	
	@Override
	protected void addDefaultLibraryLocations(IFileHandle executableLocation, List<LibraryLocation> locs) {
		// Generic install adds no libraryLocations
	}
	
}
