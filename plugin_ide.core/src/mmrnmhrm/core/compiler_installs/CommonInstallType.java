package mmrnmhrm.core.compiler_installs;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.DeeCore;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.LibraryLocation;

public abstract class CommonInstallType extends AbstractInterpreterInstallType {
	
	public static class DeeLaunchingPlugin extends DeeCore { // alias to DeeCore
	}
	
	public CommonInstallType() {
		super();
	}
	
	@Override
	public String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected String getPluginId() {
		return DeeLaunchingPlugin.PLUGIN_ID;
	}
	
	@Override
	protected ILog getLog() {
		return DeeLaunchingPlugin.getInstance().getLog();
	}
	
	public IFileHandle directoryHasCompilerPresent(Location exeDir) {
		String possibleCompilerExeNames[] = getPossibleInterpreterNames();
		
		for (String possibleCompilerExeName : possibleCompilerExeNames) {
			Location compileExeLocation = exeDir.resolveOrNull(possibleCompilerExeName);
			if(compileExeLocation != null && compileExeLocation.toFile().isFile()) {
				return LocalEnvironment.getInstance().getFile(compileExeLocation.toUri());
			}
			// Try .exe extension. Note, it is intentional that both extensions are checked regardless of 
			// what actual platform we are on. 
			compileExeLocation = exeDir.resolveOrNull(possibleCompilerExeName + ".exe");
			if(compileExeLocation != null && compileExeLocation.toFile().isFile()) {
				return LocalEnvironment.getInstance().getFile(compileExeLocation.toUri());
			}
		}
		
		return null;
	}
	
	@Override
	public synchronized LibraryLocation[] getDefaultLibraryLocations(IFileHandle installLocation,
			EnvironmentVariable[] variables, IProgressMonitor monitor) {
		//return super.getDefaultLibraryLocations(installLocation, variables, monitor);
		/* Unlike the parent class, this InstallType does not find library paths by
		 * running some kind of external executable, like Ruby or Python.
		 * It just adds some predefined path. */
		
		List<LibraryLocation> locations = new ArrayList<LibraryLocation>(); 
		addDefaultLibraryLocations(installLocation, locations); 
		return locations.toArray(new LibraryLocation[0]);
	}
	
	protected abstract void addDefaultLibraryLocations(IFileHandle executableLocation, 
		List<LibraryLocation> locs);
	
	
	protected void addExpectedLibraryLocation(List<LibraryLocation> locs, IEnvironment env, IPath path) {
		if(!path.toFile().isDirectory()) {
			DeeCore.logError("Missing expected library directory: " + path.toString() 
				+ " for install type: " + getName());
			return;
		}
		addLibraryLocationFromPath(locs, env, path);
	}
	
	protected static void addLibraryLocationFromPath(List<LibraryLocation> locs, IEnvironment env, IPath path) {
		LibraryLocation loc = new LibraryLocation(EnvironmentPathUtils.getFullPath(env, path));
		locs.add(loc);
	}
	
	@Override
	protected IPath createPathFile(IDeployment deployment) throws IOException {
		Assert.fail("Does not run lookup executable"); return null;
	}
	
}