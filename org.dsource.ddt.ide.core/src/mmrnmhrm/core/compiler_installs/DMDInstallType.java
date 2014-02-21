package mmrnmhrm.core.compiler_installs;

import java.util.List;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.LibraryLocation;

public class DMDInstallType extends CommonInstallType {
	
	public static final String INSTALLTYPE_ID = DeeCore.EXTENSIONS_IDPREFIX+"launching.deeDmdInstallType";
	
	@Override
	public String getName() {
		return "DMD";
	}
	
	private static String[] INTERPRETER_EXECUTABLE_NAMES = { "dmd" };
	
	@Override
	protected String[] getPossibleInterpreterNames() {
		return INTERPRETER_EXECUTABLE_NAMES;
	}
	
	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new DMDInstall(this, id);
	}
	
	public class DMDInstall extends CommonDeeInstall {
		public DMDInstall(IInterpreterInstallType type, String id) {
			super(type, id);
		}
	}
	
	@Override
	protected void addDefaultLibraryLocations(IFileHandle executableLocation, List<LibraryLocation> locs) {
		IEnvironment env = executableLocation.getEnvironment();
		
		final IPath exePath = executableLocation.getPath();
		final IPath dmdZipInstallPath = exePath.removeLastSegments(3);
		
		if(checkForDMD2InstallLocation(locs, env, dmdZipInstallPath.append("src")))
			return;
		
		IPath path = dmdZipInstallPath.append("src/phobos");
		if(path.toFile().exists() && path.toFile().isDirectory()) {
			// if "src/phobos" exists but "druntime/import" doesn't exist, it's likely a D1 DMD install
			addLibraryLocationFromPath(locs, env, path);
			return;
		}
		
		// exePath is /usr/bin/dmd
		if(checkForDMD2InstallLocation(locs, env, exePath.removeLastSegments(3).append("include/d/dmd")))
			return;
		
		if(checkForDMD2InstallLocation(locs, env, exePath.removeLastSegments(3).append("usr/include/dmd")))
			return;
		
		// Not sure there is any Linux layout like this, but no hard in trying:
		if(checkForDMD2InstallLocation(locs, env, exePath.removeLastSegments(3).append("include/dmd")))
			return;
		
		// TODO: should we throw an error or show an error dialog?
	}
	
	public boolean checkForDMD2InstallLocation(List<LibraryLocation> locs, IEnvironment env, IPath libBasePath) {
		IPath path = libBasePath.append("druntime/import");
		if(path.toFile().exists() && path.toFile().isDirectory()) {
			// Found a D2 DMD install with Unix style install
			addLibraryLocationFromPath(locs, env, path);
			addLibraryLocationFromPath(locs, env, libBasePath.append("phobos"));
			return true;
		}
		return false;
	}
	
}