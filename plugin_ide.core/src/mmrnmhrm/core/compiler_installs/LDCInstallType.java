package mmrnmhrm.core.compiler_installs;

import java.util.List;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;

public class LDCInstallType extends CommonInstallType {
	
	public static final String INSTALLTYPE_ID = DeeCore.PLUGIN_ID + ".launching.LDCInstallType";
	
	@Override
	public String getName() {
		return "LDC2";
	}
	
	protected static String[] INTERPRETER_EXECUTABLE_NAMES = { "ldc2", "ldc" };
	
	@Override
	protected String[] getPossibleInterpreterNames() {
		return INTERPRETER_EXECUTABLE_NAMES;
	}
	
	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new CommonDeeInstall(this, id);
	}
	
	@Override
	protected void addDefaultLibraryLocations(IFileHandle executableLocation, List<LibraryLocation> locs) {
		IEnvironment env = executableLocation.getEnvironment();
		IPath installPath = executableLocation.getPath().removeLastSegments(2);
		
		tryLDCLibFolder(locs, env, installPath);
	}
	
	protected boolean tryLDCLibFolder(List<LibraryLocation> locs, IEnvironment env, IPath installBasePath) {
		IPath importBaseDir = installBasePath.append("import");
		if(!importBaseDir.toFile().isDirectory()) {
			DeeCore.logError("Missing expected dir from LDC install:" + importBaseDir);
			return false;
		} else {
			addExpectedLibraryLocation(locs, env, importBaseDir.append("ldc"));
			addExpectedLibraryLocation(locs, env, importBaseDir.append("core"));
			addExpectedLibraryLocation(locs, env, importBaseDir.append("std"));
			return true;
		}
	}
	
}