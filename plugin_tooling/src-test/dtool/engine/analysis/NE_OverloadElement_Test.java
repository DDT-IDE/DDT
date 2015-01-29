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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.StringUtil;
import dtool.engine.ResolvedModule;



public class NE_OverloadElement_Test extends NamedElement_CommonTest {
	
	protected PickedElement<INamedElement> pickedElementFromResolution(String source) {
		ResolvedModule parsedModule = parseModule_(source);
		INamedElement element = NameLookup_ScopeTest.getReferenceResolvedElement(parsedModule, "/*M*/");
		return new PickedElement<>(element, parsedModule.getSemanticContext());
	}
	
	@Override
	public void test_NamedElement________() throws Exception {
		PickedElement<INamedElement> pick = pickedElementFromResolution("void xxx; int xxx; auto _ = xxx/*M*/; ");
		
		INamedElement overloadElement = pick.element;
		assertTrue(overloadElement.getName().equals("xxx"));
		assertTrue(overloadElement.getExtendedName().equals("xxx"));
		
		test_resolveConcreteElement(pick, nameConflict("void xxx;", "int xxx;"));
		
		test_NamedElement(pick, null, expectNotAValue("xxx"), strings());
		
		
		pick = pickedElementFromResolution("import overload; auto _ = xxx/*M*/; ");
		test_resolveConcreteElement(pick, nameConflict("void xxx;", "int xxx;"));
		
		pick = pickedElementFromResolution("import overload; import overload2; auto _ = xxx/*M*/; ");
		
		// We change so that name conflict is squashed togheter
		test_resolveConcreteElement(pick, 
			nameConflict(nameConflict("void xxx;", "int xxx;"), nameConflict("bool xxx;", "int xxx;"))
			);
	}
	
	protected String nameConflict(String... subElements) {
		return "#NameConflict["+StringUtil.collToString(subElements, "| ") + "]";
	}
	
}