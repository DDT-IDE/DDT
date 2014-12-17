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

import java.io.IOException;

import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.engine.ResolvedModule;

public class NameLookup_ErrorsTest extends CommonNodeSemanticsTest {
	
	protected IConcreteNamedElement doResolveConcreteElement(String source, String marker) {
		PickedElement<INamedElement> pickedElement = parseElement(source, marker, INamedElement.class);
		ISemanticContext context = pickedElement.context;
		return pickedElement.element.resolveConcreteElement(context);
	}
	
	protected IConcreteNamedElement doResolveConcreteElementForRef(String source, String marker) {
		PickedElement<NamedReference> pickedElement = parseElement(source, marker, NamedReference.class);
		ISemanticContext context = pickedElement.context;
		return pickedElement.element.resolveTargetElement(context).resolveConcreteElement(context);
	}
	
	protected INamedElement doResolveNamedElementForRef(String source, String marker) {
		PickedElement<RefIdentifier> pickedElement = parseElement(source, marker, RefIdentifier.class);
		ISemanticContext context = pickedElement.context;
		return pickedElement.element.resolveTargetElement(context);
	}
	
	@Test
	public void testNotFound() throws Exception { testNotFound_____(); }
	public void testNotFound_____() throws Exception {
		
		checkResultNotFound(
			doResolveNamedElementForRef("int blah = xxx;", "xxx"));

		checkResultNotFound(
			doResolveNamedElementForRef("alias A = B; alias B = xxx;", "xxx"));
		
		checkResultNotFound(
			doResolveConcreteElementForRef("alias A = B; alias B = xxx; alias _ = A/**/;", "A/**/;"));
		
		checkResultNotFound(
			doResolveConcreteElementForRef("import not_found; alias _ = not_found/**/;", "not_found/**/;"));
		
		testModuleParseException();
	}
	
	protected void checkResultNotFound(INamedElement result) {
		assertTrue(result.getName().equals(ErrorElement.NOT_FOUND__Name));
		assertTrue(result.getNameInRegularNamespace() == null);
	}
	
	protected void testModuleParseException() {
		NamedReference element = parseElement("import not_found;", "not_found;", NamedReference.class).element;
		ISemanticContext context = new EmptySemanticResolution() {
			
			@Override
			protected ResolvedModule getBundleResolvedModule(ModuleFullName moduleFullName)
					throws ModuleSourceException {
				throw new ModuleSourceException(new IOException("FAKE_IO_ERROR"));
			}
		};
		
		IConcreteNamedElement resolvedElement = element.resolveTargetElement(context).resolveConcreteElement(context);
		assertTrue(resolvedElement.getName().equals("not_found"));
		assertTrue(resolvedElement.getNameInRegularNamespace() == null);
		assertTrue(resolvedElement instanceof ErrorElement);
	}
	
	
	/* -----------------  ----------------- */
	
	@Test
	public void testLoop() throws Exception { testLoop_____(); }
	public void testLoop_____() throws Exception {
		
		checkLoopResult(
			doResolveConcreteElementForRef("alias A= B; alias B = A/**/;", "A/**/"));
		
		checkLoopResult(
			doResolveConcreteElementForRef("alias A= B; alias B = C; alias C = A/**/;", "A/**/"));
		
		checkLoopResult(
			doResolveConcreteElement("alias A= B; alias B = C; alias C = A/**/;", "C = A"));
		
		checkResultNotFound(
			doResolveConcreteElementForRef("B A; A B; auto _ = A.xxx;", "xxx"));
		checkResultNotFound(
			doResolveConcreteElementForRef("alias A= B; alias B = A; auto _ = A.xxx;", "xxx"));
		
		
		checkResultNotFound(
			doResolveConcreteElementForRef("class A : A { }; auto _ = A.xxx;", "xxx"));
		checkResultNotFound(
			doResolveConcreteElementForRef("class A : B { }; class B : A { }; auto _ = A.xxx;", "xxx"));
		checkResultNotFound(
			doResolveConcreteElementForRef("class A : A; auto _ = A.xxx;", "xxx"));
	}
	
	protected void checkLoopResult(INamedElement result) {
		assertTrue(result.getName().equals(ErrorElement.LOOP_ERROR_ELEMENT__Name));
		assertTrue(result.getNameInRegularNamespace() == null);
	}
	
}