package mmrnmhrm.core.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.misc.StringUtil;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.dsource.ddt.ide.core.model.DeeModelUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

import dtool.DeeNamingRules;
import dtool.ast.definitions.Module;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class DLTKModuleResolver implements IModuleResolver {
	
	public static final DLTKModuleResolver instance = new DLTKModuleResolver();
	
	/** Finds the module with the given package and module name.
	 * refModule is used to determine which project/build-path to search. */
	@Override
	public Module findModule(Module sourceRefModule, String[] packages, String modName) throws CoreException {
		
		//ScriptModelUtil.findType(module, qualifiedName, delimeter)
		
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
					if(exists(modUnit)) { 
						DeeModuleDeclaration modDecl = DeeModelUtil.getParsedDeeModule(modUnit);
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
	
	private static boolean exists(ISourceModule modUnit) {
		return modUnit != null && modUnit.exists()
		// XXX: DLTK bug workaround: 
		// modUnit.exists() true on ANY external source modules of libraries
		// we should make a test case for this
			&& externalReallyExists(modUnit)
		;
	}
	
	private static boolean externalReallyExists(ISourceModule modUnit) {
		if(!(modUnit instanceof IExternalSourceModule))
			return true;
		//modUnit.getUnderlyingResource() of externals is allways null
		IPath localPath = EnvironmentPathUtils.getLocalPath(modUnit.getPath());
		return new File(localPath.toOSString()).exists();
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
