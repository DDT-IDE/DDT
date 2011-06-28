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

import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

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
		
		checkEngineSelection(srcModule, "mod1Var", getTarget("pack", "mod1", "mod1Var"));
		checkEngineSelection(srcModule, "Mod1Class", getTarget("pack", "mod1", "Mod1Class"));
		
		checkEngineSelection(srcModule, "foopublicImportVar", getTarget("pack2", "foopublic", "foopublicImportVar"));
		
		checkEngineSelection(srcModule, "ClassThatDoesNotExist", null);
	}
	
	protected IType getTarget(String pkg, String module, String type) {
		IProjectFragment fragment = SampleMainProject.getFolderProjectFragment(TR_SAMPLE_SRC3);
		return fragment.getScriptFolder(new Path(pkg)).getSourceModule(module + ".d").getType(module).getType(type);
	}
	
	@Test
	public void testSelectOtherKinfOfNodes() throws Exception { testSelectOtherKinfOfNodes$(); }
	public void testSelectOtherKinfOfNodes$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(TR_REFS, SAMPLE_REFS);
		
		checkEngineSelection(srcModule, "class ", null);
		checkEngineSelection(srcModule, "void ", null);
	}
	
	protected void checkEngineSelection(ISourceModule srcModule, String nodeSrcKey, IType expectedElement)
			throws ModelException {
		String source = srcModule.getSource();
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
		
		checkEngineMultipleSelection(srcModule, "/*MultipleSelection*/", 
				srcModule.getTypes()[0].getType("SampleRefsClass", 1),
				srcModule.getTypes()[0].getType("SampleRefsClass", 2),
				srcModule.getTypes()[0].getType("SampleRefsClass", 3)
		);
		
		checkEngineMultipleSelection(srcModule, "/*MultipleSelection2*/", 
				srcModule.getTypes()[0].getType("Parent").getType("func", 1),
				srcModule.getTypes()[0].getType("Parent").getType("func", 2),
				srcModule.getTypes()[0].getType("Parent").getType("func", 3)
		);
		
		checkEngineMultipleSelection(srcModule, "/*Class1*/", 
				srcModule.getTypes()[0].getType("SampleRefsClass", 1)
		);
		// Following tests not yet supported
//		checkEngineMultipleSelection(srcModule, "/*Class3*/", 
//				srcModule.getTypes()[0].getType("SampleRefsClass", 3)
//		);
//		
//		checkEngineMultipleSelection(srcModule, "/*func2*/", 
//				srcModule.getTypes()[0].getType("Parent").getType("func", 2)
//		);
		
	}
	
	protected void checkEngineMultipleSelection(ISourceModule srcModule, String nodeSrcKey, IType... expectedElements)
			throws ModelException {
		String source = srcModule.getSource();
		int offset = source.indexOf(nodeSrcKey);
		if(nodeSrcKey.startsWith("/")) {
			offset = offset + nodeSrcKey.length();
		}
		
		do {
			DeeSelectionEngine deeSelectionEngine = new DeeSelectionEngine();
			IModelElement[] selection = deeSelectionEngine.select((IModuleSource) srcModule, offset);
			assertTrue(selection.length == expectedElements.length);
			
			for (int i = 0; i < selection.length; i++) {
				IModelElement selectedElement = selection[i];
				IModelElement expectedElement = expectedElements[i];
				
				assertAreEqual(selectedElement, expectedElement);
			}
			
			offset = source.indexOf(nodeSrcKey, offset+1);
		} while(offset != -1);
	}
	
}
