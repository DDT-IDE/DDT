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

import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

public class DeeSelectionEngine_Test extends BaseDeeTest implements ITestResourcesConstants {
	
	
	protected static IModelElement selectSingleElement(ISourceModule sourceModule, int offset, int length) {
		DeeSelectionEngine deeSelectionEngine = new DeeSelectionEngine();
		IModelElement[] selection = deeSelectionEngine.select((IModuleSource) sourceModule, offset);
		assertTrue(selection.length <= length);
		return selection.length > 0 ? selection[0] : null;
	}
	
	@Test
	public void testEngineSelection() throws Exception { testEngineSelection$(); }
	public void testEngineSelection$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(TR_REFS, "sampleRefs.d");
		
		checkEngineSelection(srcModule, "mod1Var", handle("pack{", "mod1", "[mod1Var"));
		checkEngineSelection(srcModule, "Mod1Class", handle("pack{", "mod1", "[Mod1Class"));
		
		checkEngineSelection(srcModule, "foopublicImportVar", handle("pack2{", "foopublic", "[foopublicImportVar"));
		
		
		checkEngineSelection(srcModule, "ClassThatDoesNotExist", null);
	}
	
	private String handle(String packageStr, String moduleName, String memberPath) {
		return "<"+packageStr+moduleName+".d["+moduleName+memberPath;
	}
	
	
	@Test
	public void testSelectOtherKinfOfNodes() throws Exception { testSelectOtherKinfOfNodes$(); }
	public void testSelectOtherKinfOfNodes$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(TR_REFS, "sampleRefs.d");
		
		checkEngineSelection(srcModule, "mod1class", null);
		checkEngineSelection(srcModule, "dummy2", null);
	}
	
	protected void checkEngineSelection(ISourceModule srcModule, String nodeSrcKey, String expectedHandle)
			throws ModelException {
		String source = srcModule.getSource();
		int offset = source.indexOf(nodeSrcKey);
		do {
			IModelElement selectedElement = selectSingleElement(srcModule, offset, 1);
			if(expectedHandle == null) {
				assertTrue(selectedElement == null);
			} else {
				assertTrue(selectedElement.getHandleIdentifier().endsWith(expectedHandle));
			}
			offset = source.indexOf(nodeSrcKey, offset+1);
		} while(offset != -1);
	}
	
}
