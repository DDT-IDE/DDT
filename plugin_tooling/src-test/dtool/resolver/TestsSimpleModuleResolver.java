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

import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.utilbox.collections.ArrayList2;
import dtool.engine.AbstractSemanticContext;
import dtool.engine.BundleModules;
import dtool.engine.ResolvedModule;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.tests.CommonDToolTest;

public final class TestsSimpleModuleResolver extends AbstractSemanticContext {
	
	protected Map<ModuleFullName, ResolvedModule> parsedModules = new HashMap<>();
	
	protected ModuleFullName extraModuleName;
	protected ParsedModule extraModuleResult;
	
	public TestsSimpleModuleResolver(File projectFolder) {
		super(createBundles(projectFolder.toPath()));
		
		
		for (Entry<ModuleFullName, Path> entry : bundleModules.getModules().entrySet()) {
			ModuleFullName moduleFullName = entry.getKey();
			
			String source = CommonDToolTest.readStringFromFile_PreserveBOM(entry.getValue().toFile());
			ParsedModule parsedModule = DeeParser.parseSource(source, moduleFullName.getFullNameAsString());
			
			ResolvedModule resolvedModule = new ResolvedModule(parsedModule, this);
			parsedModules.put(entry.getKey(), resolvedModule);
		}
	}
	
	protected static BundleModules createBundles(Path sourceFolder) {
		BundleModules bundleModules = new BundleModulesVisitor(new ArrayList2<>(sourceFolder)) {
			@Override
			protected FileVisitResult handleFileVisitException(Path file, IOException exc) {
				throw assertFail();
			}
		}.toBundleModules();
		return bundleModules;
	}
	
	public void setExtraModule(String extraModuleName, ParsedModule extraModuleResult) {
		this.extraModuleName = ModuleFullName.fromString(extraModuleName);
		this.extraModuleResult = extraModuleResult;
	}
	
	@Override
	protected void findBundleModules(String fullNamePrefix, HashSet<String> matchedModules) {
		if(extraModuleName != null && extraModuleName.getFullNameAsString().startsWith(fullNamePrefix)) {
			matchedModules.add(extraModuleName.getFullNameAsString());
		}
		
		super.findBundleModules(fullNamePrefix, matchedModules);
	}
	
	@Override
	public ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ModuleSourceException {
		if(extraModuleName != null && extraModuleName.equals(moduleFullName)) {
			return new ResolvedModule(extraModuleResult, this);
		}
		
		return parsedModules.get(moduleFullName);
	}
	
	@Override
	public ResolvedModule findResolvedModule(Path path) throws ModuleSourceException {
		throw assertFail(); // Not used.
	}
	
}