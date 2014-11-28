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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.ast.util.NodeUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ElementResolution;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import dtool.ast.definitions.Module;
import dtool.dub.BundlePath;
import dtool.engine.CommonSemanticsTest;
import dtool.engine.ResolvedModule;
import dtool.resolver.DefUnitResultsChecker;

public class CommonNodeSemanticsTest extends CommonSemanticsTest {
	
	protected static final String DEFAULT_ModuleName = "_tests";
	
	public static final BundlePath DEFAULT_TestsBundle = bundlePath(SEMANTICS_TEST_BUNDLES, "defaultBundle");
	public static final BundlePath TESTER_TestsBundle = bundlePath(SEMANTICS_TEST_BUNDLES, "tester");
	
	public static final Path DEFAULT_TestsModule = 
			DEFAULT_TestsBundle.resolve("source/" + DEFAULT_ModuleName + ".d");
	
	protected static ResolvedModule getDefaultTestsModule() throws ExecutionException {
		return defaultSemMgr.getUpdatedResolvedModule(DEFAULT_TestsModule);
	}
	
	protected static ISemanticContext getDefaultTestsModuleContext() throws ExecutionException {
		return getDefaultTestsModule().getSemanticContext();
	}
	
	protected static ResolvedModule getTesterModule(String sourcePath) {
		try {
			return defaultSemMgr.getUpdatedResolvedModule(
				TESTER_TestsBundle.path.resolve("source").resolve(sourcePath));
		} catch (ExecutionException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected static ResolvedModule parseModule(String source) throws ExecutionException {
		defaultSemMgr.getParseCache().setWorkingCopyAndGetParsedModule(DEFAULT_TestsModule, source);
		return getDefaultTestsModule();
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
	
	protected static <T> T findNode(ResolvedModule moduleRes, int offset, Class<T> klass) {
		ASTNode node = ASTNodeFinder.findElement(moduleRes.getModuleNode(), offset);
		return NodeUtil.getMatchingParent(node, klass);
	}
	
	protected static <E extends ISemanticElement> PickedElement<E> parseTestElement(String source, 
		String offsetSource, Class<E> klass) throws ExecutionException {
		ResolvedModule resolvedModule = parseModule(source);
		return pickElement(resolvedModule, offsetSource, klass);
	}
	
	public static <E extends ISemanticElement> PickedElement<E> pickElement(ResolvedModule resolvedModule,
			String offsetSource, Class<E> klass) {
		String source = resolvedModule.getParsedModule().source;
		int indexOf = source.indexOf(offsetSource);
		assertTrue(indexOf >= 0);
		return PickedElement.create(findNode(resolvedModule, indexOf, klass), resolvedModule.getSemanticContext());
	}
	
	/* ----------------- Helper to test caching ----------------- */
	
	protected void checkIsSameResolution(ElementResolution<?> resolutionA, ElementResolution<?> resolutionOther) {
		assertTrue(resolutionA == resolutionOther);
	}
	
	/* -----------------  ----------------- */
	
	public static DefUnitResultsChecker resultsChecker(CommonScopeLookup lookup) {
		return resultsChecker(lookup, true, true, true);
	}
	
	public static DefUnitResultsChecker resultsChecker(CommonScopeLookup lookup, boolean ignoreDummy,
			boolean ignorePrimitives, boolean ignoreObjectModule) {
		DefUnitResultsChecker checker = new DefUnitResultsChecker(lookup.getMatchedElements());
		checker.removeIgnoredDefUnits(ignoreDummy, ignorePrimitives);
		if(ignoreObjectModule) {
			checker.removeStdLibObjectDefUnits();
		}
		return checker;
	}
	
}