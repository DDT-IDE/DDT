package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import melnorme.utilbox.misc.StringUtil;
import dtool.DeeNamingRules;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.resolver.BaseResolverSourceTests.ITestsModuleResolver;
import dtool.tests.DToolBaseTest;

public final class TestsSimpleModuleResolver implements ITestsModuleResolver {
	
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
	
	@Override
	public void cleanupChanges() {
		extraModuleName = null;
		extraModuleResult = null;
	}
	
	public void initModules(File projectFolder, String namePrefix) {
		File[] children = projectFolder.listFiles();
		assertNotNull(children);
		for (File child : children) {
			String resourceName = child.getName();
			
			if(child.isDirectory()) {
				String packageName = resourceName;
				if(!DeeNamingRules.isValidPackageNamePart(packageName, true)) {
					continue;
				}
				initModules(child, namePrefix + packageName + ".");
			} else if(resourceName.endsWith(".d")) {
				String moduleName = DeeNamingRules.getModuleNameFromFileName(resourceName);
				if(!DeeNamingRules.isValidCompilationUnitName(resourceName)) {
					continue;
				}
				String source = DToolBaseTest.readStringFromFileUnchecked(child);
				DeeParserResult parseResult = DeeParser.parseSource(source, moduleName);
				modules.put(namePrefix + moduleName, parseResult);
			} else {
				assertFail();
			}
		}
	}
	
	@Override
	public String[] findModules(String fqNamePrefix) throws Exception {
		ArrayList<String> matchedModules = new ArrayList<>();
		Set<String> nameEntries = new HashSet<>(modules.keySet());
		if(extraModuleName != null) {
			nameEntries.add(extraModuleName);
		}
		
		for (String moduleName : nameEntries) {
			if(moduleName.startsWith(fqNamePrefix)) {
				matchedModules.add(moduleName);
			}
		}
		return matchedModules.toArray(new String[0]);
	}
	
	@Override
	public Module findModule(String[] packages, String module) throws Exception {
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