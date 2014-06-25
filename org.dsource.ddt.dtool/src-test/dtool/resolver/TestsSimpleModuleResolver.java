package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.Module;
import dtool.engine.modules.CommonModuleResolver;
import dtool.engine.modules.ModuleNamingRules;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.tests.CommonDToolTest;

public final class TestsSimpleModuleResolver extends CommonModuleResolver {
	
	protected File projectFolder;
	protected Map<String, DeeParserResult> modules = new HashMap<>();
	protected String extraModuleName;
	protected DeeParserResult extraModuleResult;
	
	public TestsSimpleModuleResolver(File projectFolder) {
		this.projectFolder = projectFolder;
		
		initModules(projectFolder, "");
	}
	
	public void setExtraModule(String extraModuleName, DeeParserResult extraModuleResult) {
		this.extraModuleName = extraModuleName;
		this.extraModuleResult = extraModuleResult;
	}
	
	public void initModules(File projectFolder, String packagePath) {
		File[] children = projectFolder.listFiles();
		assertNotNull(children);
		for (File child : children) {
			String resourceName = child.getName();
			
			if(child.isDirectory()) {
				String packageName = resourceName;
				if(!ModuleNamingRules.isValidPackageNameSegment(packageName)) {
					continue;
				}
				initModules(child, packagePath + packageName + "/");
			} else if(resourceName.endsWith(".d")) {
				
				String moduleFQName = ModuleNamingRules.getModuleFQNameFromFilePath(packagePath, resourceName);
				if(moduleFQName == null) 
					continue;
				
				String moduleName = StringUtil.substringAfterLastMatch(moduleFQName, ".");
				
				String source = CommonDToolTest.readStringFromFile_PreserveBOM(child);
				DeeParserResult parseResult = DeeParser.parseSource(source, moduleName);
				modules.put(moduleFQName, parseResult);
			} else {
				assertFail();
			}
		}
	}
	
	@Override
	public Set<String> findModules_do(String fqNamePrefix) {
		HashSet<String> matchedModules = new HashSet<>();
		Set<String> nameEntries = new HashSet<>(modules.keySet());
		if(extraModuleName != null) {
			nameEntries.add(extraModuleName);
		}
		
		for (String moduleName : nameEntries) {
			if(moduleName.startsWith(fqNamePrefix)) {
				matchedModules.add(moduleName);
			}
		}
		return matchedModules;
	}
	
	@Override
	public Module findModule_do(String[] packages, String module) throws Exception {
		String fullName = StringUtil.collToString(packages, ".");
		if(packages.length > 0) {
			fullName += ".";
		}
		fullName += module;
		return findModule(fullName);
	}
	
	public Module findModule(String fullName) {
		if(extraModuleName != null && fullName.equals(extraModuleName)) {
			return extraModuleResult.module;
		}
		DeeParserResult moduleEntry = modules.get(fullName);
		return moduleEntry == null ? null : moduleEntry.module;
	}
	
}