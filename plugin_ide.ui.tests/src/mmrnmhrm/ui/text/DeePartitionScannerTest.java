/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.downCast;
import melnorme.utilbox.core.DevelopmentCodeMarkers;
import melnorme.utilbox.tests.CommonTest;
import mmrnmhrm.core.text.DeePartitions;

import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.junit.Test;

// BM: a lot more could done in terms of test, this is just basic example
public class DeePartitionScannerTest extends CommonTest implements DeePartitions {
	
	private static final String NL = "\n";
	private Document document;
	private FastPartitioner fp;
	private boolean recreateDocSetup = true;
	
	public void testBasic() throws Exception {
		testPartitions("foo = \"asdf\"; ", array(DEE_STRING));
	}
	
	private void testPartitions(String source, String... expectedPositions) throws BadPositionCategoryException {
		Position[] positions = calculatePositions(source);
		checkPositions(positions, expectedPositions);
	}
	
	private Position[] calculatePositions(String docContents) throws BadPositionCategoryException {
		setupDocument(docContents);
		Position[] positions = getPartitionPositions();
		return positions;
	}
	private Position[] getPartitionPositions() throws BadPositionCategoryException {
		return document.getPositions(fp.getManagingPositionCategories()[0]);
	}
	private void setupDocument(String docContents) {
		if(recreateDocSetup){ 
			document = new Document(docContents);
			fp = DeeTextTestUtils.installDeePartitioner(document);
		} else {
			document.set(docContents);
		}
	}
	
	private void installPartitionerOnDocumentWithTextTools() {
		DeeTextTools textTools = new DeeTextTools(true);
		textTools.setupDefaultDocumentPartitioner(document);
		fp = (FastPartitioner) document.getDocumentPartitioner();
	}
	
	protected void checkPositions(Position[] positions, String[] expectedPositions) {
		assertTrue(positions.length == expectedPositions.length);
		for (int i = 0; i < positions.length; i++) {
			TypedPosition position = downCast(positions[i], TypedPosition.class);
			assertTrue(position.getType() == expectedPositions[i]);
		}
	}
	
	@Test
	public void testString() throws Exception { testString$(); }
	public void testString$() throws Exception {
		testPartitions("foo = \"foo\"; ", array(DEE_STRING));
		testPartitions("foo = \"foo\\\"bar\"; ", array(DEE_STRING));
		testPartitions("foo = \"foo\" ~ \"abc\"; ", array(DEE_STRING, DEE_STRING));

		testPartitions("foo = \"foo \n", array(DEE_STRING)); // incomplete string
		testPartitions("foo = \"foo ", array(DEE_STRING)); // incomplete string
		testPartitions("foo = \"as\0\"df\"; /+ +/", array(DEE_STRING, DEE_STRING));
		testPartitions("foo = \"as \n df\"; /+ +/", array(DEE_STRING, DEE_NESTED_COMMENT));
		
		testPartitions("foo = x\"foo\"; ", array(DEE_STRING));
	}
	
	@Test
	public void testRawString() throws Exception { testRawString$(); }
	public void testRawString$() throws Exception {
		testPartitions("foo = `asdf`; ", array(DEE_RAW_STRING));
		testPartitions("foo = `as\\`df`; /+ +/", array(DEE_RAW_STRING, DEE_RAW_STRING));
		testPartitions("foo = `foo` ~ `abc`; ", array(DEE_RAW_STRING, DEE_RAW_STRING));
		
		testPartitions("foo = `foo \n", array(DEE_RAW_STRING)); // incomplete string
		testPartitions("foo = `foo ", array(DEE_RAW_STRING)); // incomplete string
		testPartitions("foo = `as\0`df`; /+ +/", array(DEE_RAW_STRING, DEE_RAW_STRING));
		testPartitions("foo = `as \n df`; /+ +/", array(DEE_RAW_STRING, DEE_NESTED_COMMENT));
		
		
		testPartitions("foo = r\"asdf\"; ", array(DEE_RAW_STRING2));
		testPartitions("foo = r\"as\\\"df\"; /+ +/", array(DEE_RAW_STRING2, DEE_STRING));
		testPartitions("foo = r\"foo\" ~ r\"abc\"; ", array(DEE_RAW_STRING2, DEE_RAW_STRING2));
		
		testPartitions("foo = r\"foo \n", array(DEE_RAW_STRING2)); // incomplete string
		testPartitions("foo = r\"foo ", array(DEE_RAW_STRING2)); // incomplete string
		testPartitions("foo = r"); // incomplete string start - Regression test
		testPartitions("foo = r\"as\0\"df\"; /+ +/", array(DEE_RAW_STRING2, DEE_STRING));
		testPartitions("foo = r\"as \n df\"; /+ +/", array(DEE_RAW_STRING2, DEE_NESTED_COMMENT));
	}
	
