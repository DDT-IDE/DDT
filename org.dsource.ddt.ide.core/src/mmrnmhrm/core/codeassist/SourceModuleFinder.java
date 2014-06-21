package mmrnmhrm.core.codeassist;

import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DLTKModelUtils;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.definitions.Module;
import dtool.engine.modules.ModuleNamingRules;

public class SourceModuleFinder extends ImportPathVisitor {
	
	protected final String moduleName;
	protected final IPath packagesPath;
	
	protected ISourceModule foundSourceModule;
	
	public SourceModuleFinder(String moduleName, IPath packagesPath) {
		this.moduleName = moduleName;
		this.packagesPath = packagesPath;
	}
	
	public ISourceModule findImportTarget(IScriptProject deeProject) throws ModelException {
		iteratonFullImportPath(deeProject);
		return foundSourceModule;
	}
	
	@Override
	protected boolean visitSourceContainer(IProjectFragment srcFolder) {
		IScriptFolder scriptFolder = srcFolder.getScriptFolder(packagesPath);
		
		if(scriptFolder.exists()) {
			foundSourceModule = getExistingSourceModule(scriptFolder, moduleName);
			if(foundSourceModule != null) {
				return true;
			}
		}
		// search for package.d
		scriptFolder = srcFolder.getScriptFolder(packagesPath.append(moduleName));
		if(scriptFolder.exists()) {
			foundSourceModule = getExistingSourceModule(scriptFolder, "package");
			if(foundSourceModule != null) {
				return true;
			}
		}
		return false;
	}
	
	public static ISourceModule findModuleUnit(Module module, IScriptProject scriptProject) throws ModelException {
		String[] packages = module.getDeclaredPackages();
		String moduleName = module.getName();
		
		return findModuleUnit(scriptProject, packages, moduleName);
	}
	
	public static ISourceModule findModuleUnit(IScriptProject deeProject, String[] packages, String moduleName) 
			throws ModelException {
		IPath packagesPath = new Path(StringUtil.collToString(packages, "/"));
		
		return new SourceModuleFinder(moduleName, packagesPath).findImportTarget(deeProject);
	}

	protected static ISourceModule getExistingSourceModule(IScriptFolder scriptFolder, String moduleName) {
		for (String validExtension : ModuleNamingRules.VALID_EXTENSIONS) {
			ISourceModule sourceModule = scriptFolder.getSourceModule(moduleName + validExtension);
			if(DLTKModelUtils.exists(sourceModule)) {
				return sourceModule;
			}
		}
		return null;
	}
}