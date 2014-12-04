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

import static dtool.engine.analysis.DeeLanguageIntrinsics.D2_063_intrinsics;

import java.util.ArrayList;

import dtool.ast.expressions.Expression;
import dtool.engine.analysis.DeeLanguageIntrinsics.IntrinsicDynArray;
import dtool.engine.analysis.DeeLanguageIntrinsics.IntrinsicStaticArray;
import dtool.engine.analysis.DeeLanguageIntrinsics.IntrinsicTypePointer;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.IntrinsicTypeDefUnit;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;

public class LanguageIntrinsics_SemanticsTest extends NamedElement_CommonTest {
	
	@Override
	public void test_resolveElement________() throws Exception {
		test_resolveElement_Concrete(pickedNative(D2_063_intrinsics.float_type), 
			"float", true);
		test_resolveElement_Concrete(pickedNative(D2_063_intrinsics.pointerType), 
			IntrinsicTypePointer.POINTER_NAME, true);
		test_resolveElement_Concrete(pickedNative(D2_063_intrinsics.dynArrayType), 
			IntrinsicDynArray.DYNAMIC_ARRAY_NAME, true);
		test_resolveElement_Concrete(pickedNative(D2_063_intrinsics.staticArrayType), 
			IntrinsicStaticArray.STATIC_ARRAY_NAME, true);
		
		// TODO: test the intrinsic properties as well
	}
	
	/* ----------------- ----------------- */
	
	public static final String[] PRIMITIVE_TYPES = array(
		"/bool", 
		"/byte", "/ubyte", "/short", "/ushort", "/int", "/uint", "/long", "/ulong", 
		"/char", "/wchar", "/dchar", 
		"/float", "/double", "/real", 
		"/void", 
		"/ifloat", "/idouble", "/ireal", "/cfloat", "/cdouble", "/creal"
	);
	
	protected static final String[] INT_PROPERTIES = ArrayUtil.concat(COMMON_PROPERTIES,
		"max", "min"
	);
	protected static final String[] FLOAT_PROPERTIES = ArrayUtil.concat(COMMON_PROPERTIES,
		ArrayUtil.createFrom(getMemberNames2(D2_063_intrinsics.float_type), String.class)
	);
	protected static final String[] DYN_ARRAY_PROPERTIES = ArrayUtil.concat(COMMON_PROPERTIES,
		ArrayUtil.createFrom(getMemberNames2(D2_063_intrinsics.dynArrayType), String.class)
	);
	protected static final String[] STATIC_ARRAY_PROPERTIES = ArrayUtil.concat(COMMON_PROPERTIES,
		ArrayUtil.createFrom(getMemberNames2(D2_063_intrinsics.staticArrayType), String.class)
	);
	
	public static ArrayList<String> getMemberNames2(IntrinsicTypeDefUnit intrinsicDefUnit) {
		ArrayList<String> names = new ArrayList<>();
		for (INamedElement defUnit : intrinsicDefUnit.getMembersScope().members) {
			names.add(defUnit.getName());
		}
		return names;
	}
	
	@Override
	public void test_resolveSearchInMembersScope________() throws Exception {
		test_resolveSearchInMembersScope(pickedNative(D2_063_intrinsics.bool_type), COMMON_PROPERTIES);
		testExpressionResolution_expSource("true", COMMON_PROPERTIES);
		
		test_resolveSearchInMembersScope(pickedNative(D2_063_intrinsics.int_type), INT_PROPERTIES);
		testExpressionResolution_expSource("123", INT_PROPERTIES);
		
		test_resolveSearchInMembersScope(pickedNative(D2_063_intrinsics.char_type), INT_PROPERTIES);
		testExpressionResolution_expSource("'c'", INT_PROPERTIES);
		
		test_resolveSearchInMembersScope(pickedNative(D2_063_intrinsics.float_type), FLOAT_PROPERTIES);
		testExpressionResolution_expSource("123.123", FLOAT_PROPERTIES);
		testExpressionResolution_expSource("123.123f", FLOAT_PROPERTIES);
		
		
		test_resolveSearchInMembersScope(pickedNative(D2_063_intrinsics.dynArrayType), DYN_ARRAY_PROPERTIES);
		test_resolveSearchInMembersScope(pickedNative(D2_063_intrinsics.staticArrayType), STATIC_ARRAY_PROPERTIES);
	}
	
	protected static void testExpressionResolution_expSource(String expSource, String... expectedResults) {
		Expression exp = new DeeTestsChecksParser(expSource).parseExpression().getNode();
		testExpressionResolution_(exp, expectedResults);
	}
	
}