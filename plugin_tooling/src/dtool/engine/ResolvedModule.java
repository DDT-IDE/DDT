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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.HashMap;

import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.IElementSemantics;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult.ParsedModule;

public class ResolvedModule implements IModuleResolution {
	
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
	
	@Override
	public Path getModulePath() {
		return parsedModule.modulePath;
	}
	
	public AbstractBundleResolution getSemanticResolution() {
		return bundleRes;
	}
	
	@Override
	public IBundleResolution getBundleResolution() {
		return bundleRes;
	}
	
	/* FIXME: */
	@Deprecated
	public IModuleResolver getModuleResolver() {
		return bundleRes; 
	}
	
	protected final HashMap<ISemanticElement, IElementSemantics> semanticsMap = new HashMap<>();
	
	@Override
	public IElementSemantics getElementSemantics(ISemanticElement element) {
		return semanticsMap.get(element);
	}
	
	@Override
	public IElementSemantics putElementSemantics(ISemanticElement element, IElementSemantics elementSemantics) {
		assertTrue(semanticsMap.containsKey(element) == false);
		semanticsMap.put(element, elementSemantics);
		return elementSemantics;
	}
	
}