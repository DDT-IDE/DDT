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

import static dtool.engine.analysis.DeeLanguageIntrinsics.D2_063_intrinsics;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.IntrinsicProperty;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.IntrinsicProperty2;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.declarations.ModuleProxy;
import dtool.ast.declarations.PackageNamespace;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.Reference;
import dtool.engine.ResolvedModule;
import dtool.engine.StandardLibraryResolution;
import dtool.engine.analysis.templates.AliasElement;
import dtool.engine.analysis.templates.InstantiatedDefUnit;
import dtool.engine.analysis.templates.VarElement;

public class NamedElements_Test extends CommonNodeSemanticsTest {
	
	/* ----------------- helpers to create elements ----------------- */
	
	protected static String func(String string) {
		return "void func() { " + string + " }";
	}
	
	protected static PickedElement<INamedElement> parseDefUnit(String source) {
		return parseDefUnit(source, "xxx");
	}
	
	public static PickedElement<INamedElement> parseDefUnit(String source, String markerString) {
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
		return pickedElement(name.getDefUnit(), semanticModule.getSemanticContext());
	}
	
	public static PickedElement<INamedElement> pickedElement(INamedElement namedElement, ISemanticContext context) {
		return new PickedElement<>(namedElement, context);
	}
	
	public static PickedElement<INamedElement> syntheticElement(InstantiatedDefUnit namedElement, 
		ResolvedModule module) {
		namedElement.setParent(module.getModuleNode());
		namedElement.setParsedStatus();
		assertTrue(namedElement.isParsedStatus());
		return new PickedElement<>((INamedElement) namedElement, module.getSemanticContext());
	}
	
	/* ----------------- Sample elements ----------------- */
	
	/** Helper to visit a sample of test elements. */
	protected static class NamedElementVisitor {
		
		protected final void visit(PickedElement<INamedElement> pickedElement, boolean isConcrete) {
			assertTrue((pickedElement.element instanceof IConcreteNamedElement) == isConcrete);
			doVisit(pickedElement, isConcrete);
		}
		
		protected void visitConcrete(PickedElement<INamedElement> pickedElement) {
			visit(pickedElement, true);
		}
		
		protected void visitAliasElement(PickedElement<INamedElement> pickedElement) {
			visit(pickedElement, false);
		}
		
		
		@SuppressWarnings("unused")
		protected void doVisit(PickedElement<INamedElement> pickedElement, boolean isConcrete) {
		}
		
		public void visitElements() throws Exception {
			
			StandardLibraryResolution stdLib = defaultSemMgr.getUpdatedStdLibResolution(null);
			
			visitConcrete(parseDefUnit("int xxx;"));
			visitConcrete(parseDefUnit("int z, xxx;"));
			visitConcrete(parseDefUnit("auto xxx = 2;"));
			visitConcrete(parseDefUnit("enum Enum { xxx = 1 }"));
			visitConcrete(parseDefUnit("enum xxx = 1;"));
			
			visitConcrete(parseDefUnit("struct xxx { }"));
			visitConcrete(parseDefUnit("union xxx { }"));
			visitConcrete(parseDefUnit("class xxx { }"));
			visitConcrete(parseDefUnit("interface xxx { }"));
			
			visitConcrete(parseDefUnit("void func() {  if(int xxx) { }  }"));
			visitConcrete(parseDefUnit("void func(int xxx) {   }"));
			
			
			visitConcrete(parseDefUnit("Enum xxx { }"));
			visitConcrete(parseDefUnit("module xxx;")); /*FIXME: BUG here, module name mismatch. */
			
			visitConcrete(parseDefUnit("static this() { }", "this"));
			
			
			visitConcrete(parseDefUnit(func(" try {} catch(Exception xxx) {} ")));
			visitConcrete(parseDefUnit(func(" foreach(a , xxx ;  [1, 2 3]);")));

			visitConcrete(pickedElement(D2_063_intrinsics.bool_type, stdLib));
			visitConcrete(pickedElement(D2_063_intrinsics.object_type, stdLib));
			
			visitConcrete(pickedElement(new IntrinsicProperty("max", D2_063_intrinsics.int_type, null), stdLib));
			visitConcrete(pickedElement(new IntrinsicProperty2("max", new RefIdentifier("blah"), null), stdLib));
			
			
			visitConcrete(parseDefUnit("template xxx() { }"));
			visitConcrete(parseDefUnit("template blah(int xxx) { }"));
			
			visitConcrete(parseDefUnit("mixin blah xxx;"));
			visitConcrete(parseDefUnit("template blah(xxx...) { }"));
			
			// A few derived elements.
			
			/* FIXME: BUG here add these tests: */
			//visitModuleProxy();
			//visitPackageNamespace();
			
			// TODO test template synthetic elements:
			
			VarElement varInstance = new VarElement(new DefSymbol("blah"), new RefIdentifier("foo"));
//			visitConcrete(syntheticElement(varInstance, getDefaultTestsModule()));
			
//			varInstance,
//			templateInstance,
			
//			TemplateInstance templateInstance = new TemplateInstance((DefinitionTemplate) getDefUnit("template xxx(T){}"), 
//			new ArrayList2<INamedElementNode>(
//				new TypeAliasElement(new DefSymbol("blah"), parseSourceAndFindNode("int z;", 0, RefPrimitive.class))
//			)
//		);
			
			visitAliases();
		}
		
