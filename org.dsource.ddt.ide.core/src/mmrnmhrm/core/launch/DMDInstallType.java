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
	
	private static final Path DMD_INSTALL_LIBRARY_PATH = new Path("src/phobos");
	private static final Path DMD2_INSTALL_LIBRARY_PATH = new Path("src/druntime/import");
	
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
		IPath path = installPath.append(DMD2_INSTALL_LIBRARY_PATH);
		if(path.toFile().exists() && path.toFile().isDirectory()) {
			// Found a D2 DMD install
			addLibraryLocationFromPath(locs, env, path);
			addLibraryLocationFromPath(locs, env, installPath.append("src/phobos"));
		} else {
			// Can only be a D1 DMD install
			path = installPath.append(DMD_INSTALL_LIBRARY_PATH);
			addLibraryLocationFromPath(locs, env, path);
		}
	}
	
}
