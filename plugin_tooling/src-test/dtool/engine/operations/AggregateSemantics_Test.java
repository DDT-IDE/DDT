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

import static dtool.engine.operations.ExpLiteralSemantics_Test.COMMON_PROPERTIES;
import static dtool.util.NewUtils.getSingleElementOrNull;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Expression;
import dtool.engine.modules.NullModuleResolver;

public class AggregateSemantics_Test extends CommonNodeSemanticsTest {
	
	protected static final String[] OBJECT_PROPERTIES = ArrayUtil.concat(COMMON_PROPERTIES,
		"classinfo"
	);
	@Test
	public void testCompletionSearch() throws Exception { testCompletionSearch$(); }
	public void testCompletionSearch$() throws Exception {
		testExpressionResolution("class Foo {} ; Foo foo; auto _ = foo/*X*/;", OBJECT_PROPERTIES);
		testExpressionResolution("interface Foo {} ; Foo foo; auto _ = foo/*X*/;", OBJECT_PROPERTIES);
		testExpressionResolution("struct Foo {} ; Foo foo; auto _ = foo/*X*/;", COMMON_PROPERTIES);
		testExpressionResolution("union Foo {} ; Foo foo; auto _ = foo/*X*/;", COMMON_PROPERTIES);
		
		testExpressionResolution("enum Foo {} ; Foo foo; auto _ = foo/*X*/;", COMMON_PROPERTIES);
	}
	
	protected void testExpressionResolution(String source, String... expectedResults) {
		Expression exp = parseSourceAndPickNode(source, source.indexOf("/*X*/"), Expression.class);
		INamedElement expType = getSingleElementOrNull(exp.resolveTypeOfUnderlyingValue(new NullModuleResolver()));
		
		testResolveSearchInMembersScope(expType, expectedResults);
	}
	
}