package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import melnorme.lang.tooling.bundles.EmptySemanticResolution;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.Module;
import dtool.engine.BundleModules;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.tests.CommonDToolTest;

public final class TestsSimpleModuleResolver extends EmptySemanticResolution {
	
	protected File projectFolder;
	protected Map<ModuleFullName, DeeParserResult> modules = new HashMap<>();
	protected String extraModuleName;
	protected DeeParserResult extraModuleResult;
	
	public TestsSimpleModuleResolver(File projectFolder) {
		this.projectFolder = projectFolder;
		
		BundleModules bundleModules = new BundleModulesVisitor(new ArrayList2<>(projectFolder.toPath())) {
			@Override
			protected FileVisitResult handleFileVisitException(Path file, IOException exc) {
				throw assertFail();
			}
		}.toBundleModules();
		
		for (Entry<ModuleFullName, Path> entry : bundleModules.getModules().entrySet()) {
			ModuleFullName moduleName = entry.getKey();
			String source = CommonDToolTest.readStringFromFile_PreserveBOM(entry.getValue().toFile());
			DeeParserResult parseResult = DeeParser.parseSource(source, moduleName.getFullNameAsString());
			
			modules.put(moduleName, parseResult);
		}
	}
	
	public void setExtraModule(String extraModuleName, DeeParserResult extraModuleResult) {
		this.extraModuleName = extraModuleName;
		this.extraModuleResult = extraModuleResult;
	}
	
	@Override
	public Set<String> findModules_do(String fqNamePrefix) {
		HashSet<String> matchedModules = new HashSet<>();
		Set<ModuleFullName> nameEntries = new HashSet<>(modules.keySet());
		if(extraModuleName != null) {
			nameEntries.add(ModuleFullName.fromString(extraModuleName));
		}
		
		for (ModuleFullName moduleName : nameEntries) {
			String moduleNameString = moduleName.getFullNameAsString();
			if(moduleNameString.startsWith(fqNamePrefix)) {
				matchedModules.add(moduleNameString);
			}
		}
		return matchedModules;
	}
	
	@Override
	public Module findModule_do(String[] packages, String module) {
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
		DeeParserResult moduleEntry = modules.get(ModuleFullName.fromString(fullName));
		return moduleEntry == null ? null : moduleEntry.module;
	}
	
}