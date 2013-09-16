package mmrnmhrm.core.launch;

import java.io.File;
import java.util.List;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;

public class GDCInstallType extends CommonInstallType {
	
	public static final String INSTALLTYPE_ID = DeeCore.EXTENSIONS_IDPREFIX+"launching.GDCInstallType";
	
	@Override
	public String getName() {
		return "GDC";
	}
	
	private static String[] INTERPRETER_EXECUTABLE_NAMES = { "gdc" };
	
	@Override
	protected String[] getPossibleInterpreterNames() {
		return INTERPRETER_EXECUTABLE_NAMES;
	}
	
	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new GDCInstall(this, id);
	}
	
	@Override
	protected void addDefaultLibraryLocations(IFileHandle executableLocation, List<LibraryLocation> locs) {
		IEnvironment env = executableLocation.getEnvironment();
		IPath installPath = executableLocation.getPath().removeLastSegments(2);
		IPath baseLibPath = installPath.append("include/d2");
		
		File[] listFiles = baseLibPath.toFile().listFiles();
		if(listFiles == null) 
			return;
		
		for (int i = 0; i < listFiles.length; i++) {
			File libEntry = listFiles[i];
			if(libEntry.isDirectory() && new File(libEntry, "object.di").exists()) {
				addLibraryLocationFromPath(locs, env, baseLibPath.append(libEntry.getName()));
				break;
			}
		}
		
	}
	
}
