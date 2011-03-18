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
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;

public class DeeDmdInstallType extends AbstractInterpreterInstallType {
	
	public static final String INSTALLTYPE_ID = DeeCore.EXTENSIONS_IDPREFIX+"launching.deeDmdInstallType";
	
	private static final Path DMD_INSTALL_LIBRARY_PATH = new Path("src/phobos");
	private static final Path DMD2_INSTALL_LIBRARY_PATH = new Path("src/druntime/import");
	
	public static class DeeLaunchingPlugin extends DeeCore { // alias to DeeCore
	}
	private static String[] interpreterNames = { "dmd" };
	
	public static boolean isStandardLibraryEntry(IBuildpathEntry entry) {
		// TODO: do this differently
		IPath path = entry.getPath();
		int numSegs = path.segmentCount();
		return entry.isExternal() && path.isAbsolute()
			&& (
				(path.lastSegment().matches("phobos") && path.segment(numSegs-2).matches("src")) ||
				(path.lastSegment().matches("import") && path.segment(numSegs-2).matches("druntime"))
			);
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
	public String getName() {
		return "DMD";
	}
	
	@Override
	protected String[] getPossibleInterpreterNames() {
		return interpreterNames;
	}
	
	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new DeeInstall(this, id);
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
	
	private void addDefaultLibraryLocations(IFileHandle executableLocation, List<LibraryLocation> locs) {
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
	
	
	private static void addLibraryLocationFromPath(List<LibraryLocation> locs, IEnvironment env, IPath path) {
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
