package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.engine_client.DToolClient;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.definitions.Module;
import dtool.engine.modules.CommonModuleResolver;
import dtool.engine.modules.ModuleNamingRules;

@Deprecated
public class DeeProjectModuleResolver extends CommonModuleResolver {
	
	public final IScriptProject scriptProject;
	
	public DeeProjectModuleResolver(IScriptProject scriptProject) {
		assertNotNull(scriptProject);
		this.scriptProject = scriptProject;
	}

	@Override
	public Module findModule_do(String[] packages, String moduleName) throws CoreException {
		return findModule(packages, moduleName, this.scriptProject);
	}
	
	protected Module findModule(String[] packages, String modName, IScriptProject deeproj) 
		throws ModelException {
		ISourceModule moduleUnit = SourceModuleFinder.findModuleUnit(deeproj, packages, modName);
		if(moduleUnit == null)
			return null;
		
		return DToolClient.getDefault().getModuleNodeOrNull(moduleUnit);
	}
	
	@Override
	public String[] findModules_do(String fqNamePrefix) throws ModelException {
		return new ImportsModuleFinder(fqNamePrefix).findModules(scriptProject);
	}
	
	protected static class ImportsModuleFinder extends ImportPathVisitor {
		
		protected final String fqNamePrefix;
		protected List<String> matchedModulesFQName = new ArrayList<String>();
		
		public ImportsModuleFinder(String fqNamePrefix) {
			this.fqNamePrefix = fqNamePrefix;
		}
		
		protected String[] findModules(IScriptProject scriptProject) throws ModelException {
			iteratonFullImportPath(scriptProject);
			return ArrayUtil.createFrom(matchedModulesFQName, String.class);
		}
		
		@Override
		protected boolean visitSourceContainer(IProjectFragment srcContainer) throws ModelException {
			for (IModelElement pkgFragElem : srcContainer.getChildren()) {
				IScriptFolder pkgFrag = (IScriptFolder) pkgFragElem;
				
				String packagePath = pkgFrag.getElementName();
				
				if(!ModuleNamingRules.isValidPackagesPath(packagePath))
					continue;
				
				for (IModelElement srcUnitElem : pkgFrag.getChildren()) {
					ISourceModule cu = (ISourceModule) srcUnitElem;
					String cuFileName = cu.getElementName();
					
					String fqName = ModuleNamingRules.getModuleFQNameFromFilePath(packagePath, cuFileName);
					
					if(fqName == null)
						continue;
					
					if(fqName.startsWith(fqNamePrefix)) {
						matchedModulesFQName.add(fqName);
					}
				}
			}
			return false;
		}
		
	}
	
}