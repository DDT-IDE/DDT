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
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.AbstractIntrinsicProperty;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.IntrinsicTypeDefUnit;
import melnorme.lang.tooling.engine.intrinsics.IntrinsicDefUnit;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;

import org.junit.Test;

import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.references.RefPrimitive;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.engine.analysis.templates.TypeAliasElement;

public class NamedElements_Test extends CommonNodeSemanticsTest {
	
	public static INamedElement[] getConcreteNamedElements_AllSample() {
		AbstractIntrinsicProperty intrinsicProperty = new AbstractIntrinsicProperty("blah", null) {
			
			@Override
			protected INamedElement resolveType(IModuleResolver mr) {
				return null;
			}
		};
		
		IntrinsicTypeDefUnit intrinsicTypeDefUnit = new IntrinsicTypeDefUnit("blah", null) {
			
			@Override
			public void createMembers(IntrinsicDefUnit... members) {
			}
		};
		
		TemplateInstance templateInstance = new TemplateInstance((DefinitionTemplate) getDefUnit("template xxx(T){}"), 
			new ArrayList2<INamedElementNode>(
				new TypeAliasElement(new DefSymbol("blah"), parseSourceAndPickNode("int z;", 0, RefPrimitive.class))
			)
		);
		
		INamedElement[] elements = array(
			getDefUnit("int xxx;"),
			getDefUnit("int z, xxx;"),
			getDefUnit("auto xxx = 2;"),
			getDefUnit("enum Enum { xxx = 1 }"),
			getDefUnit("enum xxx = 1;"),
			getDefUnit("void func() {  if(int xxx) { }  }"),
			
			getDefUnit("struct xxx { }"),
			getDefUnit("union xxx { }"),
			getDefUnit("class xxx { }"),
			getDefUnit("interface xxx { }"),

			getDefUnit("Enum xxx { }"),
			getDefUnit("module xxx;"),
			
			getDefUnit("static this() { }", 7),
			
			intrinsicProperty, intrinsicTypeDefUnit,
			
			getDefUnit("template xxx() { }"),
			templateInstance,
			
			getDefUnit("mixin blah xxx;"),
			getDefUnit("template blah(xxx...) { }"),
			
			getDefUnit("int xxx()")
			
		);
		
		
		
		return elements;
	}
	
	protected static DefUnit getDefUnit(String source) {
		int offset = source.indexOf("xxx");
		return getDefUnit(source, offset);
	}
	
	protected static DefUnit getDefUnit(String source, int offset) {
		ASTNode node = parseSourceAndPickNode(source, offset);
		DefSymbol name = assertCast(node, DefSymbol.class);
		return name.getDefUnit();
	}
	
	@Test
	public void test_resolveConcreteElement() throws Exception { test_resolveConcreteElement$(); }
	public void test_resolveConcreteElement$() throws Exception {
		testResolveConcretElement(getConcreteNamedElements_AllSample());
		
		testAliases(array(
			getDefUnit("alias xxx = target;"),
			getDefUnit("alias foo = blah, xxx = target;"),
			
			getDefUnit("alias target xxx;"),
			getDefUnit("alias target blah, xxx;")
		));
		
	}
	
	protected void testResolveConcretElement(INamedElement[] elements) {
		for (INamedElement namedElement : elements) {
			restResolveElementConcrete(namedElement);
		}
	}
	
	protected void restResolveElementConcrete(INamedElement namedElement) {
		assertTrue(namedElement.resolveConcreteElement() == namedElement);
	}
	
	protected void testAliases(INamedElement[] elements) {
		for (INamedElement namedElement : elements) {
			assertTrue(namedElement.getName().equals("xxx"));
			IConcreteNamedElement concreteElement = namedElement.resolveConcreteElement();
			assertTrue(concreteElement != null && concreteElement.getName().equals("target"));
		}		
	}
	
}