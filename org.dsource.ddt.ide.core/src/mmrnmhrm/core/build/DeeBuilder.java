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
import mmrnmhrm.core.launch.DmdInstall;
import mmrnmhrm.core.projectmodel.DeeProjectModel;
import mmrnmhrm.core.projectmodel.DeeProjectOptions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;

import dtool.DeeNamingRules;
import dtool.Logg;
import dtool.SimpleLogger;

public class DeeBuilder {
	
	public static final SimpleLogger buildLog = Logg.builder;
	
	public static String getPreviewBuildCommands(IScriptProject deeProj, DeeProjectOptions overlayOptions,
			IProgressMonitor monitor) {
		DeeBuilder builder; 
		try {
			builder = new DeeBuilder(deeProj);
			//builder.dontCollectModules = true;
			builder.collectBuildUnits(monitor);
		} catch(CoreException e) {
			DeeCore.log(e);
			return "Cannot determine preview: " + e;
		}
		
		String buildCommands = builder.resolveVarsInBuildCommands(overlayOptions);
		return buildCommands;
	}
	
	protected final IScriptProject deeProj;
	protected final DmdInstall deeCompiler;
	
	private boolean dontCollectModules;
	
	private List<String> libraryEntries;
	private List<String> folderEntries;
	private List<String> buildModules;
	private IPath compilerPath;
	
	protected Iterable<IDeeBuilderListener> listeners;
//	private IPath standardLibPath;
	
	public DeeBuilder(IScriptProject deeProj) throws CoreException {
		this.deeProj = deeProj;
		
		IInterpreterInstall install = ScriptRuntime.getInterpreterInstall(deeProj);
		if(!(install instanceof DmdInstall)) {
			throw DeeCore.createCoreException(
					"Could not find a D compiler/interpreter associated to the project", null);
		}
		deeCompiler = ((DmdInstall) install);
		
		dontCollectModules = false;
		
		buildModules = new ArrayList<String>();
		libraryEntries = new ArrayList<String>();
		folderEntries = new ArrayList<String>();
	}
	
	protected void setListeners(Iterable<IDeeBuilderListener> iterable) {
		this.listeners = iterable;
	}
	
	protected DeeProjectOptions getProjectOptions(IScriptProject deeProj) {
		return DeeProjectModel.getDeeProjectInfo(deeProj);
	}
	
	
	public void collectBuildUnits(IProgressMonitor monitor) throws CoreException {
		
		compilerPath = deeCompiler.getCompilerBasePath();
		assertNotNull(compilerPath);
		
		IBuildpathEntry[] buildpathEntries = deeProj.getResolvedBuildpath(true);
		
		for(int i = 0; i < buildpathEntries.length; i++) {
			IBuildpathEntry entry = buildpathEntries[i];
			buildLog.println("Builder:: In entry: " + entry);
			
			
			if(entry.getEntryKind() == IBuildpathEntry.BPE_SOURCE) {
				processSourceEntry(entry, monitor);
			} else if(entry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY) {
				processLibraryEntry(entry);
			}
		}
		
	}
	
	protected void processLibraryEntry(IBuildpathEntry entry) throws CoreException {
		if(IBuildpathEntry.BUILTIN_EXTERNAL_ENTRY.isPrefixOf(entry.getPath())) {
			// Ignore builtin entry
		} else if(entry.isExternal()) {
			// Assume it is standard library
			// Ignore Standard Lib entry  
		} else if(!entry.isExternal()) {
			// Should not happen
		}
	}
	
	protected void processSourceEntry(IBuildpathEntry entry, IProgressMonitor monitor)
			throws CoreException {
		IProject project = deeProj.getProject();
		
		if(entry.isExternal()) {
			throw DeeCore.createCoreException("Unsupported external source entry" + entry, null);
		}
		
		IPath projectBasedPath = entry.getPath().removeFirstSegments(1);
		IContainer entryContainer = (IContainer) project.findMember(projectBasedPath);
		
		
		String containerPathStr = entryContainer.isLinked(IResource.CHECK_ANCESTORS) ?
				entryContainer.getLocation().toOSString() :
				projectBasedPath.toOSString();
		
		folderEntries.add(containerPathStr);
		if(dontCollectModules)
			return;
		
		if(entryContainer != null) {
			proccessSourceFolder(entryContainer, monitor);
		}
		
	}
	
