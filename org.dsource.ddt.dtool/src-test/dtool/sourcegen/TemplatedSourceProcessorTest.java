/*******************************************************************************
 * Copyright (c) 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package dtool.sourcegen;

import org.junit.Test;

import dtool.sourcegen.AnnotatedSource.MetadataEntry;

/* language features: ---------------------------------------------

#:SPLIT blah blah -----------------
text
#:HEADER ---------------------------
header2
━━━━━━━━━━━━━━━━━━━━━━━━ other split syntax
text
━━━━━━━━━━━━━━━━━━━━━━━━ →◙
text with custom marker: ◙◙
Ⓗ━━━━━━━━━━━━━━━━━━━━━━━━ other header syntax
header1
▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Metadata:
#metadata(value,value,value){associatedSource}
#metadata(value,value,value)¤【sourceValueNotIncluded】

#metadata(value,value,value)《
	associated Source
	associated Source
》 

#metadata_endlineFormat(value,value,value):
  blah blah
#:END:

#metadata(value,value,value){not part of MD}

▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃  Expansion
#@expansion(refIdentifier) 《
	►arg1●
	►arg2●
	►arg3●
¤》 // There is no arg4

#@EXP_ID•SOURCE_NOT_PART_OF_EXP_ID 	 
▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ other:
#:DISCARD_CASE
 
 */
public class TemplatedSourceProcessorTest extends TemplatedSourceProcessorCommonTest {
	
	
	/* ------------------------  SPLIT  ------------------------ */
	
	@Test
	public void testSplit() throws Exception { testSplit$(); }
	public void testSplit$() throws Exception {
		for (String splitMarker : array("#:SPLIT", "━━", "▂▂", "▃▃")) {
			testSplit(splitMarker);
		}
		
		for (String headerMarker : array("#:HEADER", "Ⓗ━━", "Ⓗ▃▃")) {
			testHeaderSplit(headerMarker, "#:SPLIT", "━━");
		}
	}
	
	public void testSplit(String splitMarker) {
		testSourceProcessing("#", 
			splitMarker+" ___________________\ncase1\nasdfasdf"+
			splitMarker+" comment\ncase ##2\nblahblah\n#:SPLIT comment\r\n"+ 
			splitMarker+"\n case ##:3\nblahblah\n"
			,
			checkMD("case1\nasdfasdf"),
			checkMD("case #2\nblahblah\n"),
			checkMD(""),
			checkMD(" case #:3\nblahblah\n")
		);
		
		
		testSourceProcessing("#", 
			"case ##1\nasdfasdf"+
				splitMarker+" comment\ncase ##2\nblahblah\n"
			,
			checkMD("case #1\nasdfasdf"),
			checkMD("case #2\nblahblah\n")
		);
		
		testSourceProcessing("#", 
			splitMarker+" _____\ncase1\na#=XPLIT sdfasdf"+
			splitMarker+"\n case3\nblahblah\n"
			,
			8
		);
		
		// Test new key marker
		testSourceProcessing("#", 
			splitMarker+" ___________________→◙\ncase1:#NOTMD blah ◙MD"+
			splitMarker+" comment\ncase2:#MD blah ◙NOTMD#:SPLIT comment\r\n"+ 
			splitMarker+"→X blah\n case#3:\nblahblahXMD\n"
			,
			checkMD("case1:#NOTMD blah ", new MetadataEntry("MD", null, null, 18)),
			checkMD("case2: blah ◙NOTMD", new MetadataEntry("MD", null, null, 6)),
			checkMD(""),
			checkMD(" case#3:\nblahblah\n", new MetadataEntry("MD", null, null, 17))
		);
	}
	
	public void testHeaderSplit(String headerMarker, String splitMarker1, String splitMarker2) {
		testSourceProcessing("#", 
			headerMarker+" ___________________\ncase1\nasdfasdf"+
			splitMarker1+" comment\ncase ##2\ncase2.\n#:SPLIT comment\r\n"+ 
			headerMarker+" comment\ncase ##4\nblahblah\n"+splitMarker2+"comment2\r\ncase5"+ 
			splitMarker1+"\n case ##:6\nxxxxxxx\n"
			,
			checkMD("case #2\ncase2.\n"),
			checkMD(""),
			checkMD("case5"),
			checkMD(" case #:6\nxxxxxxx\n")
		);
	}
	
	/* ------------------------  METADATA  ------------------------ */
	
