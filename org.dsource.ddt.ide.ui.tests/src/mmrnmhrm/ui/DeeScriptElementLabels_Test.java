/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.model_elements.SampleModelElementsVisitor;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.ModelElementTestUtils;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.ui.views.DeeModelElementLabelProvider;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.junit.Test;

public class DeeScriptElementLabels_Test extends BaseDeeTest implements ITestResourcesConstants {
	
	private static final long DEE_SEARCHPAGE_FLAGS = 
			ScriptElementLabels.T_FULLY_QUALIFIED |
			ScriptElementLabels.T_TYPE_PARAMETERS |
			ScriptElementLabels.F_FULLY_QUALIFIED |
			ScriptElementLabels.M_FULLY_QUALIFIED | ScriptElementLabels.M_PARAMETER_NAMES  
			;
	
	public static ISourceModule getSourceModule(String srcFolder, String cuPath) {
		ISourceModule sourceModule = SampleMainProject.getSourceModule(srcFolder, cuPath);
		assertTrue(sourceModule.exists());
		return sourceModule;
	}
	
	final DeeScriptElementLabels sel = DeeUILanguageToolkit.getDefault().getScriptElementLabels();
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		ISourceModule srcModule;
		
//		srcModule = getSourceModule(TR_SAMPLE_SRC1, "sampledefs.d");
//		runTest(srcModule, "sampledefs", "");
		
		srcModule = getSourceModule(TR_SAMPLE_SRC1, "packA/sampledefs_inpack.d");
		runTest(srcModule, "sampledefs_inpack", "packA.");
		
		srcModule = getSourceModule(TR_SAMPLE_SRC1, "packA/subpack/sampledefs_inpack.d");
		runTest(srcModule, "sampledefs_inpack", "packA.subpack.");
	}
	
	protected void runTest(ISourceModule srcModule, String moduleName, String packageQualification) 
			throws ModelException {
		IType topLevelElement = srcModule.getType(moduleName);
		checkLabel(topLevelElement, packageQualification + moduleName);
		
		String qualification = packageQualification + moduleName + ".";
		checkLabel(topLevelElement.getType("Class"), qualification + "Class");
		checkLabel(topLevelElement.getType("Enum"), qualification + "Enum");
		
		checkLabel(topLevelElement.getType("Template"), qualification + "Template");

		checkLabel(topLevelElement.getField("variable"), qualification + "variable");
		checkLabel(topLevelElement.getType("Class").getField("fieldA"), qualification + "Class.fieldA");
		
		checkLabel(getChild(topLevelElement, "functionFoo"), qualification + "functionFoo(int)");
		
		checkLabel(getChild(topLevelElement.getType("Class"), "methodB"), qualification + "Class.methodB()");
		checkLabel(getChild(topLevelElement.getType("Class"), "this"), qualification + "Class.this(int)");
//		checkLabel(getChild(topLevelElement.getType("Class"), "~this"), qualification + "Class.~this()");
		
//		checkLabel(getChild(getChild(topLevelElement, "functionFoo"), "fooLocalVar"), 
//				qualification + "functionFoo(int).fooLocalVar");
		
		checkLabel(topLevelElement.getType("Template").getType("TplNestedClass"), 
				qualification + "Template.TplNestedClass");
	}
	
	protected IMember getChild(IMember topLevelElement, String childName) throws ModelException {
		IMember child = ModelElementTestUtils.getChild(topLevelElement, childName);
		assertNotNull(child);
		return child;
	}
	
	protected void checkLabel(IModelElement element, String expected) {
		assertTrue(element.exists());
		String elementLabel = sel.getElementLabel(element, DEE_SEARCHPAGE_FLAGS);
		assertEquals(elementLabel, expected);
		
		DeeModelElementLabelProvider labelProvider = new DeeModelElementLabelProvider();
		labelProvider.getImage(element);
	}
	
	protected void checkLabel(IModelElement element, String expectedQualification, String expected) {
		checkLabel(element, expectedQualification + expected);
	}
	
	@Test
	public void testSampleModelElements() throws Exception { testSampleModelElements$(); }
	public void testSampleModelElements$() throws Exception {
		ISourceModule srcModule = getSourceModule(TR_SAMPLE_SRC1, "sampledefs.d");
		final String packageQualification = "";
		
		new SampleModelElementsVisitor(srcModule) { 
			@Override
			public void visitAllModelElements(
				IType _Module,
				IField _Variable,
				IField _Variable2,
				IField _VarExtended,
				IField _VarExtended2,
				IField _AutoVar,
				IField _AutoVar2,
				
				IMethod _Function_,
				IMethod _AutoFunction,
				
				IType _Struct,
				IType _Union,
				IType _Class,
				IType _Interface,
				IType _Template,
				IType _Enum,
				IType _Mixin,
				IType _AliasVarDecl,
				IType _AliasFunctionDecl,
				IType _AliasFrag,
				IType _AliasFrag2,
				IField _OtherClass_fieldA,
				IMethod _OtherClass_methodB,
				IMethod _OtherClass_this,
				IType _OtherTemplate_TplNestedClass,
				IMethod tplFunc) {
				
				String moduleName = "sampledefs";
				checkLabel(_Module, packageQualification + moduleName);
				
				String qualification = packageQualification + moduleName + ".";
				
				checkLabel(_Variable, qualification, "Variable");
				checkLabel(_Variable2, qualification, "Variable2");
				checkLabel(_VarExtended, qualification, "VarExtended");
				checkLabel(_VarExtended2, qualification, "VarExtended2");
				
				
				checkLabel(_AutoVar, qualification, "AutoVar");
				checkLabel(_AutoVar2, qualification, "AutoVar2");
				
				checkLabel(_Function_, qualification, "Function(int)");
				checkLabel(_AutoFunction, qualification, "AutoFunction(int)");
				
				checkLabel(_Struct, qualification, "Struct");
				checkLabel(_Union, qualification, "Union");
				checkLabel(_Class, qualification, "Class");
				checkLabel(_Interface, qualification, "Interface");
		
				checkLabel(_Template, qualification, "Template");
				checkLabel(_Enum, qualification, "Enum");
				
				checkLabel(_Mixin, qualification, "Mixin");
				
				checkLabel(_AliasVarDecl, qualification, "AliasVarDecl");
				checkLabel(_AliasFunctionDecl, qualification, "AliasFunctionDecl");
				checkLabel(_AliasFrag, qualification, "AliasFrag");
				checkLabel(_AliasFrag2, qualification, "AliasFrag2");
				
				checkLabel(_OtherClass_fieldA, qualification, "OtherClass.fieldA");
				checkLabel(_OtherClass_methodB, qualification, "OtherClass.methodB()");
				checkLabel(_OtherClass_this, qualification, "OtherClass.this(int)");
				
				checkLabel(_OtherTemplate_TplNestedClass, qualification, "OtherTemplate.TplNestedClass");
				checkLabel(tplFunc, qualification, "OtherTemplate.TplNestedClass.tplFunc(asdf.qwer)");
				
			}
		}.visitAll();
	}
	
}