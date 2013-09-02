/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.util.Iterator;

import mmrnmhrm.core.model_elements.DeeModelElement_Test;

import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.junit.Ignore;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.INamedElement;
import dtool.ast.util.NodeUtil;
import dtool.resolver.ReferenceResolver.DirectDefUnitResolve;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;

@Ignore // TODO properly define this test
public class SelectionEngineSourceTests extends CoreResolverSourceTests {
	
	public SelectionEngineSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	@Override
	public void runFindTest_________(MetadataEntry mde) {
//		DirectDefUnitResolve resolveResult = doFindTest(mde);
		DirectDefUnitResolve resolveResult = null;
		
		// TODO: adapt test to more than one defUnit returned?
		Iterator<INamedElement> iterator = resolveResult.getResolvedDefUnits().iterator();
		INamedElement defElement = iterator.hasNext() ? iterator.next() : null;
		if(defElement instanceof DefUnit) {
			DefUnit defUnit = (DefUnit) defElement;
			SelectionEngineSourceTests.testDeeSelectionEngine(sourceModule, mde.offset, defUnit);
		}
	}
	
	@Override
	public void runFindMissingTest_________(MetadataEntry mde) {
		// TODO:
	}
	
	@Override
	public void runFindFailTest_________(MetadataEntry mde) {
		// TODO:
	}
	
	public static void testDeeSelectionEngine(ISourceModule moduleElement, int offset, DefUnit defunit) {
		DeeSelectionEngine selectionEngine = new DeeSelectionEngine();
		IModelElement[] select = selectionEngine.select((IModuleSource) moduleElement, offset, offset-1);
		
		if(!DeeModelElement_Test.defunitIsReportedAsModelElement(defunit)) {
			// Hum, Perhaps do this case differently?
			assertTrue(select == null || select.length == 0);
			return;
		}
		
		assertTrue(select.length >= 1);
		IModelElement modelElement = select[0];
		for (int i = 1; i < select.length; i++) {
			assertEquals(modelElement.getElementName(), select[i].getElementName());
			assertEquals(modelElement.getParent(), select[i].getParent());
		}
		
		while(true) {
			assertNotNull(modelElement);
			if(modelElement.getElementType() == IModelElement.SOURCE_MODULE) {
				assertTrue(defunit == null);
				break;
			}
			assertEquals(defunit.getName(), modelElement.getElementName());
			defunit = NodeUtil.getOuterDefUnit(defunit);
			modelElement = modelElement.getParent();
		}
	}

	@Override
	protected void runRefSearchTest_________(RefSearchOptions options) {
		// Do nothing
	}
	
}