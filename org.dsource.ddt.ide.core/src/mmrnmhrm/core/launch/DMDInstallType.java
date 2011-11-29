package mmrnmhrm.core.launch;

import java.util.List;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstall;
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
	
	@Override
	protected void addDefaultLibraryLocations(IFileHandle executableLocation, List<LibraryLocation> locs) {
		IEnvironment env = executableLocation.getEnvironment();
		IPath installPath = executableLocation.getPath().removeLastSegments(3);
		IPath path = installPath.append("src/druntime/import");
		if(path.toFile().exists() && path.toFile().isDirectory()) {
			// Found a D2 DMD install
			addLibraryLocationFromPath(locs, env, path);
			addLibraryLocationFromPath(locs, env, installPath.append(new Path("src/phobos")));
			return;
		} 
		path = installPath.append("src/phobos");
		if(path.toFile().exists() && path.toFile().isDirectory()) {
			// if "druntime/import" doesn't exist, it's likely a D1 DMD install
			addLibraryLocationFromPath(locs, env, path);
			return;
		}
		path = installPath.append("include/d/dmd/druntime/import");
		if(path.toFile().exists() && path.toFile().isDirectory()) {
			// Found a D2 DMD install with Unix style install
			addLibraryLocationFromPath(locs, env, path);
			addLibraryLocationFromPath(locs, env, installPath.append("include/d/dmd/phobos"));
			return;
		}
		// TODO: should we throw an error?
	}
	
}