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
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.bundles.MockSemanticResolution;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.tooling.engine.scoping.ScopeSemantics;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.engine.ResolvedModule;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.resolver.PrefixDefUnitSearch;


public class Template_SemanticsTest extends CommonNodeSemanticsTest {
	
	public static TestsElementSearch resolveAllMembers(ResolvedModule module, Reference tplRef) {
		ISemanticContext context = module.getSemanticContext();
		TestsElementSearch search = allElementsSearch(module);
		INamedElement tplInstance_ = tplRef.resolveTargetElement(context).getSingleResult();
		TemplateInstance tplInstance = assertCast(tplInstance_, TemplateInstance.class);
		tplInstance.resolveSearchInMembersScope(search);
		return search;
	}
	
	protected static TestsElementSearch allElementsSearch(ResolvedModule module) {
		return new TestsElementSearch(module.getModuleNode(), -1, module.getSemanticContext());
	}
	
	public static class TestsElementSearch extends PrefixDefUnitSearch {
		
		public TestsElementSearch(Module refOriginModule, int refOffset, ISemanticContext moduleResolver) {
			super(refOriginModule, refOffset, moduleResolver);
		}
		
		public INamedElement findElement(String elementName) {
			INamedElement foundMatch = null;
			
			for (INamedElement match : getMatchedElements()) {
				if(match.getName().equals(elementName)) {
					assertTrue(foundMatch == null);
					foundMatch = match;
				}
			}
			return foundMatch;
		}
		
	}
	
	/* -----------------  ----------------- */
	
	protected Reference getSampleType(ResolvedModule rm, String elementName) throws ModuleSourceException {
		INamedElement element = ScopeSemantics.findElement(rm.getModuleNode(), elementName);
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
		
		INamedElement tplArg = search.findElement("TYPE1");
		//assertTrue(resolveEffectiveType(tplArg).getName().equals("int"));
	}
	
	protected INamedElement resolveEffectiveType(INamedElement tplArg) {
		MockSemanticResolution sr = new MockSemanticResolution();
		return tplArg.resolveConcreteElement(sr);
	}
	
}