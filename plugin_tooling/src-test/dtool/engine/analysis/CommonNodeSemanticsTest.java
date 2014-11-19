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
package dtool.engine.analysis;


import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast.util.NodeUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.Module;
import dtool.dub.BundlePath;
import dtool.engine.AbstractBundleResolution;
import dtool.engine.BundleResolution;
import dtool.engine.CommonSemanticsTest;
import dtool.engine.ResolvedModule;

public class CommonNodeSemanticsTest extends CommonSemanticsTest {
	
	protected static final String DEFAULT_ModuleName = "_tests";
	
	public static final BundlePath DEFAULT_SemanticsTest_Bundle = 
			bundlePath(SEMANTICS_TEST_BUNDLES, "defaultBundle");
	
	protected static ResolvedModule parseModule(String source) throws ExecutionException {
		Path filepath = DEFAULT_SemanticsTest_Bundle.resolve("source/" + DEFAULT_ModuleName + ".d");
		defaultSemMgr.getParseCache().setWorkingCopyAndGetParsedModule(filepath, source);
		return defaultSemMgr.getUpdatedResolvedModule(filepath);
	}
	
	protected AbstractBundleResolution getSemanticResolution(INamedElement namedElement) {
		BundleResolution bundleSR = defaultSemMgr.getStoredResolution(DEFAULT_SemanticsTest_Bundle);
		ResolvedModule findResolvedModule;
		try {
			ModuleFullName moduleFullName = namedElement.getModuleFullName();
			findResolvedModule = bundleSR.findResolvedModule(moduleFullName);
		} catch (ModuleSourceException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		return findResolvedModule.getSemanticResolution();
	}
	
	protected static Module parseSource(String source) {
		try {
			return parseModule(source).getModuleNode();
		} catch (ExecutionException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected static ASTNode parseSourceAndPickNode(String source, int offset) {
		Module module = parseSource(source);
		return ASTNodeFinder.findElement(module, offset);
	}
	
	public static <T> T parseSourceAndFindNode(String source, int offset, Class<T> klass) {
		ASTNode node = parseSourceAndPickNode(source, offset);
		return NodeUtil.getMatchingParent(node, klass);
	}
	
}