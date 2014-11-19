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
import melnorme.lang.tooling.bundles.ISemanticResolution;
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
import dtool.ast.definitions.Module;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefPrimitive;
import dtool.engine.AbstractBundleResolution;
import dtool.engine.analysis.templates.AliasElement;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.engine.analysis.templates.TypeAliasElement;
import dtool.engine.analysis.templates.VarElement;

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
				new TypeAliasElement(new DefSymbol("blah"), parseSourceAndFindNode("int z;", 0, RefPrimitive.class))
			)
		);
		
		VarElement varInstance = new VarElement(new DefSymbol("blah"), new RefIdentifier("foo"));
		
		INamedElement[] elements = array(
			getDefUnit("int xxx;"),
			getDefUnit("int z, xxx;"),
			getDefUnit("auto xxx = 2;"),
			getDefUnit("enum Enum { xxx = 1 }"),
			getDefUnit("enum xxx = 1;"),
			
			getDefUnit("struct xxx { }"),
			getDefUnit("union xxx { }"),
			getDefUnit("class xxx { }"),
			getDefUnit("interface xxx { }"),
			
			getDefUnit("void func() {  if(int xxx) { }  }"),
			getDefUnit("void func(int xxx) {   }"),
			
			
			getDefUnit("Enum xxx { }"),
			getDefUnit("module xxx;"), /*FIXME: BUG here, module name mismatch. */
			
			getDefUnit("static this() { }", "this"),
			
			
			getDefUnit(func(" try {} catch(Exception xxx) {} ")),
			getDefUnit(func(" foreach(a , xxx ;  [1, 2 3]);")),

			/*FIXME: BUG here TODO */
//			intrinsicProperty, intrinsicTypeDefUnit,
			
			getDefUnit("template xxx() { }"),
			getDefUnit("template blah(int xxx) { }"),
			/*FIXME: BUG here TODO */
//			templateInstance,
//			varInstance,
			
			getDefUnit("mixin blah xxx;"),
			getDefUnit("template blah(xxx...) { }")
			
		);
		
		return elements;
	}
	
	protected static String func(String string) {
		return "void func() { " + string + " }";
	}
	
	protected static DefUnit getDefUnit(String source) {
		return getDefUnit(source, "xxx");
	}
	
	public static DefUnit getDefUnit(String source, String markerString) {
		int offset = source.indexOf(markerString);
		ASTNode node = parseSourceAndPickNode(source, offset);
		DefSymbol name = assertCast(node, DefSymbol.class);
		return name.getDefUnit();
	}
	
	@Test
	public void test_resolveConcreteElement() throws Exception { test_resolveConcreteElement$(); }
	public void test_resolveConcreteElement$() throws Exception {
		
		for (INamedElement namedElement : getConcreteNamedElements_AllSample()) {
			restResolveElementConcrete(namedElement);
		}
		
		AliasElement aliasElement = sampleModule(new AliasElement(new DefSymbol("xxx"), new RefIdentifier("target")));
		
		DefUnit[] aliases_Elements = array(
			getDefUnit("int target;  alias xxx = target;"),
			getDefUnit("int target;  alias foo = blah, xxx = target;"),
			
			getDefUnit("int target;  alias target xxx;"),
			getDefUnit("int target;  alias target blah, xxx;"),
			
			/*FIXME: BUG here TODO*/
//			getDefUnit("import xxx = target;"),
			getDefUnit("import blah : xxx = target;"),
			
			/*FIXME: BUG here TODO*/
//			aliasElement,
			
			getDefUnit("int target;  static if(is(target xxx)) { }")
		);
		for (INamedElement namedElement : aliases_Elements) {
			testAlias(namedElement);
		}
	}
	
	protected AliasElement sampleModule(AliasElement aliasElement) {
		Module module = parseSource("int target;");
		aliasElement.setParent(module);
		return aliasElement;
	}
	
	protected void restResolveElementConcrete(INamedElement namedElement) {
		ISemanticResolution sr = getSemanticResolution(namedElement);
		assertTrue(namedElement.resolveConcreteElement(sr) == namedElement);
	}
	
	protected void testAlias(INamedElement namedElement) {
		assertTrue(namedElement.getName().equals("xxx") || namedElement.getName().equals("target"));
		
		AbstractBundleResolution sr = getSemanticResolution(namedElement);
		IConcreteNamedElement concreteElement = namedElement.resolveConcreteElement(sr);
		
		assertTrue(concreteElement != null);
		assertTrue(concreteElement.getName().equals("target"));
	}
	
}