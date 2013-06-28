package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.areEqualArrays;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DLTKModelUtils;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.dsource.ddt.ide.core.model.DeeModuleParsingUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.DeeNamingRules;
import dtool.ast.definitions.Module;
import dtool.resolver.api.IModuleResolver;

public class DeeProjectModuleResolver implements IModuleResolver {
	
	protected final IScriptProject scriptProject;
	
	public DeeProjectModuleResolver(IScriptProject scriptProject) {
		assertNotNull(scriptProject);
		this.scriptProject = scriptProject;
	}
	
	public DeeProjectModuleResolver(ISourceModule sourceModule) {
		this(sourceModule.getScriptProject());
	}
	
	/** Shortcut method */
	public ISourceModule findModuleUnit(Module module) throws ModelException {
		return findModuleUnit(module, null);
	}
	
	public ISourceModule findModuleUnit(Module module, ISourceModule workingCopySourceModule) throws ModelException {
		String[] packages = module.getDeclaredPackages();
		String moduleName = module.getName();
		
		if(workingCopySourceModule != null) {
			Module wcModule = DeeModuleParsingUtil.getParsedDeeModule(workingCopySourceModule);
			
			String wcModuleName = wcModule.getName();
			String[] wcPackages = wcModule.getDeclaredPackages();
			
			if(wcModuleName.equals(moduleName) && areEqualArrays(wcPackages, packages)) {
				return workingCopySourceModule;
			}
		}
		return findModuleUnit(scriptProject, packages, moduleName);
	}
	
	@Override
	public Module findModule(String[] packages, String moduleName) throws CoreException {
		// possible BUG here
		return findModule(packages, moduleName, this.scriptProject);
	}
	
	protected Module findModule(String[] packages, String modName, IScriptProject deeproj) throws ModelException {
		ISourceModule moduleUnit = findModuleUnit(deeproj, packages, modName);
		if(moduleUnit == null)
			return null;
		
		Module module = DeeModuleParsingUtil.getParsedDeeModule(moduleUnit);
		return module;
	}
	
	public static ISourceModule findModuleUnit(IScriptProject deeProject, String[] packages, String moduleName) 
			throws ModelException {
		if(deeProject.exists() == false || !isDeeProject(deeProject))
			return null;
		
		IPath packagesPath = new Path(StringUtil.collToString(packages, "/"));
		
		for (IProjectFragment srcFolder : deeProject.getProjectFragments()) {
			IScriptFolder scriptFolder = srcFolder.getScriptFolder(packagesPath);
			
			if(scriptFolder.exists()) {
				for (String validExtension : DeeNamingRules.VALID_EXTENSIONS) {
					ISourceModule sourceModule = scriptFolder.getSourceModule(moduleName + validExtension);
					if(DLTKModelUtils.exists(sourceModule)) {
						return sourceModule;
					}
				}
			}
		}
		return null;
	}
	
	protected static boolean isDeeProject(IScriptProject scriptProject) {
		return scriptProject.getLanguageToolkit().getNatureId().equals(DeeLanguageToolkit.NATURE_ID);
	}
	
	@Override
	public String[] findModules(String fqNamePrefix) throws ModelException {
		return findModules(fqNamePrefix, this.scriptProject);
	}
	
	protected String[] findModules(String fqNamePrefix, IScriptProject scriptProject) throws ModelException {
		List<String> strings = new ArrayList<String>();
		
		for (IProjectFragment srcFolder : scriptProject.getProjectFragments()) {
			
			for (IModelElement pkgFragElem : srcFolder.getChildren()) {
				IScriptFolder pkgFrag = (IScriptFolder) pkgFragElem;
				
				String pkgName = pkgFrag.getElementName();
				
				if(!DeeNamingRules.isValidPackagePathName(pkgName))
					continue;
				
				pkgName = pkgName.replace("/", ".");
				
				for (IModelElement srcUnitElem : pkgFrag.getChildren()) {
					ISourceModule srcUnit = (ISourceModule) srcUnitElem;
					String modName = srcUnit.getElementName();
					// remove extension
					modName = modName.substring(0, modName.indexOf('.'));
					String fqName;
					if(pkgName.equals("")) {
						fqName = modName;
					} else {
						fqName = pkgName + "." + modName;
					}
					
					if(fqName.startsWith(fqNamePrefix)) {
						strings.add(fqName);
					}
				}
			}
		}
		
		return strings.toArray(new String[strings.size()]);
	}
	
}