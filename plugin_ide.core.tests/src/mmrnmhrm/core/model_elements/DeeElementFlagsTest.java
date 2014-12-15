/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.engine.scoping.NamedElementsScope;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.definitions.EArcheType;
import dtool.engine.analysis.DeeLanguageIntrinsics;

public class DeeElementFlagsTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		NamedElementsScope primitivesScope = DeeLanguageIntrinsics.D2_063_intrinsics.primitivesScope;
		for (INamedElement nativeDefUnit : primitivesScope.getScopeNodeList()) {
			assertTrue(new DefElementDescriptor(nativeDefUnit).isNative());
		}
		
		for (EArcheType archeType : EArcheType.values()) {
			testArchetype(archeType);
		}
	}
	
	public void testArchetype(EArcheType archeType) {
		int elementFlags = DefElementFlagsUtil.elementFlagsForArchetype(archeType);
		assertTrue(archeType == DefElementFlagsUtil.elementFlagsToArcheType(elementFlags));
	}
	
}