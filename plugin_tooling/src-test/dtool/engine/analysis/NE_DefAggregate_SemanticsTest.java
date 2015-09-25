/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis;

import static melnorme.utilbox.misc.ArrayUtil.concat;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

public class NE_DefAggregate_SemanticsTest extends NamedElement_CommonTest {
	
	protected static final String[] OBJECT_PROPERTIES = ArrayUtil.concat(COMMON_PROPERTIES,
		"classinfo"
	);
	
	@Override
	public void test_NamedElement________() throws Exception {
		
		test_NamedElement_Type(parseTypeElement("struct XXX { int x, y; } "), 
			concat(COMMON_PROPERTIES, "x", "y"));
		test_NamedElement_Type(parseTypeElement("union XXX { int x, y; } "), 
			concat(COMMON_PROPERTIES, "x", "y"));
		
		test_NamedElement_Type(parseTypeElement("class XXX { int x; } "), 
			concat(OBJECT_PROPERTIES, "x"));
		test_NamedElement_Type(parseTypeElement("interface XXX { int x; } "), 
			concat(OBJECT_PROPERTIES, "x"));
		
		test_NamedElement_Type(parseTypeElement("enum XXX { A, B = 2} "), 
			concat(COMMON_PROPERTIES, "A", "B"));
		
		
		// TODO: test hierarchy scopes more:
		test_resolveSearchInMembersScope(
			parseNamedElement("class Bar { int barMember; } class Foo : Bar { int x; }"),
			concat(OBJECT_PROPERTIES, "x", "barMember"));

	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void testCompletionSearch() throws Exception { testCompletionSearch$(); }
	public void testCompletionSearch$() throws Exception {
		testExpressionResolution(parseExp("class Foo {} ; Foo foo; auto _ = foo/*M*/;"), OBJECT_PROPERTIES);
		testExpressionResolution(parseExp("interface Foo {} ; Foo foo; auto _ = foo/*M*/;"), OBJECT_PROPERTIES);
		testExpressionResolution(parseExp("struct Foo {} ; Foo foo; auto _ = foo/*M*/;"), COMMON_PROPERTIES);
		testExpressionResolution(parseExp("union Foo {} ; Foo foo; auto _ = foo/*M*/;"), COMMON_PROPERTIES);
		
		testExpressionResolution(parseExp("enum Foo {} ; Foo foo; auto _ = foo/*M*/;"), COMMON_PROPERTIES);
	}
	
}