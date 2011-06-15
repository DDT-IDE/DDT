/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.dsource.ddt.ide.core.model.DeeModelElementUtil;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

import dtool.ast.definitions.EArcheType;

public class DeeModelElement_Test extends BaseDeeTest implements ITestResourcesConstants {
	
	public static ISourceModule getSourceModule(String srcFolder, String cuPath) {
		ISourceModule sourceModule = SampleMainProject.getSourceModule(srcFolder, cuPath);
		assertTrue(sourceModule.exists());
		return sourceModule;
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		ISourceModule srcModule = getSourceModule(TR_CA, "sampledefs.d");
		
		IType topLevelElement = srcModule.getType("sampledefs");
		checkElementExists(srcModule, EArcheType.Module, 
				topLevelElement, "module sampledefs;");
		
		// TODO: test the other elements
		checkElementExists(srcModule, EArcheType.Alias, 
			topLevelElement.getType("Alias"), "alias TargetFoo Alias;");
		checkElementExists(srcModule, EArcheType.Class, 
			topLevelElement.getType("Class"), "class Class  {");
		checkElementExists(srcModule, EArcheType.Enum, 
			topLevelElement.getType("Enum"), "enum Enum {");
		checkElementExists(srcModule, EArcheType.Interface, 
			topLevelElement.getType("Interface"), "interface Interface { }");
		checkElementExists(srcModule, EArcheType.Struct, 
			topLevelElement.getType("Struct"), "struct Struct { }");
		checkElementExists(srcModule, EArcheType.Typedef, 
			topLevelElement.getType("Typedef"), "typedef TargetBar Typedef;");
		checkElementExists(srcModule, EArcheType.Union, 
			topLevelElement.getType("Union"), "union Union { }");
		checkElementExists(srcModule, EArcheType.Variable, 
			topLevelElement.getField("variable"), "int variable;");
		checkElementExists(srcModule, EArcheType.Template, 
				topLevelElement.getType("Template"), "template Template(");
		
		
		checkElementExists(srcModule, EArcheType.Variable, 
				topLevelElement.getType("Class").getField("fieldA"), "int fieldA;");
		checkElementExists(srcModule, EArcheType.Function, 
			topLevelElement.getType("Class").getMethod("methodB"), "void methodB() { }");
		
		checkElementExists(srcModule, EArcheType.Class, 
			topLevelElement.getType("Template").getType("TplNestedClass"), "class TplNestedClass  {");
		
		
		checkElementExists(srcModule, EArcheType.Function, 
			topLevelElement.getType("Template").getType("TplNestedClass").getMethod("func"), 
			"void func(asdf.qwer parameter) {");
		
	}
	protected void checkElementExists(ISourceModule sourceModule, EArcheType archeType, IMember element, 
			String code) throws ModelException {
		String source = sourceModule.getSource();
		
		assertTrue(element.exists());
		assertTrue(element.getCorrespondingResource() == null);
		assertTrue(element.getOpenable() == sourceModule);
		assertTrue(element.getSource().startsWith(code));
		assertTrue(element.getNameRange().getOffset() == source.indexOf(" " + element.getElementName()) + 1);
		assertTrue(element.getNameRange().getLength() == element.getElementName().length());
		
		assertTrue(DeeModelElementUtil.elementFlagsToArcheType(element, element.getFlags()) == archeType);
	}
	
	@Test
	public void testImplicitModuleName() throws Exception { testImplicitModuleName$(); }
	public void testImplicitModuleName$() throws Exception {
		ISourceModule srcModule = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclImplicitName.d");
		assertEquals(srcModule.getElementName(), "moduleDeclImplicitName.d");
		
		assertTrue(srcModule.getType("moduleDeclImplicitName").exists() == false);
		assertTrue(getChild(srcModule, "moduleDeclImplicitName").size() == 0);
		
		IType topLevelElement = srcModule.getType("<unnamed>"); // TODO fix this
		
		checkElementExists(srcModule, EArcheType.Class, 
			topLevelElement.getType("Foo"), "class Foo");
		checkElementExists(srcModule, EArcheType.Function, 
			topLevelElement.getType("Foo").getMethod("func"), "void func()");
		
		
//		checkElementExists(implicitNameMod, implicitNameMod.getType("moduleDeclImplicitName"), 
//			"module actualModuleName_DifferentFromFileName;");
//
//		IModelElement atZero = implicitNameMod.getElementAt(0);
//		assertEquals(atZero.getElementName(), "moduleDeclImplicitName");
//		assertEquals(atZero.getParent(), implicitNameMod);
	}
	
	public static ArrayList<IMember> getChild(IParent element, String childName) throws ModelException {
		assertCast(null, IMember.class);
		ArrayList<IMember> matchedChildren = new ArrayList<IMember>();
		
		for (IModelElement child : element.getChildren()) {
			IMember member = assertCast(child, IMember.class);
			if(child.getElementName().equals(childName)) {
				matchedChildren.add(member);
			}
		}
		return matchedChildren;
	}
	
	@Test
	public void testMismatchedModuleName() throws Exception { testMismatchedModuleName$(); }
	public void testMismatchedModuleName$() throws Exception {
		ISourceModule incorrectNameMod = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclIncorrectName.d");
		assertEquals(incorrectNameMod.getElementName(), "moduleDeclIncorrectName.d");
		
		checkElementExists(incorrectNameMod, EArcheType.Module, 
				incorrectNameMod.getType("actualModuleName_DifferentFromFileName"), "module actualModuleName_DifferentFromFileName;");
	}
	
}
