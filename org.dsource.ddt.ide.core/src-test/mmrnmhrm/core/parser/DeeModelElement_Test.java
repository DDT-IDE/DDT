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

import dtool.ast.declarations.DeclarationStaticIfIsType;
import dtool.ast.declarations.ImportAliasing;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.TemplateParameter;

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
		checkElementExists(srcModule, topLevelElement, 
				EArcheType.Module, "module sampledefs;");
		
		// TODO: test the other elements
		checkElementExists(srcModule, topLevelElement.getType("Alias"), 
			EArcheType.Alias, "alias TargetFoo Alias;");
		checkElementExists(srcModule, topLevelElement.getType("Class"), 
			EArcheType.Class, "class Class  {");
		checkElementExists(srcModule, topLevelElement.getType("Enum"), 
			EArcheType.Enum, "enum Enum {");
		checkElementExists(srcModule, topLevelElement.getType("Interface"), 
			EArcheType.Interface, "interface Interface { }");
		checkElementExists(srcModule, topLevelElement.getType("Struct"), 
			EArcheType.Struct, "struct Struct { }");
		checkElementExists(srcModule, topLevelElement.getType("Typedef"), 
			EArcheType.Typedef, "typedef TargetBar Typedef;");
		checkElementExists(srcModule, topLevelElement.getType("Union"), 
			EArcheType.Union, "union Union { }");
		checkElementExists(srcModule, topLevelElement.getField("variable"), 
			EArcheType.Variable, "int variable;");
		checkElementExists(srcModule, topLevelElement.getType("Template"), 
			EArcheType.Template, "template Template(");
		
		
		checkElementExists(srcModule, topLevelElement.getType("Class").getField("fieldA"), 
			EArcheType.Variable, "int fieldA;");
		checkElementExists(srcModule, topLevelElement.getType("Class").getMethod("methodB"), 
			EArcheType.Function, "void methodB() { }");
		
		checkElementExists(srcModule, topLevelElement.getType("Template").getType("TplNestedClass"), 
			EArcheType.Class, "class TplNestedClass  {");
		
		
		checkElementExists(srcModule, topLevelElement.getType("Template").getType("TplNestedClass").getMethod("func"), 
			EArcheType.Function, 
			"void func(asdf.qwer parameter) {");
		
		
		checkElementExists(srcModule, topLevelElement.getType("Class").getMethod("this"), 
				EArcheType.Function, "/*this*/", "this(int ");
		checkElementExists(srcModule, topLevelElement.getType("Class").getMethod("~this"), 
				EArcheType.Function, "/*~this*/", "~this()");
		checkElementExists(srcModule, topLevelElement.getType("Class").getMethod("new"), 
				EArcheType.Function, "/*new*/", "new()");
		checkElementExists(srcModule, topLevelElement.getType("Class").getMethod("delete"), 
				EArcheType.Function, "/*delete*/", "delete()");
		
		checkElementExists(srcModule, topLevelElement.getType("Template").getType("TplNestedClass").getMethod("this"), 
				EArcheType.Function, "/*static this*/", "static /*static this*/ this()");
		checkElementExists(srcModule, topLevelElement.getType("Template").getType("TplNestedClass").getMethod("~this"), 
				EArcheType.Function, "/*static ~this*/", "static /*static ~this*/ ~this()");
		
	}
	protected void checkElementExists(ISourceModule sourceModule, IMember element, EArcheType archeType, 
			String code) throws ModelException {
		checkElementExists(sourceModule, element, archeType, (String) null, code);
	}
	
	protected void checkElementExists(ISourceModule sourceModule, IMember element, EArcheType archeType, String nameKey,
			String code) throws ModelException {
		String source = sourceModule.getSource();
		
		assertTrue(element.exists());
		assertTrue(element.getCorrespondingResource() == null);
		assertTrue(element.getOpenable() == sourceModule);
		assertTrue(element.getSource().startsWith(code));
		int nameOffset = (nameKey == null) ? 
				source.indexOf(" " + element.getElementName()) + 1 :
				source.indexOf(nameKey) + nameKey.length() + 1;
		assertTrue(element.getNameRange().getOffset() == nameOffset);
		assertTrue(element.getNameRange().getLength() == element.getElementName().length());
		
		assertTrue(DeeModelElementUtil.elementFlagsToArcheType(element, element.getFlags()) == archeType);
		
		if(element.getNamespace() == null) {
			assertTrue(element.getParent() != sourceModule.getParent());
		} else {
			assertEquals(element.getNamespace().getQualifiedName("."), sourceModule.getParent().getElementName());
		}
	}
	
	protected static final String UNNAMED_DEFAULT = "<unnamed>";
	
	@Test
	public void testImplicitModuleName() throws Exception { testImplicitModuleName$(); }
	public void testImplicitModuleName$() throws Exception {
		ISourceModule srcModule = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclImplicitName.d");
		assertEquals(srcModule.getElementName(), "moduleDeclImplicitName.d");
		
		assertTrue(srcModule.getType("moduleDeclImplicitName").exists() == false);
		assertTrue(getChild(srcModule, "moduleDeclImplicitName").size() == 0);
		
		IType topLevelElement = srcModule.getType(UNNAMED_DEFAULT); // TODO fix this
		
		checkElementExists(srcModule, topLevelElement.getType("Foo"), 
			EArcheType.Class, "class Foo");
		checkElementExists(srcModule, topLevelElement.getType("Foo").getMethod("func"), 
			EArcheType.Function, "void func()");
		
		
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
		
		checkElementExists(incorrectNameMod, incorrectNameMod.getType("actualModuleName_DifferentFromFileName"), 
				EArcheType.Module, "module actualModuleName_DifferentFromFileName;");
	}
	
	@Test
	public void testNameSpace() throws Exception { testNameSpace$(); }
	public void testNameSpace$() throws Exception {
		IType topLevelElement;
		topLevelElement = getTopLevelElement(TR_CA, "/", "sampledefs");
		testNameSpace(topLevelElement, "", "Class");
		
		topLevelElement = getTopLevelElement(TR_SAMPLE_SRC3, "pack/", "mod1");
		testNameSpace(topLevelElement, "pack", "Mod1Class");
		
		topLevelElement = getTopLevelElement(TR_SAMPLE_SRC3, "pack/subpack/","mod3");
		testNameSpace(topLevelElement, "pack.subpack", "Mod3Class");
		
		topLevelElement = getSourceModule(TR_SAMPLE_SRC1, "moduleDeclImplicitName.d").
				getType(UNNAMED_DEFAULT);
		testNameSpace(topLevelElement, "", "Foo");
		
		topLevelElement = getSourceModule(TR_SAMPLE_SRC1, "src1pack/moduleDeclImplicitName2.d").
				getType(UNNAMED_DEFAULT);
		testNameSpace(topLevelElement, "", "Foo");
	}
	
	private IType getTopLevelElement(String srcFolder, String folderName, String moduleName) {
		return getSourceModule(srcFolder, folderName+"/"+moduleName+".d").getType(moduleName);
	}
	
	protected void testNameSpace(IType topLevelElement, String nameSpace, String sampleSubType) throws ModelException {
		assertEquals(topLevelElement.getNamespace().getQualifiedName("."), nameSpace);
		IType subElement = topLevelElement.getType(sampleSubType);
		assertTrue(subElement.exists() && subElement.getNamespace() == null);
	}
	
	
	/*  ---  */
	
	public static boolean defunitIsReportedAsModelElement(DefUnit defunit) {
		boolean result = 
				defunit instanceof FunctionParameter || 
				defunit instanceof EnumMember ||
				defunit instanceof TemplateParameter ||
				defunit instanceof DeclarationStaticIfIsType.IsTypeDefUnit ||
				defunit instanceof ImportSelective.ImportSelectiveAlias ||
				defunit instanceof ImportAliasing.ImportAliasingDefUnit;
		return !result;
	}
	
}