		protected void visitModuleProxy() throws ExecutionException {
			ResolvedModule resolvedModule = parseModule("import xxx; auto _ = xxx;");
			Reference ref = findNode(resolvedModule, resolvedModule.getSource().indexOf("xxx"), Reference.class);
			INamedElement moduleProxy = ref.resolveTargetElement(resolvedModule.getSemanticContext());
			assertTrue(moduleProxy instanceof ModuleProxy);
			
			visitConcrete(new PickedElement<>(moduleProxy, resolvedModule.getSemanticContext()));
		}
		
		protected void visitPackageNamespace() throws ExecutionException {
			ResolvedModule resolvedModule = parseModule("import xxx.foo; auto _ = xxx;");
			Reference ref = findNode(resolvedModule, resolvedModule.getSource().indexOf("xxx"), Reference.class);
			INamedElement derivedElement = ref.resolveTargetElement(resolvedModule.getSemanticContext());
			assertTrue(derivedElement instanceof PackageNamespace);
			
			visitConcrete(new PickedElement<>(derivedElement, resolvedModule.getSemanticContext()));
		}
		
		protected void visitAliases() {
			/* ----------------- aliases ----------------- */
			
			visitAliasElement(parseDefUnit("int target;  alias xxx = target;"));
			visitAliasElement(parseDefUnit("int target;  alias foo = blah, xxx = target;"));
			
			visitAliasElement(parseDefUnit("int target;  alias target xxx;"));
			visitAliasElement(parseDefUnit("int target;  alias target blah, xxx;"));
			
			visitAliasElement(parseDefUnit("import xxx = target;"));
			visitAliasElement(parseDefUnit("import blah : xxx = target;"));
			
			/* TODO test this*/
			AliasElement aliasElement = sampleModule(new AliasElement(new DefSymbol("xxx"), new RefIdentifier("target")));
//				aliasElement,
			
			visitAliasElement(parseDefUnit("int target;  static if(is(target xxx)) { }"));
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
		assertTrue(context == context.findSemanticContext(namedElement));
		
		checkIsSameResolution(
			namedElement.getSemantics(context).resolveConcreteElement(),
			namedElement.getSemantics(context).resolveConcreteElement()
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
		INamedElementSemantics semantics = namedElement.getSemantics(context);
		assertTrue(semantics == namedElement.getSemantics(context));
		
		checkIsSameResolution(semantics.resolveConcreteElement(), semantics.resolveConcreteElement());
	}
	
	@Test
	public void testModuleSyntheticUnits() throws Exception { testModuleSyntheticUnits$(); }
	public void testModuleSyntheticUnits$() throws Exception {
		
		testModuleSyntheticUnit____("module foo;", "foo");
		testModuleSyntheticUnit____("", "_tests");
		
		testModuleSyntheticUnit____("module pack.foo;", "pack.foo");
		testModuleSyntheticUnit____("module pack.subpack.foo;", "pack.subpack.foo");
	}
	
	protected void testModuleSyntheticUnit____(String preSource, String elemName) throws ExecutionException {
		ResolvedModule resolvedModule = parseModule(
			preSource + "; int _dummy = " + elemName + "/*A*/ ~ " + elemName + "/*B*/;");
		PickedElement<NamedReference> pickA = pickElement(resolvedModule, elemName + "/*A*/", NamedReference.class);
		PickedElement<NamedReference> pickB = pickElement(resolvedModule, elemName + "/*B*/", NamedReference.class);
		
		ResolvableResult resultA = Resolvables_SemanticsTest.testResolveElement(pickA);
		ResolvableResult resultB = Resolvables_SemanticsTest.testResolveElement(pickB);
		
		assertTrue(pickA.element != pickB.element);
		assertTrue(resultA.result == resultB.result);
	}
	
}