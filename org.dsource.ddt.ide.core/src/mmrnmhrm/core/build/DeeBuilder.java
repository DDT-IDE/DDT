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
import mmrnmhrm.core.compiler_installs.CommonDeeInstall;
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

import dtool.Logg;
import dtool.SimpleLogger;
import dtool.project.DeeNamingRules;

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
	protected final CommonDeeInstall deeCompiler;
	
	private boolean dontCollectModules;
	
	private List<IPath> libraryEntries;
	private List<IPath> folderEntries;
	private List<IPath> buildModules;
	private IPath compilerPath;
	
	protected Iterable<IDeeBuilderListener> listeners;
//	private IPath standardLibPath;
	
	public DeeBuilder(IScriptProject deeProj) throws CoreException {
		this.deeProj = deeProj;
		
		IInterpreterInstall install = ScriptRuntime.getInterpreterInstall(deeProj);
		if(!(install instanceof CommonDeeInstall)) {
			throw DeeCore.createCoreException(
					"Could not find a D compiler/interpreter associated to the project", null);
		}
		deeCompiler = ((CommonDeeInstall) install);
		
		dontCollectModules = false;
		
		buildModules = new ArrayList<IPath>();
		libraryEntries = new ArrayList<IPath>();
		folderEntries = new ArrayList<IPath>();
	}
	
	protected void setListeners(Iterable<IDeeBuilderListener> iterable) {
		this.listeners = iterable;
	}
	
	protected DeeProjectOptions getProjectOptions(IScriptProject deeProj) {
		return DeeProjectModel.getDeeProjectInfo(deeProj);
	}
	
	
	public void collectBuildUnits(IProgressMonitor monitor) throws CoreException {
		
		compilerPath = deeCompiler.getCompilerDirectoryPath();
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
		
		/*BUG here NPE*/
		IPath containerPath = entryContainer.isLinked(IResource.CHECK_ANCESTORS) ?
				entryContainer.getLocation() :
				projectBasedPath;
		
		folderEntries.add(containerPath);
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
		if(DeeNamingRules.isValidCompilationUnitName(modUnitName, false)) {
			IPath resourcePath = file.isLinked(IResource.CHECK_ANCESTORS) ?
					file.getLocation() :
					projectRelativePath;
			
			buildModules.add(resourcePath);
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
		replacePathVar(strb, "DEEBUILDER.OUTPUTPATH", outputPath);
		
		IPath outputExePath = outputPath.append(options.getArtifactName());
		replacePathVar(strb, "DEEBUILDER.OUTPUTEXE", outputExePath);
		
		replaceMultiPathVar(strb, "DEEBUILDER.SRCLIBS.-I", "-I", libraryEntries);
		replaceMultiPathVar(strb, "DEEBUILDER.SRCFOLDERS.-I", "-I", folderEntries);
		replaceMultiPathVar(strb, "DEEBUILDER.SRCMODULES", "", buildModules);
		
		IPath localCompilerPath = EnvironmentPathUtils.getLocalPath(compilerPath);
		replacePathVar(strb, "DEEBUILDER.COMPILERPATH", localCompilerPath);
		
		IPath localCompilerExePath = EnvironmentPathUtils.getLocalPath(deeCompiler.getCompilerExecutablePath());
		replacePathVar(strb, "DEEBUILDER.COMPILEREXEPATH", localCompilerExePath);
		
		return strb.toString();
	}
	
	protected void replacePathVar(StringBuilder strb, String varName, IPath outputPath) {
		while(StringUtil.replace(strb, "$"+varName, encodeString(outputPath.toOSString())))
			;
		while(StringUtil.replace(strb, "$/"+varName, encodeString(outputPath.toString())))
			;
	}
	
	protected void replaceMultiPathVar(StringBuilder strb, String varName, String pathPrefix, List<IPath> paths) {
		StringBuilder varTextOSString = new StringBuilder();
		StringBuilder varTextPosix = new StringBuilder();
		for(IPath srcModule : paths) {
			varTextOSString.append(pathPrefix + encodeString(srcModule.toOSString()) + "\n");
			varTextPosix.append(pathPrefix + encodeString(srcModule.toString()) + "\n");
		}
		while(StringUtil.replace(strb, "$"+varName, varTextOSString.toString()))
			;
		while(StringUtil.replace(strb, "$/"+varName, varTextPosix.toString()))
			;
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
			IPath compilerFullPath = deeCompiler.getCompilerExecutablePath();
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
			if(builder.command().size() == 0) {
				fireHandleOutputLine("DDT: No build process specified.");
				return;
			}
			ExternalProcessLineNotifyHandler_Ext processUtil = new ExternalProcessLineNotifyHandler_Ext(builder, monitor) {
				protected void handleReadLine(String line) {
					synchronized(this) {
						// TODO: review concurrency usage of this method and fireHandleOutputLine
						buildLog.println("\t" + line);
						fireHandleOutputLine(line);
					}
				}

				@Override
				protected void handleStdOutLine(String line) {
					handleReadLine(line);
				}

				@Override
				protected void handleStdErrLine(String line) {
					handleReadLine(line);
				}
			};
			int exitValue = processUtil.awaitTermination();
			buildLog.println(">>  Exit value: " + exitValue);
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
