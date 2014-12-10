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
import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.ScopeSemantics;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.Reference;
import dtool.engine.ResolvedModule;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.engine.analysis.templates.VarElement;


public class Template_SemanticsTest extends CommonNodeSemanticsTest {
	
	public static CompletionScopeLookup resolveAllMembers(ResolvedModule module, Reference tplRef) {
		ISemanticContext context = module.getSemanticContext();
		CompletionScopeLookup search = allElementsSearch(module);
		INamedElement tplInstance_ = tplRef.getSemantics(context).resolveTargetElement().getSingleResult();
		TemplateInstance tplInstance = assertCast(tplInstance_, TemplateInstance.class);
		search.evaluateInMembersScope(tplInstance);
		return search;
	}
	
	protected static CompletionScopeLookup allElementsSearch(ResolvedModule module) {
		return new CompletionScopeLookup(module.getModuleNode(), -1, module.getSemanticContext());
	}
	
	public static INamedElement findElement(String elementName, CommonScopeLookup search) {
		INamedElement foundMatch = null;
		
		for (INamedElement match : search.getMatchedElements()) {
			if(match.getName().equals(elementName)) {
				assertTrue(foundMatch == null);
				foundMatch = match;
			}
		}
		return foundMatch;
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
		
		
		if(true)
			return; // TODO: finish these tests
		
		CommonScopeLookup search = resolveAllMembers(module, tplRef);
		
		INamedElement tplArg = findElement("TYPE1", search);
		assertTrue(resolveEffectiveType(tplArg).getName().equals("int"));
		
		
		// TODO test template instantiated elements using NamedElements_Test
		
		VarElement varInstance = new VarElement(new DefSymbol("blah"), new RefIdentifier("foo"));
//		visitConcrete(syntheticElement(varInstance, getDefaultTestsModule()));
		
//		varInstance,
//		templateInstance,
		
//		TemplateInstance templateInstance = new TemplateInstance((DefinitionTemplate) getDefUnit("template xxx(T){}"), 
//		new ArrayList2<INamedElementNode>(
//			new TypeAliasElement(new DefSymbol("blah"), parseSourceAndFindNode("int z;", 0, RefPrimitive.class))
//		)
//	);

//		AliasElement aliasElement = sampleModule(
//			new AliasElement(new DefSymbol("xxx"), new RefIdentifier("target")));
//			aliasElement,
		

	}
	
	protected INamedElement resolveEffectiveType(INamedElement tplArg) {
		EmptySemanticResolution sr = new EmptySemanticResolution();
		return tplArg.resolveConcreteElement(sr);
	}
	
}