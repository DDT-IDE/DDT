/*******************************************************************************
 * Copyright (c) 2012, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import melnorme.lang.tooling.context.AbstractSemanticContext;
import melnorme.lang.tooling.context.BundleModules;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.misc.Location;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.tests.CommonDToolTest;

public final class TestsSimpleModuleResolver extends AbstractSemanticContext {
	
	protected Map<ModuleFullName, ParsedModule> parsedModules = new HashMap<>();
	
	protected ModuleFullName extraModuleName;
	protected ParsedModule extraModuleResult;
	
	public TestsSimpleModuleResolver(Location projectFolder) {
		super(createBundles(projectFolder));
		
		
		for (Entry<ModuleFullName, Location> entry : bundleModules.getModules().entrySet()) {
			ModuleFullName moduleFullName = entry.getKey();
			
			String source = CommonDToolTest.readStringFromFile_PreserveBOM(entry.getValue().toFile());
			ParsedModule parsedModule = DeeParser.parseSource(source, moduleFullName.getFullNameAsString());
			
			parsedModules.put(entry.getKey(), parsedModule);
		}
	}
	
	protected static BundleModules createBundles(Location sourceFolder) {
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
	public IConcreteNamedElement findModule(ModuleFullName moduleName) throws ModuleSourceException {
		if(extraModuleName != null && extraModuleName.equals(moduleName)) {
			return extraModuleResult.module;
		}
		
		ParsedModule parsedModule = parsedModules.get(moduleName);
		return parsedModule == null ? null : parsedModule.module;
	}
	
	@Override
	public ISemanticContext getContainingBundleResolution(boolean isStdLib, Location modulePath) {
		return this;
	}
	
}