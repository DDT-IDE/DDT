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

import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

public class DefAggregate_SemanticsTest extends DefElement_CommonTest {
	
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
	
	@Override
	public void test_resolveTypeForValueContext________() throws Exception {
		test_resolveTypeForValueContext("class XXX {} ", "XXX", true);
		test_resolveTypeForValueContext("interface XXX {} ", "XXX", true);
		test_resolveTypeForValueContext("struct XXX {} ", "XXX", true);
		test_resolveTypeForValueContext("union XXX {} ", "XXX", true);
		
		test_resolveTypeForValueContext("enum XXX {} ", "XXX", true);
	}
	
}