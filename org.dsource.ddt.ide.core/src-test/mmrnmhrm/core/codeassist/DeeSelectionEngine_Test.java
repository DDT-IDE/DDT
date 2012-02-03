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
package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

import dtool.tests.DToolTests;

public class DeeSelectionEngine_Test extends BaseDeeTest implements ITestResourcesConstants {
	
	private static final String SAMPLE_REFS = "sampleRefs.d";

	protected static IModelElement selectSingleElement(ISourceModule sourceModule, int offset, int length) {
		DeeSelectionEngine deeSelectionEngine = new DeeSelectionEngine();
		IModelElement[] selection = deeSelectionEngine.select((IModuleSource) sourceModule, offset);
		assertTrue(selection.length <= length);
		return selection.length > 0 ? selection[0] : null;
	}
	
	@Test
	public void testEngineSelection() throws Exception { testEngineSelection$(); }
	public void testEngineSelection$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(TR_REFS, SAMPLE_REFS);
		
		checkEngineSelection(srcModule, "mod1Var", 
				getTarget("pack", "mod1").getField("mod1Var"));
		checkEngineSelection(srcModule, "Mod1Class", 
				getTarget("pack", "mod1").getType("Mod1Class"));
		
		checkEngineSelection(srcModule, "foopublicImportVar", 
				getTarget("pack2", "foopublic").getField("foopublicImportVar"));
		
		checkEngineSelection(srcModule, "ClassThatDoesNotExist", null);
	}
	
	protected IType getTarget(String pkg, String module, String type) {
		IType moduleType = getTarget(pkg, module);
		return moduleType.getType(type);
	}
	
	protected IType getTarget(String pkg, String module) {
		IProjectFragment fragment = SampleMainProject.getFolderProjectFragment(TR_SAMPLE_SRC3);
		IType moduleType = fragment.getScriptFolder(new Path(pkg)).getSourceModule(module + ".d").getType(module);
		return moduleType;
	}
	
	@Test
	public void testSelectOtherKinfOfNodes() throws Exception { testSelectOtherKinfOfNodes$(); }
	public void testSelectOtherKinfOfNodes$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(TR_REFS, SAMPLE_REFS);
		
		checkEngineSelection(srcModule, "class ", null);
		checkEngineSelection(srcModule, "void ", null);
	}
	
	protected void checkEngineSelection(ISourceModule srcModule, String nodeSrcKey, IMember expectedElement)
			throws ModelException {
		String source = srcModule.getSource();
		assertTrue(expectedElement == null || expectedElement.exists());
		int offset = source.indexOf(nodeSrcKey);
		do {
			IModelElement selectedElement = selectSingleElement(srcModule, offset, 1);
			assertAreEqual(selectedElement, expectedElement);
			offset = source.indexOf(nodeSrcKey, offset+1);
		} while(offset != -1);
	}
	
	@Test
	public void testMultipleSelection() throws Exception { testMultipleSelection$(); }
	public void testMultipleSelection$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(TR_REFS, SAMPLE_REFS);
		
		checkEngineMultipleSelection(srcModule, "/*Class1*/", 
				getModuleContainer(srcModule).getType("SampleRefsClass", 1)
		);
		if(DToolTests.UNSUPPORTED_FUNCTIONALITY_MARKER) {
			//This is disabled because of the way Class Templates are represented in the AST structure
			checkEngineMultipleSelection(srcModule, "/*Class3*/", 
					getModuleContainer(srcModule).getType("SampleRefsClass", 3)
			);
		}

		checkEngineMultipleSelection(srcModule, "/*MultipleSelection*/", 
				getModuleContainer(srcModule).getType("SampleRefsClass", 1),
				getModuleContainer(srcModule).getType("SampleRefsClass", 2),
				findField(getModuleContainer(srcModule), "SampleRefsClass", 1),
				getModuleContainer(srcModule).getType("SampleRefsClass", 3)
		);
		
		checkEngineMultipleSelection(srcModule, "/*MultipleSelection2*/", 
				findMethod(getModuleContainer(srcModule).getType("Parent"), "func", 1),
				findMethod(getModuleContainer(srcModule).getType("Parent"), "func", 2),
				findMethod(getModuleContainer(srcModule).getType("Parent"), "func", 3)
		);
		
		checkEngineMultipleSelection(srcModule, "/*func2*/", 
				findMethod(getModuleContainer(srcModule).getType("Parent"), "func", 2)
		);
		
	}
	
	public static IMethod findMethod(IMember parent, String name, int occurrenceCount) throws ModelException {
		return  (IMethod) DeeModelEngine.findMember(parent, IModelElement.METHOD, name, occurrenceCount);
	}
	
	public static IField findField(IMember parent, String name, int occurrenceCount) throws ModelException {
		return (IField) DeeModelEngine.findMember(parent, IModelElement.FIELD, name, occurrenceCount);
	}
	
	protected IType getModuleContainer(ISourceModule srcModule) throws ModelException {
		return srcModule.getTypes()[0];
	}
	
	protected void checkEngineMultipleSelection(ISourceModule srcModule, String srcKey, IMember... expectedElements)
			throws ModelException {
		String source = srcModule.getSource();
		int offset = source.indexOf(srcKey);
		if(srcKey.startsWith("/")) {
			offset = offset + srcKey.length();
		}
		
		do {
			DeeSelectionEngine deeSelectionEngine = new DeeSelectionEngine();
			IModelElement[] selection = deeSelectionEngine.select((IModuleSource) srcModule, offset);
			assertTrue(selection.length == expectedElements.length);
			
			for (int i = 0; i < selection.length; i++) {
				IModelElement selectedElement = selection[i];
				IModelElement expectedElement = expectedElements[i];
				assertTrue(expectedElement == null || expectedElement.exists());
				
				assertAreEqual(selectedElement, expectedElement);
			}
			
			offset = source.indexOf(srcKey, offset+1);
		} while(offset != -1);
	}
	
}
