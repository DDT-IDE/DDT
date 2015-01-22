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

import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.engine.ResolvedModule;



public class NE_OverloadElement_Test extends NamedElement_CommonTest {
	
	protected PickedElement<INamedElement> pickedElementFromResolution(String source) {
		ResolvedModule parsedModule = parseModule_(source);
		INamedElement element = NameLookup_ScopeTest.getReferenceResolvedElement(parsedModule, "/*M*/");
		return new PickedElement<>(element, parsedModule.getSemanticContext());
	}
	
	@Override
	public void test_NamedElement________() throws Exception {
		test_NamedElement(pickedElementFromResolution("void xxx; int xxx; auto _ = xxx/*M*/; "), 
			null, expectNotAValue("xxx"), strings());
	}
	
}