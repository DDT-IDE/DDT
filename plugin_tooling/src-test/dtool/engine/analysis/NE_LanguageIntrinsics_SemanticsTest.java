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

import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.BuiltinTypeElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.expressions.Expression;

public class NE_LanguageIntrinsics_SemanticsTest extends NamedElement_CommonTest {
	
	public static final String[] PRIMITIVE_TYPES = array(
		"/bool", 
		"/byte", "/ubyte", "/short", "/ushort", "/int", "/uint", "/long", "/ulong", 
		"/char", "/wchar", "/dchar", 
		"/float", "/double", "/real", 
		"/void", 
		"/ifloat", "/idouble", "/ireal", "/cfloat", "/cdouble", "/creal"
	);
	
	/* ----------------- ----------------- */
	
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
	
	public static ArrayList<String> getMemberNames2(BuiltinTypeElement intrinsicDefUnit) {
		ArrayList<String> names = new ArrayList<>();
		for (INamedElement defUnit : intrinsicDefUnit.getMembersScope().getMembersIterable()) {
			names.add(defUnit.getName());
		}
		return names;
	}
	
	@Override
	public void test_NamedElement________() throws Exception {
		test_NamedElement_Type(pickedNative(D2_063_intrinsics.bool_type), COMMON_PROPERTIES);
		testExpressionResolution_expSource("true", COMMON_PROPERTIES);
		
		test_NamedElement_Type(pickedNative(D2_063_intrinsics.byte_type), INT_PROPERTIES);
		test_NamedElement_Type(pickedNative(D2_063_intrinsics.int_type), INT_PROPERTIES);
		testExpressionResolution_expSource("123", INT_PROPERTIES);
		
		test_resolveSearchInMembersScope(pickedNative(D2_063_intrinsics.char_type), INT_PROPERTIES);
		testExpressionResolution_expSource("'c'", INT_PROPERTIES);
		
		test_NamedElement_Type(pickedNative(D2_063_intrinsics.float_type), FLOAT_PROPERTIES);
		testExpressionResolution_expSource("123.123", FLOAT_PROPERTIES);
		testExpressionResolution_expSource("123.123f", FLOAT_PROPERTIES);
				
		
		test_NamedElement_Type(pickedNative(D2_063_intrinsics.pointerType), null /* TODO */);
		test_NamedElement_Type(pickedNative(D2_063_intrinsics.dynArrayType), DYN_ARRAY_PROPERTIES);
		test_NamedElement_Type(pickedNative(D2_063_intrinsics.staticArrayType), STATIC_ARRAY_PROPERTIES);
		
		// TODO: test the intrinsic properties as well
	}
	
	protected static void testExpressionResolution_expSource(String expSource, String... expectedResults) {
		PickedElement<Expression> exp = parseElement("auto _ = " + expSource + "/*M*/", "/*M*/", Expression.class);
		testExpressionResolution(exp, expectedResults);
	}
	
}