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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.tooling.engine.scoping.IScopeProvider;
import melnorme.lang.tooling.engine.scoping.ScopeSemantics;
import melnorme.utilbox.misc.PathUtil;
import melnorme.utilbox.misc.PathUtil.InvalidPathExceptionX;

import org.junit.Test;

import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.engine.AbstractBundleResolution;
import dtool.engine.CommonSemanticManagerTest.Tests_SemanticManager;
import dtool.engine.DToolServer;
import dtool.engine.ResolvedModule;
import dtool.resolver.PrefixDefUnitSearch;


public class Template_SemanticsTest extends CommonNodeSemanticsTest {
	
	public static TestsElementSearch resolveAllMembers(ResolvedModule module, Reference tplRef) {
		AbstractBundleResolution sr = module.getSemanticResolution();
		TestsElementSearch search = allElementsSearch(module);
		IScopeProvider tplInstance = 
				assertCast(tplRef.resolveTargetElement(sr).getSingleResult(), IScopeProvider.class);
		tplInstance.resolveSearchInScope(search);
		return search;
	}
	
	protected static TestsElementSearch allElementsSearch(ResolvedModule module) {
		return new TestsElementSearch(module.getModuleNode(), -1, module.getSemanticResolution());
	}
	
	public static class TestsElementSearch extends PrefixDefUnitSearch {
		
		public TestsElementSearch(Module refOriginModule, int refOffset, IModuleResolver moduleResolver) {
			super(refOriginModule, refOffset, moduleResolver);
		}
		
		public ILangNamedElement findElement(String elementName) {
			ILangNamedElement foundMatch = null;
			
			for (ILangNamedElement match : getMatchedElements()) {
				if(match.getName().equals(elementName)) {
					assertTrue(foundMatch == null);
					foundMatch = match;
				}
			}
			return foundMatch;
		}
		
	}
	
	/* -----------------  ----------------- */

	protected Tests_SemanticManager sm = new Tests_SemanticManager(new DToolServer());
	
	protected ResolvedModule parseModule(String source) throws InvalidPathExceptionX, ExecutionException {
		Path path = PathUtil.createPath("##_test1");
		sm.getParseCache().setWorkingCopyAndGetParsedModule(path, source);
		return sm.getUpdatedResolvedModule(path);
	}
	
	protected Reference getSampleType(ResolvedModule rm, String elementName) throws ModuleSourceException {
		ILangNamedElement element = ScopeSemantics.findElement(rm.getModuleNode(), elementName);
		assertNotNull(element);
		return assertCast(element, DefinitionVariable.class).type;
	}
	
	protected static final String TPL_DEF_A = "template Tpl("
			+ "TYPE1"
			+ ") { int x; };";
	
	protected static final String TPL_DEF_B = "template Tpl("
			+ "TYPE1 : int = bar, "
			+ "TYPE2 = ambigB, "
			+ "int VAR1 : 10 = 1,"
			+ "alias ALIAS1 : 12 + 2 = foo,"
			+ "alias ALIAS2 : int = 2,"
			+ "TUPLE ..., "
			+ "this THIS"
			+ ") { int x; };";
	
	@Test
	public void testTemplateInstantiation() throws Exception { testTemplateInstantiation$(); }
	public void testTemplateInstantiation$() throws Exception {
		ResolvedModule module = parseModule(TPL_DEF_A + "Tpl!(int) ref1;");
		Reference tplRef = getSampleType(module, "ref1");
		TestsElementSearch search = resolveAllMembers(module, tplRef);
		
		ILangNamedElement tplArg = search.findElement("TYPE1");
//		assertTrue(resolveEffectiveType(tplArg).getName().equals("int"));
	}
	
	protected ILangNamedElement resolveEffectiveType(ILangNamedElement tplArg) {
		return tplArg; // TODO
	}
	
}