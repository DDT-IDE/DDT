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

import dtool.engine.ResolvedModule;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.INamedElement;



public class NE_OverloadElement_Test extends NamedElement_CommonTest {
	
	@Override
	public void test_resolveElement________() throws Exception {
		test_resolveElement(
			pickedElementFromResolution("void xxx; int xxx; auto _ = xxx/*M*/; "), null, "xxx", true);
	}
	
	protected PickedElement<INamedElement> pickedElementFromResolution(String source) {
		ResolvedModule parsedModule = parseModule_(source);
		INamedElement element = NameLookup_ScopeTest.getReferenceResolvedElement(parsedModule, "/*M*/");
		PickedElement<INamedElement> pe = new PickedElement<>(element, parsedModule.getSemanticContext());
		return pe;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void test_resolveSearchInMembersScope________() throws Exception {
		
		test_resolveSearchInMembersScope(
			pickedElementFromResolution("void xxx; int xxx; auto _ = xxx/*M*/; "));
		
	}
	
}