package mmrnmhrm.core.launch;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.core.Assert;
import mmrnmhrm.core.DeeCore;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
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
	
	protected abstract void addDefaultLibraryLocations(IFileHandle executableLocation, List<LibraryLocation> locs);
	
	
	protected static void addLibraryLocationFromPath(List<LibraryLocation> locs, IEnvironment env, IPath path) {
		LibraryLocation loc = new LibraryLocation(EnvironmentPathUtils.getFullPath(env, path));
		locs.add(loc);
	}
	
	
	@Override
	protected IPath createPathFile(IDeployment deployment) throws IOException {
		Assert.fail("Does not run lookup executable"); return null;
	}
	
	// Generating the InstallName not supported yet
	/*
	public String generateAutomaticInstallName(File installLocation) {
		Process process = null;
		String[] env = extractEnvironment();
		String path = installLocation.getAbsolutePath();
		String[] cmdLine = new String[]{ path };
		
		try {
			process = Runtime.getRuntime().exec(cmdLine, env);
			String line = readLine(process);
			int ix = line.indexOf(" v");
			if(ix == -1)
				return null;
			return line.substring(ix+2);
		} catch (IOException e) {
		    Status status = DeeCore.createErrorStatus(
		    		"Error running DMD to determine version", e); 
			LangCore.log(new CoreException(status));
			return null;
		}
	}

	private static String readLine(Process process) throws IOException {
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		try {
			// FIXME: when used code
			is.close(); is.close();
			isr.close(); isr.close();
			br.close(); br.close();
			return br.readLine();
		} finally {
			is.close(); is.close();
			isr.close(); isr.close();
			br.close(); br.close();
		}
	}
	 */
	
}