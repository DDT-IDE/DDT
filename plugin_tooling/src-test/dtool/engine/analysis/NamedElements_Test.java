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

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.AbstractIntrinsicProperty;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.IntrinsicTypeDefUnit;
import melnorme.lang.tooling.engine.intrinsics.IntrinsicDefUnit;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefIdentifier;
import dtool.engine.ResolvedModule;
import dtool.engine.analysis.templates.AliasElement;
import dtool.engine.analysis.templates.VarElement;

public class NamedElements_Test extends CommonNodeSemanticsTest {
	
	/* ----------------- helpers to create elements ----------------- */
	
	protected static String func(String string) {
		return "void func() { " + string + " }";
	}
	
	protected static PickedElement<INamedElement> element(String source) {
		return getDefUnit(source, "xxx");
	}
	
	public static PickedElement<INamedElement> getDefUnit(String source, String markerString) {
		int offset = source.indexOf(markerString);
		ResolvedModule semanticModule;
		try {
			semanticModule = parseModule(source);
		} catch (ExecutionException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		Module module = semanticModule.getModuleNode();
		ASTNode node = ASTNodeFinder.findElement(module, offset);
		DefSymbol name = assertCast(node, DefSymbol.class);
		return new PickedElement<INamedElement>(name.getDefUnit(), semanticModule.getSemanticContext());
	}
	
	/* ----------------- Sample elements ----------------- */
	
	/** Helper to visit a sample of test elements. */
	protected static class NamedElementVisitor {
		
		@SuppressWarnings("unused")
		protected void visit(PickedElement<INamedElement> pickedElement) {
		}
		
		protected void visitConcrete(PickedElement<INamedElement> pickedElement) {
			visit(pickedElement);
		}
		
		protected void visitAliasElement(PickedElement<INamedElement> pickedElement) {
			visit(pickedElement);
		}

		
		public void visitElements() {
			
			visitConcrete(element("int xxx;"));
			visitConcrete(element("int z, xxx;"));
			visitConcrete(element("auto xxx = 2;"));
			visitConcrete(element("enum Enum { xxx = 1 }"));
			visitConcrete(element("enum xxx = 1;"));
			
			visitConcrete(element("struct xxx { }"));
			visitConcrete(element("union xxx { }"));
			visitConcrete(element("class xxx { }"));
			visitConcrete(element("interface xxx { }"));
			
			visitConcrete(element("void func() {  if(int xxx) { }  }"));
			visitConcrete(element("void func(int xxx) {   }"));
			
			
			visitConcrete(element("Enum xxx { }"));
			visitConcrete(element("module xxx;")); /*FIXME: BUG here, module name mismatch. */
			
			visitConcrete(getDefUnit("static this() { }", "this"));
			
			
			visitConcrete(element(func(" try {} catch(Exception xxx) {} ")));
			visitConcrete(element(func(" foreach(a , xxx ;  [1, 2 3]);")));

			/*FIXME: BUG here TODO */
//			intrinsicProperty, intrinsicTypeDefUnit,
			
			AbstractIntrinsicProperty intrinsicProperty = new AbstractIntrinsicProperty("blah", null) {
				
				@Override
				protected INamedElement resolveType(ISemanticContext mr) {
					return null;
				}
			};
			
			IntrinsicTypeDefUnit intrinsicTypeDefUnit = new IntrinsicTypeDefUnit("blah", null) {
				
				@Override
				public void createMembers(IntrinsicDefUnit... members) {
				}
			};
			
			
			visitConcrete(element("template xxx() { }"));
			visitConcrete(element("template blah(int xxx) { }"));
			/*FIXME: BUG here TODO */
//			templateInstance,
			
//			TemplateInstance templateInstance = new TemplateInstance((DefinitionTemplate) getDefUnit("template xxx(T){}"), 
//			new ArrayList2<INamedElementNode>(
//				new TypeAliasElement(new DefSymbol("blah"), parseSourceAndFindNode("int z;", 0, RefPrimitive.class))
//			)
//		);
			
			VarElement varInstance = new VarElement(new DefSymbol("blah"), new RefIdentifier("foo"));
			
//			varInstance,
			
			visitConcrete(element("mixin blah xxx;"));
			visitConcrete(element("template blah(xxx...) { }"));
				
			
			/* ----------------- aliases ----------------- */
			
			visitAliasElement(element("int target;  alias xxx = target;"));
			visitAliasElement(element("int target;  alias foo = blah, xxx = target;"));
			
			visitAliasElement(element("int target;  alias target xxx;"));
			visitAliasElement(element("int target;  alias target blah, xxx;"));
			
			/*FIXME: BUG here TODO*/
//				getDefUnit("import xxx = target;"),
			visitAliasElement(element("import blah : xxx = target;"));
			
			/*FIXME: BUG here TODO*/
			AliasElement aliasElement = sampleModule(new AliasElement(new DefSymbol("xxx"), new RefIdentifier("target")));
//				aliasElement,
			
			visitAliasElement(element("int target;  static if(is(target xxx)) { }"));
			
		}
		
	}
	
	protected static AliasElement sampleModule(AliasElement aliasElement) {
		Module module = parseSource("int target;");
		aliasElement.setParent(module);
		return aliasElement;
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void testSemantics() throws Exception { testSemantics$(); }
	public void testSemantics$() throws Exception {
		
		NamedElementVisitor namedElementVisitor = new NamedElementVisitor() {
			@Override
			protected void visitConcrete(PickedElement<INamedElement> pickedElement) {
				testResolveElementConcrete(pickedElement);
				restResolveElementConcrete_ForConcrete(pickedElement);
			}
			
			@Override
			protected void visitAliasElement(PickedElement<INamedElement> pickedElement) {
				testResolveElementConcrete(pickedElement);
				restResolveElementConcrete_Alias(pickedElement);
			}
			
		};
		namedElementVisitor.visitElements();
		
	}
	
	protected void testResolveElementConcrete(PickedElement<INamedElement> pickedElement) {
		ISemanticContext context = pickedElement.context;
		INamedElement namedElement = pickedElement.element;
		assertTrue(context == getSemanticResolution(namedElement));
		
		checkIsSameResolution(
			namedElement.getSemantics().resolveConcreteElement(context),
			namedElement.getSemantics().resolveConcreteElement(context)
		);
	}
	
	protected void restResolveElementConcrete_ForConcrete(PickedElement<INamedElement> pickedElement) {
		ISemanticContext context = pickedElement.context;
		INamedElement namedElement = pickedElement.element;
		
		// non-alias elements relsolve to themselves
		assertTrue(namedElement.resolveConcreteElement(context) == namedElement);
	}
	
	protected void restResolveElementConcrete_Alias(PickedElement<INamedElement> pickedElement) {
		ISemanticContext context = pickedElement.context;
		INamedElement namedElement = pickedElement.element;
		assertTrue(namedElement.getName().equals("xxx") || namedElement.getName().equals("target"));
		
		IConcreteNamedElement concreteElement = namedElement.resolveConcreteElement(context);
		
		assertTrue(concreteElement != null);
		assertTrue(concreteElement.getName().equals("target"));
	}
	
	/* ----------------- test caching ----------------- */
	
	protected void testNamedElementSemantics(ResolvedModule moduleRes) throws ModuleSourceException {
		ISemanticContext context = moduleRes.getSemanticContext();
		INamedElement namedElement = moduleRes.getModuleNode();
		INamedElementSemantics semantics = namedElement.getSemantics();
		assertTrue(semantics == namedElement.getSemantics());
		
		checkIsSameResolution(semantics.resolveConcreteElement(context), semantics.resolveConcreteElement(context));
	}
	
}