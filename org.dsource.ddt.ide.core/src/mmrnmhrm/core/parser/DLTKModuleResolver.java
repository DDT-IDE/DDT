package mmrnmhrm.core.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DLTKModelUtils;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.dsource.ddt.ide.core.model.DeeModuleParsingUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.DeeNamingRules;
import dtool.ast.definitions.Module;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class DLTKModuleResolver implements IModuleResolver {
	
	public static final DLTKModuleResolver instance = new DLTKModuleResolver();
	
	/** Finds the module with the given package and module name.
	 * refModule is used to determine which project/build-path to search. */
	@Override
	public Module findModule(Module sourceRefModule, String[] packages, String modName) throws CoreException {
		
		ISourceModule sourceModule = (ISourceModule) sourceRefModule.getModuleUnit();
		IScriptProject deeproj = sourceModule.getScriptProject();
		
		if(deeproj == null || deeproj.exists() == false || !isDeeProject(deeproj))
			return null;
		
		String fullPackageName = StringUtil.collToString(packages, "/");  
		
		for (IProjectFragment srcFolder : deeproj.getProjectFragments()) {
			
			IScriptFolder pkgFrag = srcFolder.getScriptFolder(fullPackageName);
			if(pkgFrag != null && pkgFrag.exists()) {
				for (int i = 0; i < DeeNamingRules.VALID_EXTENSIONS.length; i++) {
					String fileext = DeeNamingRules.VALID_EXTENSIONS[i];
					ISourceModule modUnit = pkgFrag.getSourceModule(modName+fileext);
					if(DLTKModelUtils.exists(modUnit)) { 
						DeeModuleDeclaration modDecl = DeeModuleParsingUtil.getParsedDeeModule(modUnit);
						return modDecl.neoModule;
					}
				}
			}
		}
		return null;
	}
	
	protected static boolean isDeeProject(IScriptProject deeproj) {
		return deeproj.getLanguageToolkit().getNatureId().equals(DeeLanguageToolkit.NATURE_ID);
	}
	
	@Override
	public String[] findModules(Module refSourceModule, String fqNamePrefix) throws ModelException {
		ISourceModule sourceModule = (ISourceModule) refSourceModule.getModuleUnit();
		IScriptProject scriptProject = sourceModule.getScriptProject();
		
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
