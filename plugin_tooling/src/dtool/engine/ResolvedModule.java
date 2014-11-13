/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine;

import java.nio.file.Path;

import melnorme.lang.tooling.bundles.ICompilationUnitData;
import melnorme.lang.tooling.bundles.IModuleResolver;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult.ParsedModule;

public class ResolvedModule implements ICompilationUnitData {
	
	protected final ParsedModule parsedModule;
	protected final AbstractBundleResolution bundleRes;
	
	public ResolvedModule(ParsedModule parsedModule, AbstractBundleResolution bundleRes) {
		this.parsedModule = parsedModule;
		this.bundleRes = bundleRes;
	}
	
	public ParsedModule getParsedModule() {
		return parsedModule;
	}
	
	public Module getModuleNode() {
		return parsedModule.module;
	}
	
	public Path getModulePath() {
		return parsedModule.modulePath;
	}
	
	public AbstractBundleResolution getSemanticResolution() {
		return bundleRes;
	}
	
	public IModuleResolver getModuleResolver() {
		return bundleRes;
	}
	
}