	@Test
	public void testDelimitedStrings() throws Exception { testDelimitedStrings$(); }
	public void testDelimitedStrings$() throws Exception {
		testPartitions("foo = q\"(foo(xxx))\"; /+ +/", array(DEE_DELIM_STRING, DEE_NESTED_COMMENT));
		testPartitions("foo = q\"(foo(xx\nx))\"; /+ +/", array(DEE_DELIM_STRING, DEE_NESTED_COMMENT));
		testPartitions("foo = q"); // incomplete string start - Regression test
		
		if(!DevelopmentCodeMarkers.UNIMPLEMENTED_FUNCTIONALITY) return;
		
		testPartitions("foo = q\"(foo(xx\"x))\"; /+ +/", array(DEE_DELIM_STRING, DEE_NESTED_COMMENT));
		
		String src = 
			"writefln(q\"EOS"+NL+ 
			"This is a \"  EOS\"multi-line heredoc string"+NL+
			"EOS\""+NL+
			"); /+ +/";
		testPartitions(src, array(DEE_DELIM_STRING, DEE_NESTED_COMMENT));
	}
	
	@Test
	public void testSLComments() throws Exception { testSLComments$(); }
	public void testSLComments$() throws Exception {
		testPartitions("a = 1; // blah \n foo;", array(DEE_SINGLE_COMMENT));
		testPartitions("a = 1  // blah ", array(DEE_SINGLE_COMMENT));
		testPartitions("a = 1; /// blah \n foo;", array(DEE_SINGLE_DOCCOMMENT));
		testPartitions("a = 1  /// blah ", array(DEE_SINGLE_DOCCOMMENT));
		 
	}
	
	@Test
	public void testComments() throws Exception { testComments$(); }
	public void testComments$() throws Exception {
		testComments_Common("/+", "+/", DEE_NESTED_COMMENT);
		testComments_Common("/++", "+/", DEE_NESTED_DOCCOMMENT);
		testComments_Common("/*", "*/", DEE_MULTI_COMMENT);
		testComments_Common("/**", "*/", DEE_MULTI_DOCCOMMENT);
		
//		testPartitions("a = /++/ special degenerate case", array(DEE_NESTED_COMMENT)); TODO
//		testPartitions("a = /**/ special degenerate case", array(DEE_NESTED_COMMENT)); TODO
		
		testPartitions("foo = /`asdf`; ", array(DEE_RAW_STRING));
		// Test nesting
		Position[] positions1 = calculatePositions("a =  /+ /+ blah   +/  'a +/ 1;");
		checkPositions(positions1, array(DEE_NESTED_COMMENT));
		assertTrue(positions1[0].length == 22);
		Position[] positions2 = calculatePositions("a =  /+ /++ bla \n +/ \n +/ 1;");
		checkPositions(positions2, array(DEE_NESTED_COMMENT));
		assertTrue(positions2[0].length == 22 - 2);
	}

	public void testComments_Common(String START, String CLOSE, String tokenType) throws BadPositionCategoryException {
		testPartitions("a /");
		testPartitions("a "+START+"", tokenType);
		
		testPartitions("a/ `S` "+START+" // "+CLOSE+" 1; ", DEE_RAW_STRING, tokenType);
		testPartitions("a/ `S` "+START+" blah * + \n `asd`", DEE_RAW_STRING, tokenType); // EOL Unterminated
		testPartitions("a/ `S` "+START+" blah * +    `asd`", DEE_RAW_STRING, tokenType); // EOF Unterminated
		testPartitions("a/ `S` "+START+" //   "+CLOSE+" `asd`", DEE_RAW_STRING, tokenType, DEE_RAW_STRING);
		
		testPartitions("a/ `S` "+START+" * + \""+CLOSE+"\" "+CLOSE+" 1\"; ", DEE_RAW_STRING, tokenType, DEE_STRING);
	}
	
	@Test
	public void testAll_withSamePartitioner() throws Exception { testAll_withSamePartitioner$(); }
	public void testAll_withSamePartitioner$() throws Exception {
		setupDocument("");
		recreateDocSetup = false;
		
		testString();
		testRawString();
		testSLComments();
		testComments();
	}
	
	@Test
	public void testAll_withTextTools() throws Exception { testAll_withTextTools$(); }
	public void testAll_withTextTools$() throws Exception {
		document = new Document("");
		installPartitionerOnDocumentWithTextTools();
		recreateDocSetup = false;
		
		testAll_withSamePartitioner();
	}
	
}
