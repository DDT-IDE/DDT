/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser.structure;

import static melnorme.lang.tooling.structure.StructureElementKind.ALIAS;
import static melnorme.lang.tooling.structure.StructureElementKind.CLASS;
import static melnorme.lang.tooling.structure.StructureElementKind.CONSTRUCTOR;
import static melnorme.lang.tooling.structure.StructureElementKind.FUNCTION;
import static melnorme.lang.tooling.structure.StructureElementKind.STRUCT;
import static melnorme.lang.tooling.structure.StructureElementKind.VARIABLE;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tests.CommonToolingTest;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.structure.IStructureElement;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.lang.tooling.structure.StructureElement;
import melnorme.lang.tooling.structure.StructureElementData;
import melnorme.lang.tooling.structure.StructureElementKind;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.misc.Location;

import org.junit.Test;

import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.tests.DToolTestResources;

public class DeeStructureCreator_Test extends CommonToolingTest {
	
	protected StructureElementData ed() {
		return new StructureElementData();
	}
	
	protected SourceRange sr(int offset, int length) {
		return new SourceRange(offset, length);
	}
	
	protected String testSource;
	
	protected int indexOf(String str) {
		int indexOf = testSource.indexOf(str);
		assertTrue(indexOf != -1);
		return indexOf;
	}
	
	protected int startOf(String str) {
		return indexOf(str);
	}
	
	protected int endOf(String str) {
		return indexOf(str) + str.length();
	}
	
	protected SourceRange sr(String start, String end) {
		return SourceRange.srStartToEnd(startOf(start), endOf(end));
	}
	
	protected SourceRange sr(String start, int length) {
		return new SourceRange(startOf(start), length);
	}
	
	protected void testParseStructure(String source, IStructureElement... expectedElements) {
		// dummy loc
		Location loc = DToolTestResources.getTestResourceLoc().resolve_fromValid("parser-structure/foo.d");
		ParsedModule parsedModule = DeeParser.parseSourceModule(source, loc.toPath());
		SourceFileStructure structure = new DeeStructureCreator().createStructure(loc, parsedModule);
		
		ArrayList2<IStructureElement> expectedStructure = new ArrayList2<>(expectedElements);
		SourceFileStructure expected = new SourceFileStructure(loc, expectedStructure);
		assertAreEqualLists(expected.getChildren(), structure.getChildren());
		
		assertEquals(structure, expected);
	}
	
	protected ArrayList2<StructureElement> elems(StructureElement... expectedElements) {
		return new ArrayList2<>(expectedElements);
	}
	
	@Test
	public void testname() throws Exception { testname$(); }
	public void testname$() throws Exception {
		testSource = "int foo; string[] func() {/*2*/} this() {/*3*/}";
		testParseStructure(
			testSource, 
			new StructureElement("foo", sr(4,3), sr(0,8), VARIABLE, ed(), "int", null),
			new StructureElement("func", sr("func()", 4), sr("string[] f","2*/}"), FUNCTION, ed(), "string[]", null),
			new StructureElement("this", sr("this()", 4), sr("this()","3*/}"), CONSTRUCTOR, ed(), null, null)
		);
		
		
		testSource = 
			"struct Xpto { int foo2; void func(int a){}  /*Xpto*/} \n " +
			"class Foo { int fox;  class Inner(T) { int xxx; }  /*Foo*/} \n ";
		testParseStructure(
			testSource, 
			new StructureElement("Xpto", sr("Xpto", 4), sr("struct Xpto","/*Xpto*/}"), STRUCT, ed(), null, elems(
				new StructureElement("foo2", sr("foo2;", 4), sr("int foo2;","foo2;"), VARIABLE, ed(), "int", null),
				new StructureElement("func", sr("func", 4), sr("void func","a){}"), FUNCTION, ed(), "void", null)
			)),
			new StructureElement("Foo", sr("Foo", 3), sr("class F","/*Foo*/}"), CLASS, ed(), null, elems(
				new StructureElement("fox", sr("fox;", 3), sr("int fox;","fox;"), VARIABLE, ed(), "int", null),
				new StructureElement("Inner", sr("Inner", 5), sr("class In","xxx; }"), CLASS, ed(), null, elems(
					new StructureElement("xxx", sr("xxx;", 3), sr("int xxx","xxx;"), VARIABLE, ed(), "int", null)
				))
			))
		);
		
		// Test attributes TODO
		testSource = "public: static const(int) foo; private immutable(int) bar; ";
		testParseStructure(
			testSource, 
			new StructureElement("foo", sr("foo", 3), sr("const(","foo;"), VARIABLE, ed(), "const(int)", null),
			new StructureElement("bar", sr("bar", 3), sr("immutable(","bar;"), VARIABLE, ed(), "immutable(int)", null)
		);
		
		// Test multiple var decl.
		testSource = "static int foo, bar; ";
		testParseStructure(
			testSource, 
			new StructureElement("foo", sr("foo", 3), sr("int foo","bar;"), VARIABLE, ed(), "int", elems(
				new StructureElement("bar", sr("bar", 3), sr("bar;","bar"), VARIABLE, ed(), "int", null)
			)
		));
		
		// Test enum.
		testSource = "enum Foo : int { ONE, TWO }";
		testParseStructure(
			testSource, 
			new StructureElement("Foo", sr("Foo", 3), sr("enum","}"), StructureElementKind.ENUM, ed(), "int", elems(
				new StructureElement("ONE", sr("ONE", 3), sr("ONE","ONE"), VARIABLE, ed(), null, null),
				new StructureElement("TWO", sr("TWO", 3), sr("TWO","TWO"), VARIABLE, ed(), null, null)
			))
		);
		testSource = "enum Foo = 123, Bar = 'abc'";
		testParseStructure(
			testSource, 
			new StructureElement("Foo", sr("Foo", 3), sr("Foo","123"), VARIABLE, ed(), null, null),
			new StructureElement("Bar", sr("Bar", 3), sr("Bar","'abc'"), VARIABLE, ed(), null, null)
		);
		
		
		// Test aliases:
		testSource = "alias Foo = Bar;";
		testParseStructure(
			testSource, 
			new StructureElement("Foo", sr("Foo", 3), sr("Foo","Bar"), ALIAS, ed(), "Bar", null)
		);
		
		testSource = "alias Foo = Bar, XXX = XPTO;";
		testParseStructure(
			testSource, 
			new StructureElement("Foo", sr("Foo", 3), sr("Foo","Bar"), ALIAS, ed(), "Bar", null),
			new StructureElement("XXX", sr("XXX", 3), sr("XXX","XPTO"), ALIAS, ed(), "XPTO", null)
		);
		
	}
	
}