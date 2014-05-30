package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.areEqualArrays;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DLTKModelUtils;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.parser.ModuleParsingHandler;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.definitions.Module;
import dtool.project.CommonModuleResolver;
import dtool.project.DeeNamingRules;

public class DeeProjectModuleResolver extends CommonModuleResolver{
	
	protected final IScriptProject scriptProject;
	
	public DeeProjectModuleResolver(IScriptProject scriptProject) {
		assertNotNull(scriptProject);
		this.scriptProject = scriptProject;
	}
	
	/** Shortcut method */
	public ISourceModule findModuleUnit(Module module) throws ModelException {
		return findModuleUnit(module, null);
	}
	
	// TODO: review this method
	public ISourceModule findModuleUnit(Module module, ISourceModule workingCopySourceModule) throws ModelException {
		String[] packages = module.getDeclaredPackages();
		String moduleName = module.getName();
		
		return findModuleUnit(packages, moduleName, workingCopySourceModule);
	}
	
	// TODO: review this method
	public ISourceModule findModuleUnit(String[] packages, String moduleName, ISourceModule workingCopySourceModule)
		throws ModelException {
		if(workingCopySourceModule != null) {
			Module wcModule = ModuleParsingHandler.parseModule(workingCopySourceModule).module;
			
			String wcModuleName = wcModule.getName();
			String[] wcPackages = wcModule.getDeclaredPackages();
			
			if(wcModuleName.equals(moduleName) && areEqualArrays(wcPackages, packages)) {
				return workingCopySourceModule;
			}
		}
		return findModuleUnit(scriptProject, packages, moduleName);
	}
	
	@Override
	public Module findModule_do(String[] packages, String moduleName) throws CoreException {
		return findModule(packages, moduleName, this.scriptProject);
	}
	
	protected Module findModule(String[] packages, String modName, IScriptProject deeproj) 
		throws ModelException {
		ISourceModule moduleUnit = findModuleUnit(deeproj, packages, modName);
		if(moduleUnit == null)
			return null;
		
		return ModuleParsingHandler.parseModule(moduleUnit).module;
	}
	
	public ISourceModule findModuleUnit(IScriptProject deeProject, String[] packages, String moduleName) 
			throws ModelException {
		IPath packagesPath = new Path(StringUtil.collToString(packages, "/"));
		
		return new ModuleImportTargetFinder(moduleName, packagesPath).findImportTarget(deeProject);
	}
	
	protected static class ImportPathVisitor {
		
		protected static boolean isAccessible(IScriptProject scriptProject) {
			return DeeNature.isAcessible(scriptProject.getProject(), false);
		}
		
		protected void iteratonFullImportPath(IScriptProject deeProject) throws ModelException {
			if(!isAccessible(deeProject))
				return;
			
			for (IProjectFragment srcFolder : deeProject.getProjectFragments()) {
				boolean stop = visitSourceContainer(srcFolder);
				if(stop) {
					return;
				}
			}
			
			IBuildpathEntry[] resolvedBuildpath = deeProject.getResolvedBuildpath(true);
			for (IBuildpathEntry bpEntry : resolvedBuildpath) {
				if(bpEntry.getEntryKind() == IBuildpathEntry.BPE_PROJECT) {
					IPath path = bpEntry.getPath();
					if(path.segmentCount() != 1) {
						DeeCore.logError("Invalid path in project BP entry: " + path);
						continue;
					}
					String projectName = path.segment(0);
					IScriptProject depProject = getDeeScriptProject(projectName);
					if(depProject == null) {
						continue;
					}
					IProjectFragment[] projectFragments = depProject.getProjectFragments();
					for (IProjectFragment projectFragment : projectFragments) {
						if(projectFragment.isExternal()) {
							continue;
						}
						
						boolean stop = visitSourceContainer(projectFragment);
						if(stop) {
							return;
						}
					}
				}
			}
		}
		
		@SuppressWarnings("unused")
		protected boolean visitSourceContainer(IProjectFragment srcContainer) throws ModelException {
			return false;
		}
		
		protected IScriptProject getDeeScriptProject(String projectName) {
			IProject project = EclipseUtils.getWorkspaceRoot().getProject(projectName);
			try {
				if(DeeNature.isAcessible(project)) {
					return DLTKCore.create(project);
				}
			} catch (CoreException e) {
			}
			return null;
		}
		
	}
	
	protected static class ModuleImportTargetFinder extends ImportPathVisitor {
		
		protected final String moduleName;
		protected final IPath packagesPath;
		
		protected ISourceModule foundSourceModule;
		
		public ModuleImportTargetFinder(String moduleName, IPath packagesPath) {
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
		
		protected static ISourceModule getExistingSourceModule(IScriptFolder scriptFolder, String moduleName) {
			for (String validExtension : DeeNamingRules.VALID_EXTENSIONS) {
				ISourceModule sourceModule = scriptFolder.getSourceModule(moduleName + validExtension);
				if(DLTKModelUtils.exists(sourceModule)) {
					return sourceModule;
				}
			}
			return null;
		}
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
				
				String packagePath= pkgFrag.getElementName();
				
				if(!DeeNamingRules.isValidPackagePathName(packagePath))
					continue;
				
				for (IModelElement srcUnitElem : pkgFrag.getChildren()) {
					ISourceModule cu = (ISourceModule) srcUnitElem;
					String cuFileName = cu.getElementName();
					
					String fqName = DeeNamingRules.getModuleFQNameFromFilePath(packagePath, cuFileName);
					
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