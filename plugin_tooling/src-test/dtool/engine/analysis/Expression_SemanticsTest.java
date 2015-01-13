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

import static melnorme.lang.tooling.engine.resolver.NamedElementSemantics.NotAValueErrorElement.ERROR_IS_NOT_A_VALUE;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.fntypes.Predicate;

import org.junit.Test;

import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpParentheses;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.IInitializer;
import dtool.engine.analysis.DeeLanguageIntrinsics.IntrinsicDynArray;

public class Expression_SemanticsTest extends CommonNodeSemanticsTest {

	@Test
	public void testname() throws Exception { testname$(); }
	public void testname$() throws Exception {
		
		testExpResolve(parseElement("auto _ = 123/*M*/;", ExpLiteralInteger.class), 
			namedElementChecker("$/int"));
		
		testExpResolve(parseElement("auto _ = `str`/*M*/;", ExpLiteralString.class), 
			namedElementChecker("$object/string"));
		testExpResolve(parseElement("auto _ = `str`/*M*/;", IInitializer.class), 
			namedElementChecker("$object/string"));
		
		
		testExpResolve(parseElement("int xxx; auto _ = xxx/*M*/;", ExpReference.class), 
			namedElementChecker("$/int"));
		testExpResolve(parseElement("int xxx; auto _ = (xxx)/*M*/;", ExpParentheses.class), 
			namedElementChecker("$/int"));
		
		testExpResolve(parseElement("int foo; auto xxx = foo; auto _ = xxx/*M*/;", ExpReference.class), 
			namedElementChecker("$/int"));
		testExpResolve(parseElement("string foo; auto xxx = foo; auto _ = xxx/*M*/;", ExpReference.class), 
			namedElementChecker("$object/string"));
		
		testExpResolve(parseElement("NotFoundFoo foo; auto xxx = foo; auto _ = xxx/*M*/;", ExpReference.class), 
			namedElementChecker("#NotFound:NotFoundFoo"));
		testExpResolve(parseElement("auto _ = xxx/*M*/;", ExpReference.class), 
			namedElementChecker("#NotFound:xxx"));
		testExpResolve(parseElement("auto _ = (xxx)/*M*/;", ExpParentheses.class), 
			namedElementChecker("#NotFound:xxx"));
		
		testExpResolve(parseElement("auto _ = string/*M*/;", IInitializer.class), 
			namedElementChecker(ERROR_IS_NOT_A_VALUE + ":" + IntrinsicDynArray.DYNAMIC_ARRAY_NAME));
		testExpResolve(parseElement("auto _ = string/*M*/;", ExpReference.class),
			
			namedElementChecker(ERROR_IS_NOT_A_VALUE + ":" + IntrinsicDynArray.DYNAMIC_ARRAY_NAME));
		testExpResolve(parseElement("auto _ = (string)/*M*/;", ExpParentheses.class), 
			namedElementChecker(ERROR_IS_NOT_A_VALUE + ":" + IntrinsicDynArray.DYNAMIC_ARRAY_NAME));
		testExpResolve(parseElement("auto _ = (int)/*M*/;", ExpParentheses.class), 
			namedElementChecker(ERROR_IS_NOT_A_VALUE + ":int"));
		
		// Test qualified refs:
		testExpResolve(parseElement("int xxx; auto _ = (xxx)/*M*/.init;", ExpParentheses.class), 
			namedElementChecker("intrinsic_type#int"));
		testExpResolve(parseElement("int xxx; auto _ = (xxx).init/*M*/;", ExpReference.class), 
			namedElementChecker("intrinsic_type#int"));
		
		testExpResolve(parseElement("auto _ = (int).init/*M*/;", ExpReference.class), 
			namedElementChecker("intrinsic_type#int"));
		
	}
	
	protected void testExpResolve(PickedElement<? extends IInitializer> pe,
			Predicate<INamedElement> checker) {
		pe.context._resetSemantics();
		
		IInitializer element = pe.element;
		INamedElement typeOfUnderlyingValue = element.resolveTypeOfUnderlyingValue(pe.context).originalType;
		checker.evaluate(typeOfUnderlyingValue);
	}
	
}