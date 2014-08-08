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
package dtool.engine.operations;

import static dtool.resolver.LanguageIntrinsics.D2_063_intrinsics;
import static dtool.util.NewUtils.getSingleElementOrNull;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.IntrinsicDefUnit;
import dtool.ast.expressions.Expression;
import dtool.engine.modules.NullModuleResolver;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.resolver.LanguageIntrinsics.PrimitiveDefUnit;

public class ExpLiteralSemantics_Test extends CommonNodeSemanticsTest {
	
	protected static final String[] COMMON_PROPERTIES = array(
		"init", "sizeof", "alignof", "mangleof", "stringof"
	);
	protected static final String[] INT_PROPERTIES = ArrayUtil.concat(COMMON_PROPERTIES,
		"max", "min"
	);
	protected static final String[] FLOAT_PROPERTIES = ArrayUtil.concat(COMMON_PROPERTIES,
		ArrayUtil.createFrom(getMemberNames(D2_063_intrinsics.float_type), String.class)
	);
	
	public static ArrayList<String> getMemberNames(PrimitiveDefUnit primitiveDefUnit) {
		ArrayList<String> names = new ArrayList<>();
		for (IntrinsicDefUnit defUnit : primitiveDefUnit.getMembers()) {
			names.add(defUnit.getName());
		}
		return names;
	}
	
	@Test
	public void testCompletionSearch() throws Exception { testCompletionSearch$(); }
	public void testCompletionSearch$() throws Exception {
		testResolveSearchInMembersScope(D2_063_intrinsics.bool_type, COMMON_PROPERTIES);
		testExpressionResolution("true", COMMON_PROPERTIES);
		
		testResolveSearchInMembersScope(D2_063_intrinsics.integral_type, INT_PROPERTIES);
		testExpressionResolution("123", INT_PROPERTIES);
		
		testResolveSearchInMembersScope(D2_063_intrinsics.float_type, FLOAT_PROPERTIES);
		testExpressionResolution("123.123", FLOAT_PROPERTIES);
	}
	
	protected void testExpressionResolution(String source, String... expectedResults) {
		Expression exp = new DeeTestsChecksParser(source).parseExpression().getNode();
		INamedElement expType = getSingleElementOrNull(exp.resolveTypeOfUnderlyingValue(new NullModuleResolver()));
		
		testResolveSearchInMembersScope(expType, expectedResults);
	}
	
}