package dtool.model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.MiscUtil;
import dtool.ast.definitions.Module;
import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubBundle.BundleFile;
import dtool.project.DeeNamingRules;
import dtool.project.IModuleResolver;

public class SemanticContext implements IModuleResolver {
	
	protected final SemanticManager manager;
	protected final DubBundleDescription bundleDesc;
	protected final Map<ModuleFullName, Path> bundleModules; //immutable

	public SemanticContext(SemanticManager manager, DubBundleDescription bundleDesc) {
		this.manager = manager;
		this.bundleDesc = bundleDesc;
		DubBundle mainBundle = bundleDesc.getMainBundle();
		assertNotNull(mainBundle.getBundleName());
		
		this.bundleModules = Collections.unmodifiableMap(calculateBundleModules(mainBundle));
	}
	
	public String getBundleId() {
		return bundleDesc.getMainBundle().getBundleName();
	}
	
	public Map<ModuleFullName, Path> getBundleModuleFiles() {
		return bundleModules;
	}
	
	/* ----------------- ----------------- */
	
	protected HashMap<ModuleFullName, Path> calculateBundleModules(DubBundle mainBundle) {
		HashMap<ModuleFullName, Path> hashMap = new HashMap<>();
		
		for (BundleFile bundleFiles : mainBundle.bundleFiles) {
			Path filePath = MiscUtil.createValidPath(bundleFiles.filePath);
			if(filePath == null) {
				logError("Invalid filesystem path: " + bundleFiles.filePath);
				continue; // ignore
			}
			
			Path[] importFolders = mainBundle.getEffectiveImportPathFolders();
			for (Path importFolder : importFolders) {
				if(filePath.startsWith(importFolder)) {
					Path relPath = importFolder.relativize(filePath);
					if(relPath.getNameCount() == 0) {
						logError("File has same path as import folder: " + filePath);
						continue;
					}
					
					ModuleFullName moduleFullName = DeeNamingRules.getModuleFullName(relPath);
					if(!moduleFullName.isValid()) {
						logWarning("Invalid path for a D module: " + relPath);
						continue;
					}
					hashMap.put(moduleFullName, filePath);
					
					// continue looking, the same file can be present in multiple import paths, if nested
					// it's not an elegant scenario, but it's probably ok to support.
				}
			}
		}
		return hashMap;
	}
	
	protected void logError(String message) {
		manager.dtoolServer.logError(message, null);
	}
	
	protected void logWarning(String message) {
		manager.dtoolServer.logMessage(message);
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public Module findModule(String[] packages, String module) throws Exception {
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, module));
		Path filePath = bundleModules.get(moduleFullName);
		if(filePath == null) {
			return null;
		}
		return manager.parseCache.getParsedModule(filePath).getModuleNode();
	}
	
	@Override
	public String[] findModules(String fullNamePrefix) {
		Set<ModuleFullName> moduleEntries = bundleModules.keySet();
		ArrayList<String> matchedModules = new ArrayList<>();
		
		for (ModuleFullName moduleEntry : moduleEntries) {
			String moduleFullName = moduleEntry.getModuleFullName();
			if(moduleFullName.startsWith(fullNamePrefix)) {
				matchedModules.add(moduleFullName);
			}
		}
		return matchedModules.toArray(new String[0]);
	}
	
}