	protected void proccessSourceFolder(IContainer container, IProgressMonitor monitor) throws CoreException {
		IResource[] members = container.members(false);
		for(int i = 0; i < members.length; i++) {
			throwIfCanceled(monitor);
			
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
	
	protected static void throwIfCanceled(IProgressMonitor monitor) {
		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}
	
	protected void processResource(IFile file) {
		String modUnitName = file.getName();
		IPath projectRelativePath = file.getProjectRelativePath();
		if(DeeNamingRules.isValidCompilationUnitName(modUnitName)) {
			String resourcePathStr = file.isLinked(IResource.CHECK_ANCESTORS) ?
					file.getLocation().toOSString() :
					projectRelativePath.toOSString();
			
			buildModules.add(resourcePathStr);
			//addCompileBuildUnit(resource);
		} else {
		}
		//String extName = projectRelativePath.getFileExtension();
		//String modName = projectRelativePath.removeFileExtension().lastSegment();
	}
	
	protected void compileModules(IProgressMonitor monitor) throws CoreException {
		throwIfCanceled(monitor);
		
		DeeProjectOptions options = getProjectOptions(deeProj);
		//IFolder outputFolder = options.getOutputFolder();
		
		String buildCommands = resolveVarsInBuildCommands(options);
		
		IFile responseFile = deeProj.getProject().getFile(options.getBuildFile());
		
		byte[] buf = buildCommands.getBytes();
		InputStream is = new ByteArrayInputStream(buf);
		if(responseFile.exists() == false) {
			responseFile.create(is, false, null);
		} else {
			responseFile.setContents(is, IResource.NONE, null);
		}
		
		Logg.main.println("--------  Build Commands:  --------\n" + buildCommands);
		
		fireBuildCommandsCreated(buildCommands);
	}
	
	protected String resolveVarsInBuildCommands(DeeProjectOptions options) {
		StringBuilder strb = new StringBuilder(options.getBuildCommands());
		
		IPath outputPath = options.getOutputFolder().getProjectRelativePath();
		String outputDir = outputPath.toOSString();
		while(StringUtil.replace(strb, "$DEEBUILDER.OUTPUTPATH", encodeString(outputDir)))
			;
		
		String outputExe = outputPath.append(options.getArtifactName()).toOSString();
		while(StringUtil.replace(strb, "$DEEBUILDER.OUTPUTEXE", encodeString(outputExe)))
			;
		
		
		{
			String srcLibs = "";
			for(String srcLib : libraryEntries) {
				srcLibs += "-I" + encodeString(srcLib) + "\n";
			}
			while(StringUtil.replace(strb, "$DEEBUILDER.SRCLIBS.-I", srcLibs))
				;
		}
		
		{
			String srcFolders = "";
			for(String srcfolder : folderEntries) {
				srcFolders += "-I" + encodeString(srcfolder) + "\n";
			}
			while(StringUtil.replace(strb, "$DEEBUILDER.SRCFOLDERS.-I", srcFolders))
				;
		}
		
		
		{
			String srcModules = "";
			for(String srcModule : buildModules) {
				srcModules += encodeString(srcModule) + "\n";
			}
			while(StringUtil.replace(strb, "$DEEBUILDER.SRCMODULES", srcModules))
				;
		}
		
		IPath compilerFullPath = deeCompiler.getCompilerFullPath();
		String localCompilerPath = EnvironmentPathUtils.getLocalPath(compilerPath).toOSString();
		String localCompilerFullPath = EnvironmentPathUtils.getLocalPath(compilerFullPath).toOSString();
		while(StringUtil.replace(strb, "$DEEBUILDER.COMPILERPATH", localCompilerPath))
			;
		while(StringUtil.replace(strb, "$DEEBUILDER.COMPILEREXEPATH", localCompilerFullPath))
			;
		
		return strb.toString();
	}
	
	protected String encodeString(String str) {
		return "\"" + DeeBuilderUtils.escapeQuotes(str) + "\"";
	}
	
	public void runBuilder(IProgressMonitor monitor) throws CoreException {
		
		DeeProjectOptions options = getProjectOptions(deeProj);
		IPath workDir = deeProj.getProject().getLocation();
		
		//String buildToolExePath = splitSpaces(options.compilerOptions.buildToolCmdLine);
		//String[] cmdLine = { buildToolExePath, options.getBuilderCommandLine() };
		
		String[] cmdLine = options.getBuilderFullCommandLine();
		
		// Substitute vars in cmdLine
		for(int i = 0; i < cmdLine.length; i++) {
			String localCompilerBasePath = EnvironmentPathUtils.getLocalPath(compilerPath).toOSString();
			cmdLine[i] = cmdLine[i].replace("$DEEBUILDER.COMPILERPATH", localCompilerBasePath);
			IPath compilerFullPath = deeCompiler.getCompilerFullPath();
			String localCompilerFullPath = EnvironmentPathUtils.getLocalPath(compilerFullPath).toOSString();
			cmdLine[i] = cmdLine[i].replace("$DEEBUILDER.COMPILEREXEPATH", localCompilerFullPath);
		}
		
		if(cmdLine.toString().length() > 30000)
			throw DeeCore.createCoreException("D Build: Error cannot build: cmd-line too big", null);
		
		final ProcessBuilder builder = new ProcessBuilder(cmdLine);
		
		addCompilerPathToBuilderEnvironment(builder);
		builder.directory(workDir.toFile());
		
		fireProcessAboutToStart(builder);
		
		startProcess(monitor, builder);
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
	
	protected void startProcess(IProgressMonitor monitor, final ProcessBuilder builder) throws CoreException {
		try {
			Process proc = builder.start();
			ExternalProcessAdapter processUtil = new ExternalProcessAdapter() {
				@Override
				protected void handleReadLine(String line) {
					buildLog.println("\t" + line);
					fireHandleOutputLine(line);
				}
			};
			processUtil.waitForProcess(monitor, proc);
			buildLog.println(">>  Exit value: " + proc.exitValue());
		} catch(IOException e) {
			throw DeeCore.createCoreException("D Build: Error exec'ing.", e);
		} catch(InterruptedException e) {
			throw DeeCore.createCoreException("D Build: Interrupted.", e);
		}
	}
	
	
	protected void fireBuildCommandsCreated(String buildCommands) {
		for(IDeeBuilderListener listener : listeners) {
			listener.buildCommandsCreated(buildCommands);
		}
	}
	
	protected void fireProcessAboutToStart(final ProcessBuilder builder) {
		for(IDeeBuilderListener listener : listeners) {
			String[] cmdLineCopy = ArrayUtil.createFrom(builder.command(), String.class);
			listener.processAboutToStart(cmdLineCopy);
		}
	}
	
	protected void fireHandleOutputLine(String line) {
		for(IDeeBuilderListener listener : listeners) {
			listener.handleProcessOutputLine(line);
		}
	}
	
}
