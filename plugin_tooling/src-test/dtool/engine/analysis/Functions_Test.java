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
import melnorme.utilbox.core.fntypes.Predicate;

import org.junit.Test;

import dtool.ast.expressions.Expression;
import dtool.ast.expressions.IInitializer;

public class Functions_Test extends CommonNodeSemanticsTest {

	@Test
	public void testname() throws Exception { testname$(); }
	public void testname$() throws Exception {
		
		testExpTypeResolve(parseElement("void function(int) func; auto _ = func/*M*/;", Expression.class), 
			namedElementChecker("$/<funtion>"));
		
		// TODO: 
		
//		testExpTypeResolve(parseElement("void func(int); auto _ = func/*M*/;", Expression.class), 
//			namedElementChecker("$/int"));
		
	}
	
	protected void testExpTypeResolve(PickedElement<? extends IInitializer> pe, Predicate<INamedElement> checker) {
		pe.context._resetSemantics();
		
		IInitializer element = pe.element;
		INamedElement typeOfUnderlyingValue = element.resolveTypeOfUnderlyingValue(pe.context).originalType;
		checker.evaluate(typeOfUnderlyingValue);
	}
	
}