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
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ElementResolution;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.dub.BundlePath;
import dtool.engine.CommonSemanticsTest;
import dtool.engine.ResolvedModule;
import dtool.engine.StandardLibraryResolution;
import dtool.resolver.DefUnitResultsChecker;

public class CommonNodeSemanticsTest extends CommonSemanticsTest {
	
	protected static final String DEFAULT_ModuleName = "_tests";
	
	public static final BundlePath DEFAULT_TestsBundle = bundlePath(SEMANTICS_TEST_BUNDLES, "defaultBundle");
	public static final BundlePath TESTER_TestsBundle = bundlePath(SEMANTICS_TEST_BUNDLES, "tester");
	
	public static final Path DEFAULT_TestsModule = 
			loc(DEFAULT_TestsBundle, "source").resolve(DEFAULT_ModuleName + ".d").path;
	
	protected static ResolvedModule getDefaultTestsModule() throws ExecutionException {
		return defaultSemMgr.getUpdatedResolvedModule(DEFAULT_TestsModule, DEFAULT_TestsCompilerInstall);
	}
	
	protected static ISemanticContext getDefaultTestsModuleContext() throws ExecutionException {
		return getDefaultTestsModule().getSemanticContext();
	}
	
	protected static ResolvedModule getTesterModule(String sourcePath) {
		try {
			return defaultSemMgr.getUpdatedResolvedModule(
				TESTER_TestsBundle.getPath().resolve("source").resolve(sourcePath), DEFAULT_TestsCompilerInstall);
		} catch (ExecutionException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected static ResolvedModule parseModule(String source) throws ExecutionException {
		// make sure we reparse, even if source is the same. 
		defaultSemMgr.getParseCache().discardEntry(DEFAULT_TestsModule);
		
		defaultSemMgr.getParseCache().setWorkingCopyAndGetParsedModule(DEFAULT_TestsModule, source);
		ResolvedModule result = getDefaultTestsModule();
		assertTrue(result.getSource().equals(source));
		return result;
	}
	
	protected static ResolvedModule parseModule_(String source) {
		try {
			return parseModule(source);
		} catch (ExecutionException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
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
	
	public static <T extends ILanguageElement> T parseSourceAndFindNode(String source, int offset, Class<T> klass) {
		ASTNode node = parseSourceAndPickNode(source, offset);
		return NodeElementUtil.getMatchingParent(node, klass);
	}
	
	protected static <T extends ILanguageElement> T findNode(ResolvedModule moduleRes, int offset, Class<T> klass) {
		ASTNode node = ASTNodeFinder.findElement(moduleRes.getModuleNode(), offset);
		return NodeElementUtil.getMatchingParent(node, klass);
	}
	
	/* -----------------  ----------------- */
	
	protected static <E extends ILanguageElement> PickedElement<E> parseElement(String source, 
		String offsetSource, Class<E> klass) {
		ResolvedModule resolvedModule = parseModule_(source);
		return pickElement(resolvedModule, offsetSource, klass);
	}
	
	protected static <E extends ILanguageElement> PickedElement<E> parseElement(String source, 
		int offset, Class<E> klass) {
		ResolvedModule resolvedModule = parseModule_(source);
		return pickElement(resolvedModule, offset, klass);
	}
	
	
	public static <E extends ILanguageElement> PickedElement<E> pickElement(ResolvedModule resolvedModule,
			String offsetSource, Class<E> klass) {
		String source = resolvedModule.getParsedModule().source;
		int indexOf = source.indexOf(offsetSource);
		assertTrue(indexOf >= 0);
		return pickElement(resolvedModule, indexOf, klass);
	}
	
	public static <E extends ILanguageElement> PickedElement<E> pickElement(ResolvedModule resolvedModule,
			int index, Class<E> klass) {
		E node = findNode(resolvedModule, index, klass);
		ISemanticContext context = resolvedModule.getSemanticContext();
		return picked(node, context);
	}
	
	/* ----------------- ----------------- */
	
	public static <E extends ILanguageElement> PickedElement<E> picked(E node, ISemanticContext context) {
		return new PickedElement<>(node, context);
	}
	
	public static <E extends ILanguageElement> PickedElement<E> pickedNative(E node) {
		return new PickedElement<>(node, getDefaultStdLibContext());
	}
	
	protected static StandardLibraryResolution getDefaultStdLibContext() {
		return defaultSemMgr.getUpdatedStdLibResolution(DEFAULT_TestsCompilerInstall);
	}
	
	/* ----------------- more complex pickers ----------------- */
	
	public static PickedElement<INamedElement> parseSourceAndPickFromRefResolving(String source) {
		return parseSourceAndPickFromRefResolving(source, "/*M*/");
	}
	
	public static PickedElement<INamedElement> parseSourceAndPickFromRefResolving(String source, String refMarker) {
		ResolvedModule resolvedModule = parseModule_(source);
		Reference ref = findNode(resolvedModule, resolvedModule.getSource().indexOf(refMarker), Reference.class);
		
		ISemanticContext context = resolvedModule.getSemanticContext();
		INamedElement derivedElement = ref.resolveTargetElement(context);
		return new PickedElement<>(derivedElement, context);
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