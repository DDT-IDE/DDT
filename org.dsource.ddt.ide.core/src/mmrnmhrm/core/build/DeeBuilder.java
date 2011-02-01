package mmrnmhrm.core.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.launch.DeeDmdInstallType;
import mmrnmhrm.core.launch.DeeInstall;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;

import dtool.DeeNamingRules;
import dtool.Logg;

public class DeeBuilder {
	
	public static String getPreviewBuildCommands(IScriptProject deeProj, DeeProjectOptions overlayOptions, 
			IProgressMonitor monitor) {
		DeeBuilder builder = new DeeBuilder();
		try {
			//builder.dontCollectModules = true;
			builder.collectBuildUnits(deeProj, monitor);
		} catch (CoreException e) {
			DeeCore.log(e);
			return "Cannot determine preview: " + e;
		}
		//builder.buildModules = Collections.singletonList("<<files.d>>");
		//DeeProjectOptions options = DeeModel.getDeeProjectInfo(deeProj);
		
		//buildCommands = buildCommands.replace("$DEEBUILDER.SRCMODULES", "#DEEBUILDER.SRCMODULES#");
		String buildCommands = builder.postProcessBuildCommands(overlayOptions);
		//buildCommands.replace("#DEEBUILDER.SRCMODULES#", "$DEEBUILDER.SRCMODULES");
		return buildCommands;
	}
	
	public static String getDefaultBuildFileData() {
		return 
		"-od$DEEBUILDER.OUTPUTPATH\n" +
		"-of$DEEBUILDER.OUTPUTEXE\n" +
		//"$DEEBUILDER.EXTRAOPTS\n" +
		"$DEEBUILDER.SRCLIBS.-I\n" +
		"$DEEBUILDER.SRCFOLDERS.-I\n" +
		"$DEEBUILDER.SRCMODULES\n";
	}
	
	public static String getDefaultBuildToolCmdLine() {
		return "$DEEBUILDER.COMPILEREXEPATH @build.rf";
	}
	
	
	private boolean dontCollectModules;
	
	private List<String> libraryEntries;
	private List<String> folderEntries;
	private List<String> buildModules;
	private IPath compilerPath;
	private DeeInstall deeCompiler;
	
	protected Iterable<IDeeBuilderListener> listeners;
//	private IPath standardLibPath;
	
	public DeeBuilder() {
		dontCollectModules = false;
		
		buildModules = new ArrayList<String>();
		libraryEntries = new ArrayList<String>();
		folderEntries = new ArrayList<String>();
	}
	
	protected void setListeners(Iterable<IDeeBuilderListener> iterable) {
		this.listeners = iterable;
	}
	
	private DeeProjectOptions getProjectOptions(IScriptProject deeProj) {
		return DeeModel.getDeeProjectInfo(deeProj);
	}
	
	
	public void collectBuildUnits(IScriptProject deeProj, IProgressMonitor monitor) throws CoreException {
		
		IInterpreterInstall install = ScriptRuntime.getInterpreterInstall(deeProj);
		if(!(install instanceof DeeInstall)) {
			throw DeeCore.createCoreException(
					"Could not find a D compiler/interpreter associated to the project", null);
		}
		deeCompiler = ((DeeInstall) install);
		
		compilerPath = deeCompiler.getCompilerBasePath();
		assertNotNull(compilerPath);
		
		IBuildpathEntry[] buildpathEntries = deeProj.getResolvedBuildpath(true);
		
		for(int i = 0; i < buildpathEntries.length; i++) {
			IBuildpathEntry entry = buildpathEntries[i];
			Logg.builder.println("Builder:: In entry: " + entry);
			

			if(entry.getEntryKind() == IBuildpathEntry.BPE_SOURCE) {
				processSourceEntry(deeProj, entry, monitor);
			} else if(entry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY) {
				processLibraryEntry(entry);
			}
		}
		
	}
	
