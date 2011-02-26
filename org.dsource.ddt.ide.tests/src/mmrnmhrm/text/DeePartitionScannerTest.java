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
package mmrnmhrm.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.downCast;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.DeeTextTestUtils;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.junit.Test;

import dtool.DToolBundle;
import dtool.tests.DeeTestUtils;

// BM: a lot more could done in terms of test, this is just basic example
public class DeePartitionScannerTest extends DeeTestUtils implements DeePartitions {
	
	private static final String NL = "\n";
	private Document document;
	private FastPartitioner fp;
	private boolean recreateDocSetup = true;
	
	public void testBasic() throws Exception {
		testPartitions("foo = \"asdf\"; ", array(DEE_STRING));
	}
	
	private void testPartitions(String source, String[] expectedPositions) throws BadPositionCategoryException {
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
		ScriptTextTools textTools = new DeeTextTools(true);
		textTools.setupDefaultDocumentPartitioner(document);
		fp = (FastPartitioner) document.getDocumentPartitioner();
	}
	
	private void checkPositions(Position[] positions, String[] expectedPositions) {
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
		
		
		testPartitions("foo = r\"asdf\"; ", array(DEE_RAW_STRING));
		testPartitions("foo = r\"as\\\"df\"; /+ +/", array(DEE_RAW_STRING, DEE_STRING));
		testPartitions("foo = r\"foo\" ~ r\"abc\"; ", array(DEE_RAW_STRING, DEE_RAW_STRING));
		
		testPartitions("foo = r\"foo \n", array(DEE_RAW_STRING)); // incomplete string
		testPartitions("foo = r\"foo ", array(DEE_RAW_STRING)); // incomplete string
		testPartitions("foo = r\"as\0\"df\"; /+ +/", array(DEE_RAW_STRING, DEE_STRING));
		testPartitions("foo = r\"as \n df\"; /+ +/", array(DEE_RAW_STRING, DEE_NESTED_COMMENT));
	}
	
	@Test
	public void testDelimitedStrings() throws Exception { testDelimitedStrings$(); }
	public void testDelimitedStrings$() throws Exception {
		testPartitions("foo = q\"(foo(xxx))\"; /+ +/", array(DEE_DELIM_STRING, DEE_NESTED_COMMENT));
		testPartitions("foo = q\"(foo(xx\nx))\"; /+ +/", array(DEE_DELIM_STRING, DEE_NESTED_COMMENT));
		if(!DToolBundle.UNSUPPORTED_DMD_FUNCTIONALITY$) return;
		
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
		testPartitions("a = /+ // +/ 1;       ", array(DEE_NESTED_COMMENT));
		testPartitions("a = /+ blah         //",array(DEE_NESTED_COMMENT));
		testPartitions("a = /+ blah \n blah   ", array(DEE_NESTED_COMMENT));
		testPartitions("a = /+ /* +/ */ 3;  //", array(DEE_NESTED_COMMENT, DEE_SINGLE_COMMENT));
		testPartitions("a = /+ \"+/\" +/ 1\"; ", array(DEE_NESTED_COMMENT, DEE_STRING));

		testPartitions("a = /++ blah +/ 1;", array(DEE_NESTED_DOCCOMMENT));
		testPartitions("a = /++ //   +/ 'a' `d`", array(DEE_NESTED_DOCCOMMENT, DEE_CHARACTER, DEE_RAW_STRING));
		
		testPartitions("foo = /`asdf`; ", array(DEE_RAW_STRING));
		// Test nesting
		Position[] positions1 = calculatePositions("a =  /+ /+ blah   +/  'a +/ 1;");
		checkPositions(positions1, array(DEE_NESTED_COMMENT));
		assertTrue(positions1[0].length == 22);
		Position[] positions2 = calculatePositions("a =  /+ /++ bla \n +/ \n +/ 1;");
		checkPositions(positions2, array(DEE_NESTED_COMMENT));
		assertTrue(positions2[0].length == 22 - 2);
	}
	
	@Test
	public void testAll_withSamePartitioner() throws Exception { testAll_withSamePartitioner$(); }
	public void testAll_withSamePartitioner$() throws Exception {
		setupDocument("");
		recreateDocSetup = false;
		
		testPartitions("a = /+ blah         ", array(DEE_NESTED_COMMENT));
		testPartitions("a = /+ blah \n blah ", array(DEE_NESTED_COMMENT));
		testPartitions("a = /+ // +/ 'a' `d`", array(DEE_NESTED_COMMENT, DEE_CHARACTER, DEE_RAW_STRING));

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