	@Test
	public void testMetadata() throws Exception { testMetadata$(); }
	public void testMetadata$() throws Exception {
		testSourceProcessing("#", "foo1 ## #error_EXP(asfd,3,4){xxx}==",
			checkMD("foo1 # xxx==", new MetadataEntry("error_EXP", "asfd,3,4", "xxx", 7))
		);
		
		testSourceProcessing("#", 
			"asdf ## #error(info1)==", 
			checkMD("asdf # ==", new MetadataEntry("error", "info1", null, 7))
		);
		
		testSourceProcessing("#", 
			"asdf ## #error==",
			checkMD("asdf # ==", new MetadataEntry("error", null, null, 7))
		);
		
		testSourceProcessing("#", 
			"asdf ## #error{xxx}==",
			checkMD("asdf # xxx==", new MetadataEntry("error", null, "xxx", 7))
		);
		// Source not included
		testSourceProcessing("#", 
			"asdf ## #error¤{xxx}==",
			checkMD("asdf # ==", new MetadataEntry("error", null, "xxx", 7))
		);		
		testSourceProcessing("#", "badsyntax #foo()¤", 17);
		testSourceProcessing("#", "badsyntax #fooxx¤", 17);
		
		testSourceProcessing("#", 
			"foo1 ## #error_EXP:asfd_ad{xxx}==",
			checkMD("foo1 # xxx==", new MetadataEntry("error_EXP", "asfd_ad", "xxx", 7))
		);
		testSourceProcessing("#", 
			"asdf ## #error:info1==", 
			checkMD("asdf # ==", new MetadataEntry("error", "info1", null, 7))
		);
		
		// Syntax errors
		testSourceProcessing("#", "badsyntax #foo(=={", 18);
		testSourceProcessing("#", "badsyntax #foo(==){asdf", 18+5);
		
		testSourceProcessing("#", "badsyntax #foo(==#:SPLIT\n)", 17);
		testSourceProcessing("#", "badsyntax #foo(==#:END:", 18);
		testSourceProcessing("#", "badsyntax #foo(){xxx#:SPLIT\n)", 17+3);
		testSourceProcessing("#", "badsyntax #foo(){xxx#:END:", 17+3+2+3);
		
		
		for (int i = 0; i < TemplatedSourceProcessor.OPEN_DELIMS.length; i++) {
			String open = TemplatedSourceProcessor.OPEN_DELIMS[i];
			String close = TemplatedSourceProcessor.CLOSE_DELIMS[i];
			if(open.equals("{")) 
				continue;
			
			testSourceProcessing("#", prepString("asdf #foo(arg)►,}◙► #◄,xxx}◄==", open, close),
				
				checkMD(prepString("asdf ,}◙► ◄,xxx}==", open, close), 
					new MetadataEntry("foo", "arg", prepString(",}◙► ◄,xxx}", open, close), 5))
			);
		}
		
		
		//multineLine MD syntax
		
		testSourceProcessing("#", 
			"multilineMD #error(arg1,arg2,arg3): blah",
			
			checkMD("multilineMD ", new MetadataEntry("error", "arg1,arg2,arg3", " blah", 12))
		);
		
		// boundary
		testSourceProcessing("#", 
			"multilineMD---#error(arg1,arg2,arg3):\n",
			
			checkMD("multilineMD---", new MetadataEntry("error", "arg1,arg2,arg3", "", 14))
		);
		
		// #:END: delim
		testSourceProcessing("#", 
			"multilineMD #error(arg1,arg2,arg3):\n line1\nline2\nline3\n#:END:lineOther4\n",
			
			checkMD("multilineMD lineOther4\n", 
				new MetadataEntry("error", "arg1,arg2,arg3", " line1\nline2\nline3\n", 12))
		);
		testSourceProcessing("#", 
			"multilineMD #error(arg1,arg2,arg3):line0\nline1\nline2\nline3#:END:afterEnd\nlineOther4\n",
			
			checkMD("multilineMD afterEnd\nlineOther4\n", 
				new MetadataEntry("error", "arg1,arg2,arg3", "line0\nline1\nline2\nline3", 12))
		);
		
		// split interaction
		testSourceProcessing("#", 
			"multilineMD #error(arg1,arg2,arg3):\n line1\nline2\nline3\n#:SPLIT:\nlineOther4\n",
			
			checkMD("multilineMD ", 
				new MetadataEntry("error", "arg1,arg2,arg3", " line1\nline2\nline3\n", 12)),
			checkMD("lineOther4\n")
		);
		
		// nested MDs
		testSourceProcessing("#", 
			"xxx#multiline1:\n xxxxA\n#multiline2:\n xxB\n#tag(arg1) blah2-cont",
			
			checkMD("xxx", 
				new MetadataEntry("multiline1", null, " xxxxA\n", 3),
				new MetadataEntry("multiline2", null, " xxB\n blah2-cont", -1),
				new MetadataEntry("tag", "arg1", null, -1))
		);
		
		// All together
		testSourceProcessing("#", 
			"foo1 ## #error_EXP(asdf,3,4){xxx}=="+
			"asdf ## #error(info1)=="+
			"asdf ## #error=="+
			"asdf ## #error{xxx}=="+
			"multilineMD #error(arg1,arg2,arg3):\n line1\nline2#tagInMD(blah){xxx}\nline3\n#:END:lineOther4\n",
			
			checkMD(
				"foo1 # xxx=="+
				"asdf # =="+
				"asdf # =="+
				"asdf # xxx=="+
				"multilineMD lineOther4\n",
				new MetadataEntry("error_EXP", "asdf,3,4", "xxx", 7),
				new MetadataEntry("error", "info1", null, 7 +5+7),
				new MetadataEntry("error", null, null, 7 +5+7 +2+7),
				new MetadataEntry("error", null, "xxx", 7 +5+7 +2+7 +2+7),
				new MetadataEntry("error", "arg1,arg2,arg3", " line1\nline2xxx\nline3\n", 7 +5+7 +2+7 +2+7 +3+2+12),
				new MetadataEntry("tagInMD", "blah", "xxx", -1)
			)
		);
	}
	
	
}