	private void processLibraryEntry(IBuildpathEntry entry) throws CoreException {
		if(IBuildpathEntry.BUILTIN_EXTERNAL_ENTRY.isPrefixOf(entry.getPath())) {
			// Ignore builtin entry
		} else if(DeeDmdInstallType.isStandardLibraryEntry(entry)) {
			// Identify StandardLib entry apart from other entries 
//			standardLibPath = entry.getPath();
		} else if(!entry.isExternal()) {
			IPath projectBasedPath = entry.getPath().removeFirstSegments(1);
			libraryEntries.add(projectBasedPath.toOSString());
		}
	}
	
	private void processSourceEntry(IScriptProject deeProj, IBuildpathEntry entry, IProgressMonitor monitor)
			throws CoreException {
		IProject project = deeProj.getProject();
		
		if(entry.isExternal()) {
			throw DeeCore.createCoreException("Unsupported external source entry" + entry, null);
		}
		
		IPath projectBasedPath = entry.getPath().removeFirstSegments(1);
		IContainer entryContainer = (IContainer) project.findMember(projectBasedPath);
		

		String containerPathStr = entryContainer.isLinked(IResource.CHECK_ANCESTORS) ?
				entryContainer.getLocation().toOSString()
				: projectBasedPath.toOSString();
		
		folderEntries.add(containerPathStr);
		if(dontCollectModules)
			return;
		
		if(entryContainer != null)
			proccessSourceFolder(entryContainer, monitor);
		
	}
	
	protected void proccessSourceFolder(IContainer container,
			IProgressMonitor monitor) throws CoreException {
		
		IResource[] members = container.members(false);
		for(int i = 0; i < members.length; i++) {
			IResource resource = members[i];
			if(resource.getType() == IResource.FOLDER) {
				proccessSourceFolder((IFolder) resource, monitor);
			} else if(resource.getType() == IResource.FILE) {
				processResource((IFile) resource);
			} else {
				assertFail();
			}
		}
	}
	
	
	protected void processResource(IFile file) {
		String modUnitName = file.getName();
		IPath projectRelativePath = file.getProjectRelativePath();
		if(DeeNamingRules.isValidCompilationUnitName(modUnitName)) {
			String resourcePathStr = file.isLinked(IResource.CHECK_ANCESTORS) ?
					file.getLocation().toOSString()
					: projectRelativePath.toOSString();
			
			buildModules.add(resourcePathStr);
			//addCompileBuildUnit(resource);
		} else {
		}
		//String extName = projectRelativePath.getFileExtension();
		//String modName = projectRelativePath.removeFileExtension().lastSegment();
	}
	
	protected void compileModules(IScriptProject deeProj) throws CoreException {
		
		DeeProjectOptions options = getProjectOptions(deeProj);
		//IFolder outputFolder = options.getOutputFolder();
		
		String buildCommands = postProcessBuildCommands(options);
		
		IFile file = deeProj.getProject().getFile(options.getBuildFile());
		
		byte[] buf = buildCommands.getBytes();
		InputStream is = new ByteArrayInputStream(buf);
		if(file.exists() == false) {
			file.create(is, false, null);
		} else {
			file.setContents(is, IResource.NONE, null);
		}
		
		Logg.main.println("--------  Build Commands:  --------\n" + buildCommands);
		for (IDeeBuilderListener listener : listeners) {
			listener.buildCommandsCreated(buildCommands);
		}
	}
	
