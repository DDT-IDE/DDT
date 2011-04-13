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

import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

public class DeeModelElement_Test extends BaseDeeTest implements ITestResourcesConstants {
	
	public static ISourceModule getSourceModule(String srcFolder, String cuPath) {
		return SampleMainProject.getSourceModule(srcFolder, cuPath);
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		ISourceModule sourceModule = getSourceModule(TR_CA, "sampledefs.d");
		IModelElement elemZero = sourceModule.getElementAt(0);
		assertEquals(elemZero.getElementName(), "sampledefs");
		
		// TODO: test the other elements
		
//		checkForElement(sourceModule, "alias Class Alias", "Alias");
//		checkForElement(sourceModule, "enum Enum { EnumMemberA, EnumMemberB }", "Enum");
		checkForElement(sourceModule, "struct Struct { }", "Struct");
		checkForElement(sourceModule, "class Class", "Class");
		checkForElement(sourceModule, "template Template(", "Template");
		
		checkForElement(sourceModule, "int variable;", "variable");
		
		checkForElement(sourceModule, "void func(asdf.qwer parameter)", "func");
		
	}
	protected void checkForElement(ISourceModule sourceModule, String code, String elementName) throws ModelException {
		String source = sourceModule.getSource();
		IModelElement elemFoo = sourceModule.getElementAt(source.indexOf(code));
		assertEquals(elemFoo.getElementName(), elementName);
	}
	
	@Test
	public void testModuleName() throws Exception { testModuleName$(); }
	public void testModuleName$() throws Exception {
		if(true) return; // TODO fix the bug
		
		ISourceModule implicitNameMod = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclImplicitName.d");
		assertEquals(implicitNameMod.getElementName(), "moduleDeclImplicitName.d");
		IModelElement atZero = implicitNameMod.getElementAt(0);
		assertEquals(atZero.getElementName(), "moduleDeclImplicitName");
		assertEquals(atZero.getParent(), implicitNameMod);
	}
	
	@Test
	public void testIncorrectName() throws Exception { testIncorrectName$(); }
	public void testIncorrectName$() throws Exception {
		ISourceModule incorrectNameMod = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclIncorrectName.d");
		assertEquals(incorrectNameMod.getElementName(), "moduleDeclIncorrectName.d");
		IModelElement atZero = incorrectNameMod.getElementAt(0);
		assertEquals(atZero.getElementName(), "moduleNameIsDifferentFromFileName");
		assertEquals(atZero.getParent(), incorrectNameMod);
	}
	
}
