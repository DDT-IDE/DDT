package mmrnmhrm.core.compiler_installs;

import java.util.List;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;

import dtool.util.NewUtils;

public class GenericInstallType extends CommonInstallType {
	
	public static final String INSTALLTYPE_ID = DeeCore.PLUGIN_ID + ".launching.GenericInstallType";
	
	@Override
	public String getName() {
		return "Generic/other D compiler";
	}
	
	@Override
	public IStatus validateInstallLocation(IFileHandle installLocation, EnvironmentVariable[] variables,
		LibraryLocation[] libraryLocations, IProgressMonitor monitor) {
		// Generic/other compiler can be at any location
		return createStatus(IStatus.OK, null, null);
	}
	
	@Override
	public IStatus validatePossiblyName(IFileHandle installLocation) {
		return super.validatePossiblyName(installLocation);
	}
	
	@Override
	protected String[] getPossibleInterpreterNames() {
		return NewUtils.EMPTY_STRING_ARRAY;
	}
	
	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new GenericInstall(this, id);
	}
	
	public class GenericInstall extends CommonDeeInstall { 
		
		public GenericInstall(GenericInstallType type, String id) {
			super(type, id);
		}
		
	}
	
	@Override
	protected void addDefaultLibraryLocations(IFileHandle executableLocation, List<LibraryLocation> locs) {
		// Generic install adds no libraryLocations
	}
	
}