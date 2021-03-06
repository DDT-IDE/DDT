/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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
import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.IntrinsicProperty;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.IntrinsicProperty2;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefIdentifier;
import dtool.engine.ResolvedModule;
import dtool.engine.StandardLibraryResolution;

// TODO: remove this class, create specific tests for each remaining NamedElement.
public class NE__OthersTest extends NamedElement_CommonTest {
	
	/* ----------------- helpers to create elements ----------------- */
	
	protected static String func(String string) {
		return "void func() { " + string + " }";
	}
	
	protected static PickedElement<INamedElement> parseDefUnit(String source) {
		return parseDefUnit(source, "xxx");
	}
	
	public static PickedElement<INamedElement> parseDefUnit(String source, String markerString) {
		int offset = source.indexOf(markerString);
		ResolvedModule semanticModule = parseModule_(source);
		Module module = semanticModule.getModuleNode();
		ASTNode node = ASTNodeFinder.findElement(module, offset);
		DefSymbol name = assertCast(node, DefSymbol.class);
		return pickedElement(name.getDefUnit(), semanticModule.getSemanticContext());
	}
	
	public static PickedElement<INamedElement> pickedElement(INamedElement namedElement, ISemanticContext context) {
		return new PickedElement<>(namedElement, context);
	}
	
	/* ----------------- Sample elements ----------------- */
	
	/** Helper to visit a sample of named elements. */
	protected static class NamedElementVisitor {
		
		protected final void visit(PickedElement<INamedElement> pickedElement, String aliasTarget) {
			INamedElement namedElement = pickedElement.element;
			boolean isConcrete = aliasTarget == null;
			
			assertTrue((namedElement instanceof IConcreteNamedElement) == isConcrete);
			assertTrue(isConcrete || 
				namedElement.getName().equals("xxx") || namedElement.getName().equals(aliasTarget));
			
			doVisit(pickedElement, aliasTarget);
		}
		
		@SuppressWarnings("unused")
		protected void doVisit(PickedElement<INamedElement> pickedElement, String aliasTarget) {
		}
		
		
		protected final void visitConcrete(PickedElement<INamedElement> pickedElement) {
			visit(pickedElement, null);
		}
		
		protected final void visitAliasElement(PickedElement<INamedElement> pickedElement) {
			visitAliasElement(pickedElement, "target");
		}
		
		protected final void visitAliasElement(PickedElement<INamedElement> pickedElement, String aliasTarget) {
			visit(pickedElement, aliasTarget);
		}
		
		
		public void visitElements() throws Exception {
			
			visitConcrete(parseDefUnit("void func() {  if(int xxx) { }  }"));
			visitConcrete(parseDefUnit("void func(int xxx) {   }"));
			
			
			visitConcrete(parseDefUnit("static this() { }", "this"));
			
			
			visitConcrete(parseDefUnit(func(" try {} catch(Exception xxx) {} ")));
			visitConcrete(parseDefUnit(func(" foreach(a , xxx ;  [1, 2 3]);")));
			
			StandardLibraryResolution stdLib = getDefaultStdLibContext();
			
			visitConcrete(pickedElement(new IntrinsicProperty("max", D2_063_intrinsics.int_type, null), stdLib));
			visitConcrete(pickedElement(new IntrinsicProperty2("max", new RefIdentifier("blah"), null), stdLib));
			
			visitConcrete(parseDefUnit("mixin blah xxx;"));
			
			
			visitAliasElement(parseDefUnit("int target;  static if(is(target xxx)) { }"), "int target;");
			
		}
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void test_NamedElement________() throws Exception {
		new NamedElementVisitor() {
			@Override
			protected void doVisit(PickedElement<INamedElement> pickedElement, String aliasTarget) {
				/* TODO: test resolved type as well*/
				test_resolveConcreteElement(pickedElement, aliasTarget);
			}
		}.visitElements();
		
	}
	
}