	protected String postProcessBuildCommands(DeeProjectOptions options) {
		StringBuilder strb = new StringBuilder(options.getBuildCommands());
		
		IPath outputPath = options.getOutputFolder().getProjectRelativePath();
		String outputDir = outputPath.toOSString();
		while(StringUtil.replace(strb, "$DEEBUILDER.OUTPUTPATH", outputDir))
			;
		
		String outputExe = outputPath.append(options.getArtifactName()).toOSString();
		while(StringUtil.replace(strb, "$DEEBUILDER.OUTPUTEXE", outputExe))
			;
		

		{
			String srcLibs = "";
			for(String srcLib : libraryEntries) {
				srcLibs += "-I" + srcLib + "\n";
			}
			while(StringUtil.replace(strb, "$DEEBUILDER.SRCLIBS.-I", srcLibs))
				;
		}
		
		{
			String srcFolders = "";
			for(String srcfolder : folderEntries) {
				srcFolders += "-I" + srcfolder + "\n";
			}
			while(StringUtil.replace(strb, "$DEEBUILDER.SRCFOLDERS.-I", srcFolders))
				;
		}
		

		{
			String srcModules = "";
			for(String srcModule : buildModules) {
				srcModules += srcModule + "\n";
			}
			while(StringUtil.replace(strb, "$DEEBUILDER.SRCMODULES", srcModules))
				;
		}
		
		String localCompilerPath = EnvironmentPathUtils.getLocalPath(compilerPath).toOSString();
		String localCompilerFullPath = EnvironmentPathUtils.getLocalPath(deeCompiler.getCompilerFullPath()).toOSString();
		while(StringUtil.replace(strb, "$DEEBUILDER.COMPILERPATH", localCompilerPath))
			;
		while(StringUtil.replace(strb, "$DEEBUILDER.COMPILEREXEPATH", localCompilerFullPath))
			;
		
		return strb.toString();
	}
	
	
	public void runBuilder(IScriptProject deeProj, IProgressMonitor monitor)
			throws CoreException {
		
		DeeProjectOptions options = getProjectOptions(deeProj);
		IPath workDir = deeProj.getProject().getLocation();
		
		//String buildToolExePath = splitSpaces(options.compilerOptions.buildToolCmdLine);
		//String[] cmdLine = { buildToolExePath, options.getBuilderCommandLine() };
		
		String[] cmdLine = options.getBuilderFullCommandLine();
		
		// Substitute vars in cmdLine
		for(int i = 0; i < cmdLine.length; i++) {
			String compilerBasePath = EnvironmentPathUtils.getLocalPath(compilerPath).toOSString();
			cmdLine[i] = cmdLine[i].replace("$DEEBUILDER.COMPILERPATH", compilerBasePath);
			String compilerFullPath = EnvironmentPathUtils.getLocalPath(deeCompiler.getCompilerFullPath()).toOSString();
			cmdLine[i] = cmdLine[i].replace("$DEEBUILDER.COMPILEREXEPATH", compilerFullPath);
		}
		
		if(cmdLine.toString().length() > 30000)
			throw DeeCore.createCoreException("D Build: Error cannot build: cmd-line too big", null);
		
		final ProcessBuilder builder = new ProcessBuilder(cmdLine);

		addCompilerPathToBuilderEnvironment(builder);
		builder.directory(workDir.toFile());
		
		for (IDeeBuilderListener listener : listeners) {
			String[] cmdLineCopy = ArrayUtil.createFrom(builder.command(), String.class);
			listener.processAboutToStart(cmdLineCopy);
		}
		
		startProcess(monitor, builder);
	}
	
	protected void startProcess(IProgressMonitor monitor, final ProcessBuilder builder) throws CoreException {
		try {
			Process proc = builder.start();
			ExternalProcessAdapter processUtil = new ExternalProcessAdapter() {
				@Override
				protected void handleReadLine(String line) {
					Logg.builder.println("\t" + line);
					for (IDeeBuilderListener listener : listeners) {
						listener.handleProcessOutputLine(line);
					}
				}
			};
			processUtil.waitForProcess(monitor, proc);
			Logg.builder.println(">>  Exit value: " + proc.exitValue());
		} catch (IOException e) {
			throw DeeCore.createCoreException("D Build: Error exec'ing.", e);
		} catch (InterruptedException e) {
			throw DeeCore.createCoreException("D Build: Interrupted.", e);
		}
	}
	
	protected void addCompilerPathToBuilderEnvironment(final ProcessBuilder builder) {
		if(compilerPath == null)
			return;
		Map<String, String> env = builder.environment();
		String pathEnvKey = "PATH";
		String pathStr = env.get(pathEnvKey);
		if(pathStr == null) {
			pathEnvKey = "Path";
			pathStr = env.get(pathEnvKey);
		}
		if(pathStr == null) {
			pathEnvKey = "path";
			pathStr = env.get(pathEnvKey);
		}
		String localCompilerPath = EnvironmentPathUtils.getLocalPath(compilerPath).toOSString();
		pathStr = localCompilerPath + File.pathSeparator + pathStr;
		env.put(pathEnvKey, pathStr);
	}
	
}
