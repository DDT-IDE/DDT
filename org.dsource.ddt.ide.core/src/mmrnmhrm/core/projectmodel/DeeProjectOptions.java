package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.build.DeeBuildOptions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;
import org.ini4j.Ini;
import org.ini4j.InvalidIniFormatException;

import dtool.Logg;


public class DeeProjectOptions {
	
	public static final String CFG_FILE_NAME = ".dprojectoptions";
	
	private static final String CFG_FILE_SECTION = "compileoptions";
	
	public final IScriptProject dltkProj;
	protected DeeBuildOptions compilerOptions;
	
	protected DeeProjectOptions(IScriptProject dltkProj) {
		this(dltkProj, new DeeBuildOptions(dltkProj.getElementName()));
	}
	
	protected DeeProjectOptions(IScriptProject dltkProj, DeeBuildOptions compilerOptions) {
		this.dltkProj = dltkProj;
		this.compilerOptions = compilerOptions;
	}
	
	public IProject getProject() {
		return dltkProj.getProject();
	}
	
	public IFolder getOutputFolder() {
		return getProject().getFolder(compilerOptions.outputDir); 
	}
	
	@Override
	public DeeProjectOptions clone() {
		DeeProjectOptions options = new DeeProjectOptions(dltkProj, compilerOptions.clone());
		return options;
	}
	
	public DeeBuildOptions getCompilerOptions() {
		return compilerOptions;
	}
	
	public void changeProjectOptions(DeeBuildOptions deeBuildOptions) throws CoreException {
		compilerOptions = deeBuildOptions;
		saveProjectConfigFile();
	}
	
	public void saveProjectConfigFile() throws CoreException {
		Ini ini = new Ini();
		Ini.Section section = ini.add(CFG_FILE_SECTION);
		
		section.put("buildtype", compilerOptions.buildType.toString());
		section.put("out", getOutputFolder().getProjectRelativePath().toString());
		section.put("outname", compilerOptions.artifactName);
		section.put("buildtool", compilerOptions.buildToolCmdLine);
		section.put("extraOptions", compilerOptions.buildCommands);
		
		writeConfigFile(ini);
	}
	
	/** Loads a project config if one exists. If one doesn't 
	 * exist already, create one with defaults settings. */
	public void loadNewProjectConfig() throws CoreException {
		IFile projCfgFile = getProject().getFile(CFG_FILE_NAME);
		if(projCfgFile.exists()) {
			try {
				loadProjectConfigFile();
			} catch (FileNotFoundException e) {
				saveProjectConfigFile();
			} catch (IOException e) {
				throw DeeCore.createCoreException("Error loading project file.", e);
			}
		} else {
			saveProjectConfigFile();
		}
	}
	
	public void loadProjectConfigFile() throws CoreException, IOException {
		Ini ini = readConfigFile();
		
		Ini.Section section = ini.get(CFG_FILE_SECTION);
		if(section == null) {
			// Proceed as if it did exist
			section = ini.add(CFG_FILE_SECTION);
		}
		
		String pathstr = section.get("out");
		if(pathstr != null) {
			compilerOptions.outputDir = Path.fromPortableString(pathstr);
		}
		
		String outname = section.get("outname");
		if(outname != null) {
			compilerOptions.artifactName = outname;
		}
		
		String buildtool = section.get("buildtool");
		if(buildtool != null) {
			compilerOptions.buildToolCmdLine = buildtool;
		}
		
		String extraOptions = section.get("extraOptions");
		if(extraOptions != null) {
			compilerOptions.buildCommands = extraOptions.replace("\r\n", "\n");
		}
	}
	
	
	private Ini readConfigFile() throws CoreException, IOException {
		IFile projCfgFile = getProject().getFile(CFG_FILE_NAME);
		Logg.main.println(projCfgFile.getLocationURI());
		
		Ini ini = new Ini();
		
		try {
			if(projCfgFile.exists()) {
				ini.load(projCfgFile.getContents());
			}
		} catch (InvalidIniFormatException e) {
			throw DeeCore.createCoreException("Error loading project file.", e); 
		} 
		return ini;
	}
	
	
	private void writeConfigFile(Ini ini) throws CoreException {
		StringWriter writer = new StringWriter();
		try {
			ini.store(writer);
		} catch (IOException e) {
			assertFail("IOE never happens on a StringWriter");
		}
		
		IFile projCfgFile = getProject().getFile(CFG_FILE_NAME);
		byte[] buf = writer.getBuffer().toString().getBytes();
		InputStream is = new ByteArrayInputStream(buf);
		if(projCfgFile.exists() == false) {
			projCfgFile.create(is, false, null);
		} else {
			projCfgFile.setContents(is, IResource.NONE, null);
		}
	}
	
	public String getArtifactName() {
		return compilerOptions.artifactName;
	}
	
	public String getArtifactNameNoExt() {
		int ix = compilerOptions.artifactName.lastIndexOf('.');
		if(ix != -1)
			return compilerOptions.artifactName.substring(0, ix);
		return compilerOptions.artifactName;
	}
	
	public String getArtifactRelPath() {
		String name = compilerOptions.artifactName;
		IPath output = compilerOptions.outputDir.append(name);
		return output.toString();
	}
	
	public String getBuildCommands() {
		return compilerOptions.buildCommands;
	}
	
	public String getBuildFile() {
		return "build.rf";
	}
	
	public String[] getBuilderFullCommandLine() {
		List<String> cmdLine = new ArrayList<String>();
		String buildToolCmd = compilerOptions.buildToolCmdLine;
		int lastNonWSpos = 0;
		int i = 0;
		while (i < buildToolCmd.length()) {
			char ch = buildToolCmd.charAt(i);
			if(ch == ' ') {
				if(lastNonWSpos != 0) {
					String cmdstr = buildToolCmd.substring(lastNonWSpos, i);
					cmdLine.add(cmdstr);
				}
				// Eat whitespace
				while(ch == ' ' && i < buildToolCmd.length()) {
					ch = buildToolCmd.charAt(++i);
				}
				lastNonWSpos = i;
				continue;
			}
			++i;
		}
		if(lastNonWSpos < i) {
			cmdLine.add(buildToolCmd.substring(lastNonWSpos, i));
		}
		
		return cmdLine.toArray(new String[0]);
	}
	
}
