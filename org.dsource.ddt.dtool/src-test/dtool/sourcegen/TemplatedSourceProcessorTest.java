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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplateSourceProcessorParser.TemplatedSourceException;
import dtool.tests.CommonTestUtils;

public class TemplatedSourceProcessorTest extends CommonTestUtils {
	
	public static final class TestsTemplateSourceProcessor extends TemplatedSourceProcessor {
		@Override
		protected void reportError(int offset) throws TemplatedSourceException {
			assertFail();
		}
	}
	
	public void testSourceProcessing(String defaultMarker, String source, GeneratedSourceChecker... checkers) {
		TemplatedSourceProcessor tsp = new TestsTemplateSourceProcessor();
		visitContainer(tsp.processSource_unchecked(defaultMarker, source), checkers);
	}
	
	public void testSourceProcessing(String marker, String source, int errorOffset) {
		try {
			TemplatedSourceProcessor.processTemplatedSource(marker, source);
			assertFail();
		} catch(TemplatedSourceException tse) {
			assertTrue(tse.errorOffset == errorOffset);
		}
	}
	
	protected abstract class GeneratedSourceChecker implements Visitor<AnnotatedSource> {} 
	protected GeneratedSourceChecker checkMD(final String expSource, final MetadataEntry... expMetadataArray) {
		return new GeneratedSourceChecker () {
			@Override
			public void visit(AnnotatedSource genSource) {
				assertEquals(genSource.source, expSource);
				assertEquals(genSource.metadata.size(), expMetadataArray.length);
				for (int i = 0; i < expMetadataArray.length; i++) {
					checkMetadata(genSource.metadata.get(i), expMetadataArray[i]);
				}
			}
		};
	}
	
	protected GeneratedSourceChecker checkSourceOnly(final String expSource, final int mdSize) {
		return new GeneratedSourceChecker () {
			@Override
			public void visit(AnnotatedSource genSource) {
				assertEquals(genSource.source, expSource);
				assertEquals(genSource.metadata.size(), mdSize);
			}
		};
	}
	
	public static final String DONT_CHECK = new String("NO_CHECK");
	
	protected void checkMetadata(MetadataEntry mde1, MetadataEntry expMde) {
		assertAreEqual(mde1.name, expMde.name);
		assertAreEqual(mde1.value, expMde.value);
		if(expMde.sourceValue != DONT_CHECK)
			assertAreEqual(mde1.sourceValue, expMde.sourceValue);
		assertAreEqual(mde1.offset, expMde.offset);
	}
	
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
	
	/* ------------------------  EXPANSION  ------------------------ */
	
	@Test
	public void testExpansionSyntax() throws Exception { testExpansionSyntax$(); }
	public void testExpansionSyntax$() throws Exception {
		// Basic syntax, escapes
		
		testSourceProcessing("#", 
			"asdf ## #{,#},#,,##, ,line}==",
			
			checkMD("asdf # =="),checkMD("asdf # }=="), checkMD("asdf # ,=="), 
			checkMD("asdf # #=="), checkMD("asdf #  =="), checkMD("asdf # line==")
		);
		
		testSourceProcessing("#", 
			"xx #{,#},## #{a,xxx#}#,},last}==",
			
			checkMD("xx =="),checkMD("xx }=="),checkMD("xx # a=="),checkMD("xx # xxx},=="),checkMD("xx last==")
		);
		
		// Syntax errors:
		testSourceProcessing("#", "foo #@{", 7); 
		testSourceProcessing("#", "foo #@==", 6);
		testSourceProcessing("#", "foo #@!", 7);
		testSourceProcessing("#", "foo #@EXPANSION1{", 17); 
		testSourceProcessing("#", "foo #@EXPANSION1(", 17);
		testSourceProcessing("#", "foo #@EXPANSION1(EXP:", 17+3);
		testSourceProcessing("#", "foo #@EXPANSION1{12,}(", 22);
		testSourceProcessing("#", "foo #@EXPANSION1{12,}(EXP:", 22+3);
		
		testSourceProcessing("#", "> #@!(EXP2)", 5);
		
	}
	
	@Test
	public void testExpansion() throws Exception { testExpansion$(); }
	public void testExpansion$() throws Exception {
		
		/* BASIC EXPANSION FORMATS:
		A:  #@{1, 2, 3}         Unnamed-Expansion
		B:  #@EXP{1, 2, 3}      Definition, Named-Expansion  
		C:  #@EXP!{1, 2, 3}     Definition only
		
		F:  #@EXP               Named-Expansion with argument referral(EXP)
		G:  #@EXP!              NO LONGER VALID
		
		EA: #@{1, 2, 3}(EXP)    Expansion, pairing with active(EXP)
		EB: #@(EXP)             NO LONGER VALID
		EC: #@EXP2{1,2,3}(EXP)  Definition, Named-Expansion, pairing with active(EXP)
		ED: #@EXP2(EXP)         Named-Expansion with argument referral(EXP2), pairing with active(EXP)
		
		H:  #@^EXP              Unnamed-Expansion with argument referral(EXP)
		I:  #@^EXP(EXP2)        Unnamed-Expansion with argument referral(EXP), pairing with active(EXP2)
		
		*/
		
		
		
		// A: Unnamed-Expansion
		testSourceProcessing("#", 
			"foo #@{var1,var2#,,var3##}==",
			
			checkMD("foo var1=="),
			checkMD("foo var2,=="),
			checkMD("foo var3#==")
		);
		
		// B: Definition, Named-Expansion -- EA:
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,var2#,,var3##}==#@{A,B,C}(EXPANSION1)",
			
			checkMD("foo var1==A"),
			checkMD("foo var2,==B"),
			checkMD("foo var3#==C")
		);
		//Error: redefined:
		testSourceProcessing("#", "foo #@EXPANSION1{a,b} -- #@EXPANSION1{a,b}", 9); 
		testSourceProcessing("#", "foo #@EXPANSION1{a,#@EXPANSION1{a,b}}", 4);
		
		
		// == C: Definition only ==
		
		// C: Basic case 
		testSourceProcessing("#", "> #@EXPANSION1!{A,B,C} b", checkMD(">  b"));
		// needs F: to test more
		
		// F:  #@EXP               Named-Expansion with argument referral(EXP)
		testSourceProcessing("#", 
			"#@EXPANSION1!{var1,var2,var3}"+
			"#@EXPANSION1 == #@{A,B,C}(EXPANSION1)",
			
			checkMD("var1 == A"),
			checkMD("var2 == B"),
			checkMD("var3 == C")
		);
		
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,var2,var3}==#@EXP2{VAR1,VAR2,VAR3}(EXPANSION1) ||"+
			" #@EXPANSION1•X == #@EXP2",
			
			checkMD("foo var1==VAR1 || var1X == VAR1"),
			checkMD("foo var2==VAR2 || var2X == VAR2"),
			checkMD("foo var3==VAR3 || var3X == VAR3")
		);
		
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,var2,var3} == #{a,xxx} -- #@EXPANSION1",
			
			checkMD("foo var1 == a -- var1"),
			checkMD("foo var1 == xxx -- var1"),
			checkMD("foo var2 == a -- var2"),
			checkMD("foo var2 == xxx -- var2"),
			checkMD("foo var3 == a -- var3"),
			checkMD("foo var3 == xxx -- var3")
		);
		
		//Error: redefined
		testSourceProcessing("#", "foo #@EXPANSION1!{a,b} -- #@EXPANSION1{a,b}", 8);
		testSourceProcessing("#", "foo #@EXPANSION1!{a,#@EXPANSION1{a,b}} #@EXPANSION1", 5);
		//Error: no args (syntax)
		testSourceProcessing("#", "foo #@EXPANSION1! -- #@EXPANSION1{a,b}", 17);
		
		
		// ============== EA: -- EB: -- EC: -- ED ==============
		
		// EA: #@{1, 2, 3}(EXP)   Expansion, pairing with active(EXP)
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,var2,var3}==#@{A,B,C}(EXPANSION1)",
			
			checkMD("foo var1==A"),
			checkMD("foo var2==B"),
			checkMD("foo var3==C")
		);
		
		testSourceProcessing("#", 
			"foo #@EXPANSION1!{var1,var2,var3}==#@{A,B,C}(EXPANSION1)==#@EXPANSION1",
			
			checkMD("foo ==A==var1"),
			checkMD("foo ==B==var2"),
			checkMD("foo ==C==var3")
		);
		
		// EC: #@EXP2{1,2,3}(EXP)  Definition, Named-Expansion, pairing with active(EXP)
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,var2,var3}==#@EXP{A,B,C}(EXPANSION1) #@{x,y,z}(EXP)",
			
			checkMD("foo var1==A x"), 
			checkMD("foo var2==B y"),
			checkMD("foo var3==C z")
		);
		
		// Make sure both EXP and EXPANSION1 IDs are ACTIVATED
		testSourceProcessing("#", 
			"foo #@EXPANSION1!{var1,var2,var3}=>#@EXP{A,B,C}(EXPANSION1) #@{x,y,z}(EXP)--#@EXPANSION1",
			
			checkMD("foo =>A x--var1"), 
			checkMD("foo =>B y--var2"),
			checkMD("foo =>C z--var3")
		);
		
		//ED: #@EXP2(EXP)         Named-Expansion with argument referral(EXP2), pairing with active(EXP)
		testSourceProcessing("#", 
			"#@EXP1{var1,var2,var3}"+"#@EXP2!{z1,z2,z3}"+"> #@EXP2(EXP1) -- #@{A,B,C}(EXP1)",
			
			checkMD("var1> z1 -- A"),
			checkMD("var2> z2 -- B"),
			checkMD("var3> z3 -- C")
		);

		// Error: undefined ref
		testSourceProcessing("#", "> #@(EXPANSION1)", 2); 
		testSourceProcessing("#", "> #@{A,B,C}(EXPANSION1)", 2);
		testSourceProcessing("#", "> #@EXP2{A,B,C}(EXPANSION1)", 2);
		testSourceProcessing("#",                      "> #@EXP2(EXPANSION1)", 2);
		testSourceProcessing("#", "#@EXP2!{z1,z2,z3}"+ "> #@EXP2(EXPANSION1)", 2);
		testSourceProcessing("#", "> #@EXP2", 2);
		//Error: Mismatched argument count:
		testSourceProcessing("#", "> #@EXPANSION1{a,b} -- #@{a}(EXPANSION1)", 7); 
		testSourceProcessing("#", "> #@EXPANSION1{a,b} -- #@{a,b,c}(EXPANSION1)", 7);
		testSourceProcessing("#", "> #@EXPANSION1{a,b} -- #@EXP2{a,b,c}(EXPANSION1)", 7);
		testSourceProcessing("#", "#@EXP2!{a,b,c}"+ "> #@EXP2(EXPANSION1)", 2);
		
		// H:  #@^EXP              Unnamed-Expansion with argument referral(EXP)
		
		testSourceProcessing("#", "> #@^{1,2,3}", 5); // Bad syntax: no name
		testSourceProcessing("#", "> #@^EXP1!{1,2,3}", 10); // Bad syntax: has define only
		
		GeneratedSourceChecker[] expectedCasesH = array(
			checkMD("> var1 -- var1"), checkMD("> var1 -- var2"), checkMD("> var1 -- var3"),
			checkMD("> var2 -- var1"), checkMD("> var2 -- var2"), checkMD("> var2 -- var3"),
			checkMD("> var3 -- var1"), checkMD("> var3 -- var2"), checkMD("> var3 -- var3"));
		
		testSourceProcessing("#", 
			"> #@^EXP1{var1,var2,var3} -- #@EXP1", expectedCasesH
		);
		testSourceProcessing("#", 
			"> #@EXP1{var1,var2,var3} -- #@^EXP1", expectedCasesH
		);
		
		// I:  #@^EXP(EXP2)        Unnamed-Expansion with argument referral(EXP), pairing with active(EXP2)
		testSourceProcessing("#", 
			"#@EXP1!{var1,var2,var3}"+ "#@EXP2!{1,2,3}"+
			"> #@EXP1 -- #@EXP2 - #@^EXP1(EXP2)", 
			
			checkMD("> var1 -- 1 - var1"), checkMD("> var1 -- 2 - var2"), checkMD("> var1 -- 3 - var3"),
			checkMD("> var2 -- 1 - var1"), checkMD("> var2 -- 2 - var2"), checkMD("> var2 -- 3 - var3"),
			checkMD("> var3 -- 1 - var1"), checkMD("> var3 -- 2 - var2"), checkMD("> var3 -- 3 - var3")
		);
		
		testSourceProcessing("#","#@EXP1!{var1,var2,var3}"+ 
			"> #@^EXP1 -- #@{1,2,3}(EXP1)",
			
			checkMD("> var1 -- 1"), checkMD("> var1 -- 2"), checkMD("> var1 -- 3"),
			checkMD("> var2 -- 1"), checkMD("> var2 -- 2"), checkMD("> var2 -- 3"),
			checkMD("> var3 -- 1"), checkMD("> var3 -- 2"), checkMD("> var3 -- 3")
		);
		
		
		// ============== Advanced cases ==============
		
		// Visibility of referrals:
		testSourceProcessing("#", ">#@{#@INNER_EXP{A,B,C},#@INNER_EXP{A,B,C}}", 
			checkMD(">A"),checkMD(">B"),checkMD(">C"),
			checkMD(">A"),checkMD(">B"),checkMD(">C"));
		testSourceProcessing("#", "> #@{#@INNER_EXP{A,B,C}, #@(INNER_EXP)}", 3); // Error: undefined ref
		testSourceProcessing("#", "> #@{#@INNER_EXP{A,B,C}, } #@(INNER_EXP)", 4); // Error: undefined ref
		
		// Nesting of expansions
		testSourceProcessing("#", 
			">#@EXPA!{A,B,C} #@X{#@EXPA,x} #@X",
			
			checkMD("> A A"), checkMD("> B B"), checkMD("> C C"), checkMD("> x x")
		);
		
		testSourceProcessing("#", 
			"> #@X{#@EXPA{A,B,C},x} #@X",
			
			checkMD("> A A"), checkMD("> B B"), checkMD("> C C"), checkMD("> x x")
		);
	}
	
	@Test
	public void testExpansionAdvancedSyntax() throws Exception { testExpansionAdvancedSyntax$(); }
	public void testExpansionAdvancedSyntax$() throws Exception {
		// Different kmarker
		testSourceProcessing("#!", 
			"asdf #ok #!{,#!},#!#! #!{a,xxx#!}#!,},last#}!==",
			
			checkMD("asdf #ok !=="),
			checkMD("asdf #ok }!=="),
			checkMD("asdf #ok #! a!=="),
			checkMD("asdf #ok #! xxx},!=="),
			checkMD("asdf #ok last#!==")
		);
		
		// Uniform list close syntax ----
		
		testSourceProcessing("#", 
			"> #@{A,B,C,\n ¤}==",
			
			checkMD("> A=="),
			checkMD("> B=="),
			checkMD("> C==")
		);
		
		testSourceProcessing("#", "> #@{xxx, b ,text\n¤}==", 19);
		testSourceProcessing("#", "> #@{xxx, b ,\n¤ }==", 16);
		
		// Uniform argument separator syntax ----
		testSourceProcessing("#", 
			"> #@{►\nasd,► ,line}==",
			
			checkMD("> \nasd=="),
			checkMD(">  =="),
			checkMD("> line==")
		);
		testSourceProcessing("#", 
			"> #@《   ►\nasdf●  ► ●  line》==",
			
			checkMD("> \nasdf=="),
			checkMD(">  =="),
			checkMD(">   line==")
		);
		
		testSourceProcessing("#", "> #@{ text ►abc\ndef, ,line,\n}==", 12);
		testSourceProcessing("#", "> #@{ ►  ►abc\ndef, ,line,\n}==", 10);
		
		// Uniform argument separator syntax -- in metadata
		
		testSourceProcessing("#", "> #MD(►xyz, line\n){ ►ABC,line}==", 
			checkSourceOnly("> ABC,line==", 1)); 
		testSourceProcessing("#", "> #MD:\n►asd, line\n ==", 
			checkMD("> ", new MetadataEntry("MD", null, "asd, line\n ==", 2)));
		
		
		// Syntax errors: interactions:
		
		testSourceProcessing("#", "foo #@EXPANSION1{12,}:EXP:", checkMD("foo 12:EXP:"), checkMD("foo :EXP:"));

		testSourceProcessing("#", "> #,", 3); 
		testSourceProcessing("#", "> #}", 3); 
		
		testSourceProcessing("#", "foo #@EXPANSION1{12#:SPLIT\n}", 19);
		testSourceProcessing("#", "foo #@EXPANSION1{12#:END:\n}", 20+4);
		
		testSourceProcessing("#", "foo #@EXPANSION1{12}(#:SPLIT\n)", 21);
		testSourceProcessing("#", "foo #@EXPANSION1{12}(xxx:END:\n)", 21+3);
		
		
		for (int i = 0; i < TemplatedSourceProcessor.OPEN_DELIMS.length; i++) {
			String openDelim = TemplatedSourceProcessor.OPEN_DELIMS[i];
			if(openDelim.equals("{")) 
				continue;
			String close = TemplatedSourceProcessor.CLOSE_DELIMS[i];
			
			testSourceProcessing("#", prepString("asdf #@EXP►,}◙► #◄,last#◙}◄==", openDelim, close),
				
				checkMD(prepString("asdf ,}==", openDelim, close)),
				checkMD(prepString("asdf ► ◄,last●}==", openDelim, close))
			);
			testSourceProcessing("#", prepString("asdf #► ", openDelim, close), 6);
		}
	}
	
	public static String prepString(String source, String openDelim, String closeDelim) {
		source = source.replaceAll("►", openDelim);
		source = source.replaceAll("◄", closeDelim);
		source = source.replaceAll("◙", "●");
		return source;
	}
	
	@Test
	public void testDiscard() throws Exception { testDiscard$(); }
	public void testDiscard$() throws Exception {
		
		testSourceProcessing("#", "> #@{A,B #:DISCARD_CASE ,C}==",
			
			checkMD("> A=="), 
			checkMD("> C==")
		);
		
		testSourceProcessing("#", "Ⓗ━━\n  #@FOO《A● B #:DISCARD_CASE ●-C-》 ━━\n> #@FOO<",
			
			checkMD("> A<"), 
			checkMD("> -C-<")
		);
		
		// discard only existing case
		testSourceProcessing("#", "> #:DISCARD_CASE ==");
	}
	
	@Test
	public void testExpansionWithSplit() throws Exception { testExpansionWithSplit$(); }
	public void testExpansionWithSplit$() throws Exception {
		
		testSourceProcessing("#", 
			"#:SPLIT ____\n"+"#@EXPANSION1{var1,var2#,,var3##}"+
			"#:SPLIT\n> #@(EXPANSION1)",
			2 // Not defined
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____header____\n"+
			"#@EXPANSION1{var1,var2,var3}"+
			"#@EXPANSION2{A,BB,CCC}"+
			"#:SPLIT ___\n> #@EXPANSION2{xxxA,xxxb,xxxc}",
			2 // Redefined
		);
		
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@EXPANSION1",
			
			checkMD("> var1"),
			checkMD("> var2"),
			checkMD("> var3")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@EXPANSION1 == #@{A,B,C}(EXPANSION1)",
			
			checkMD("> var1 == A"),
			checkMD("> var2 == B"),
			checkMD("> var3 == C")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@EXPANSION1 == #@EXPANSION1",
			
			checkMD("> var1 == var1"),
			checkMD("> var2 == var2"),
			checkMD("> var3 == var3")
		);
		
		// Activate only
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@{A,B,C}(EXPANSION1) -- #@EXPANSION1",
			
			checkMD("> A -- var1"),
			checkMD("> B -- var2"),
			checkMD("> C -- var3")
		);
		
		// Across cases
		testSourceProcessing("#", 
			"#:HEADER ____header____\n"+
			"#@EXPAN_X{X,ZZ}"+
			"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT ___\n1: #@EXPANSION3{xxxA,xxxB,xxxC}(EXPANSION1) == #@EXPANSION1"+
			"#:SPLIT ___\n2: "+
			"#@EXPAN_X _ #@EXPANSION3{xA,xxB,xxxC}(EXPANSION1) == #@{a,bb}(EXPAN_X)",
			
			checkMD("1: xxxA == var1"),
			checkMD("1: xxxB == var2"),
			checkMD("1: xxxC == var3"),
			
			checkMD("2: X _ xA == a"),
			checkMD("2: X _ xxB == a"),
			checkMD("2: X _ xxxC == a"),
			checkMD("2: ZZ _ xA == bb"),
			checkMD("2: ZZ _ xxB == bb"),
			checkMD("2: ZZ _ xxxC == bb")
		);
		
		
		// Nested
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@OUTER{.#@EXPANSION1.,B} -- #@EXPANSION1",
			
			checkMD("> .var1. -- var1"),
			checkMD("> .var2. -- var2"),
			checkMD("> .var3. -- var3"),
			checkMD("> B -- var1"), checkMD("> B -- var2"), checkMD("> B -- var3")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@OUTER{.#@EXPANSION1.,~#@EXPANSION1~} -- #@EXPANSION1",
			
			checkMD("> .var1. -- var1"),
			checkMD("> .var2. -- var2"),
			checkMD("> .var3. -- var3"),
			checkMD("> ~var1~ -- var1"),
			checkMD("> ~var2~ -- var2"),
			checkMD("> ~var3~ -- var3")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@EXPANSION1 -- #@OUTER{.#@EXPANSION1.,B}",
			
			checkMD("> var1 -- .var1."), checkMD("> var1 -- B"), 
			checkMD("> var2 -- .var2."), checkMD("> var2 -- B"),
			checkMD("> var3 -- .var3."), checkMD("> var3 -- B")
		);
		
	}
	
	/* ------------------------  METADATA-EXPANSION interactions ------------------------ */
	
	@Test
	public void testMetadata_Interactions() throws Exception { testMetadata_Interactions$(); }
	public void testMetadata_Interactions$() throws Exception {
		testSourceProcessing("#", 
			"asdf #{#}#tag_A(asfd,3,4){xxx},abc###tag_B(arg1,arg2,arg3){sourceValue2}}==#{1,xxx}",
			
			checkMD("asdf }xxx==1", new MetadataEntry("tag_A", "asfd,3,4", "xxx", 6)),
			checkMD("asdf }xxx==xxx", new MetadataEntry("tag_A", "asfd,3,4", "xxx", 6)),
			checkMD("asdf abc#sourceValue2==1", new MetadataEntry("tag_B", "arg1,arg2,arg3", "sourceValue2", 9)),
			checkMD("asdf abc#sourceValue2==xxx", new MetadataEntry("tag_B", "arg1,arg2,arg3", "sourceValue2", 9))
		);
		
		testSourceProcessing("#", 
			"#{1,xxx}asdf #{#}#tag_A(asfd,3,4){xxx},###tag_B(arg1,arg2,arg3){sourceValue2}}==",
			
			checkMD("1asdf }xxx==", new MetadataEntry("tag_A", "asfd,3,4", "xxx", 7)),
			checkMD("1asdf #sourceValue2==", new MetadataEntry("tag_B", "arg1,arg2,arg3", "sourceValue2", 7)),
			checkMD("xxxasdf }xxx==", new MetadataEntry("tag_A", "asfd,3,4", "xxx", 9)),
			checkMD("xxxasdf #sourceValue2==", new MetadataEntry("tag_B", "arg1,arg2,arg3", "sourceValue2", 9))
		);
		
		testSourceProcessing("#", 
			"foo1 ## #error_EXP(asdf,3,4){xxx}=="+
			"asdf ## #error(info1)=="+
			"#:SPLIT ____\n"+
			"asdf ## #error=="+
			"asdf ## #error{xxx}=="+
			"#:SPLIT ____\n"+
			"multilineMD #error(arg1,arg2,arg3):\n line1\nline2\nline3\n#:END:lineOther4\n",
			
			checkMD(
				"foo1 # xxx=="+
				"asdf # ==",
				new MetadataEntry("error_EXP", "asdf,3,4", "xxx", 7),
				new MetadataEntry("error", "info1", null, 7 +5+7)
			),
			checkMD(
				"asdf # =="+
				"asdf # xxx==",
				new MetadataEntry("error", null, null, 7),
				new MetadataEntry("error", null, "xxx", 7 +2+7)
			),
			checkMD(
				"multilineMD lineOther4\n",
				new MetadataEntry("error", "arg1,arg2,arg3", " line1\nline2\nline3\n", 12)
			)
		);
		
		// Metadata in header:
		testSourceProcessing("#",  "#:HEADER ____\n"+"> #@{A,B,C}", 2);
		
		// Performance test:
		AnnotatedSource[] processTemplatedSource = TemplatedSourceProcessor.processTemplatedSource("#", 
			"#:HEADER ____\n"+
			">#@N{X#tag(arg){xxx} #tag2(arg){xxx} #tag3(arg){xxx}}"+
			" #@N2!{a#@(N),b#@(N),c#@(N),d#@(N),e#@(N),f#@(N)),g#@(N),h#@(N),k#@(N),l#@(N)}"+
			" #@N3{#@(N2),#@(N2),#@(N2),#@(N2),#@(N2),#@(N2)),#@(N2),#@(N2),#@(N2),#@(N2)}"+
			" #@N4{#@(N2),#@(N2),#@(N2),#@(N2),#@(N2),#@(N2)),#@(N2),#@(N2),#@(N2),#@(N2)}"+
			" #@N5{#@(N2),#@(N2),#@(N2),#@(N2),#@(N2),#@(N2)),#@(N2),#@(N2),#@(N2),#@(N2)}"+
			" #@N6{#@(N2),#@(N2),#@(N2),#@(N2),#@(N2),#@(N2)),#@(N2),#@(N2),#@(N2),#@(N2)}"+
			" #@N7{#@(N2),#@(N2),#@(N2),#@(N2),#@(N2),#@(N2)),#@(N2),#@(N2),#@(N2),#@(N2)}"+
			"==");
		
		System.out.println(processTemplatedSource.length);
	}
	
	@Test
	public void testExpansionInMetadata() throws Exception { testExpansionInMetadata$(); }
	public void testExpansionInMetadata$() throws Exception {
		
		testSourceProcessing("#", 
			"> #@EXPANSION1{var1,var2,var3xxx} #tag(arg1,arg2,arg3){mdsource:#@EXPANSION1}",
			
			checkMD("> var1 mdsource:var1", new MetadataEntry("tag", "arg1,arg2,arg3", "mdsource:var1", 7)),
			checkMD("> var2 mdsource:var2", new MetadataEntry("tag", "arg1,arg2,arg3", "mdsource:var2", 7)),
			checkMD("> var3xxx mdsource:var3xxx", new MetadataEntry("tag", "arg1,arg2,arg3", "mdsource:var3xxx", 10))
		);
		
		testSourceProcessing("#", 
			"> #tag(arg1){mdsource: #@EXPANSION1{var1,var2,var3xxx} -- #@{A,B,C}(EXPANSION1)}",
			
			checkMD("> mdsource: var1 -- A", new MetadataEntry("tag", "arg1", "mdsource: var1 -- A", 2)),
			checkMD("> mdsource: var2 -- B", new MetadataEntry("tag", "arg1", "mdsource: var2 -- B", 2)),
			checkMD("> mdsource: var3xxx -- C", new MetadataEntry("tag", "arg1", "mdsource: var3xxx -- C", 2))
		);
		
		
		testSourceProcessing("#", 
			"> #tag(arg){mdsource: #@EXPANSION1{var1,var2,var3xxx} -- #nestedMD{nestedMDsrc #@{A,B,C}(EXPANSION1)}}",
			
			checkMD("> mdsource: var1 -- nestedMDsrc A", 
				new MetadataEntry("tag", "arg", "mdsource: var1 -- nestedMDsrc A", 2),
				new MetadataEntry("nestedMD", null, "nestedMDsrc A", 20)),
			checkMD("> mdsource: var2 -- nestedMDsrc B", 
				new MetadataEntry("tag", "arg", "mdsource: var2 -- nestedMDsrc B", 2),
				new MetadataEntry("nestedMD", null, "nestedMDsrc B", 20)),
			checkMD("> mdsource: var3xxx -- nestedMDsrc C", 
				new MetadataEntry("tag", "arg", "mdsource: var3xxx -- nestedMDsrc C", 2),
				new MetadataEntry("nestedMD", null, "nestedMDsrc C", 23))
		);
		
		testSourceProcessing("#", 
			"> #@EXP{AA,B,CCCC} #tag(arg):\ntagMD #nestedMD{xxx}",
			
			checkMD("> AA ", 
				new MetadataEntry("tag", "arg", "tagMD xxx", 5),
				new MetadataEntry("nestedMD", null, "xxx", -1))
				,
			checkMD("> B ", 
				new MetadataEntry("tag", "arg", "tagMD xxx", 4),
				new MetadataEntry("nestedMD", null, "xxx", -1))
				,
			checkMD("> CCCC ", 
				new MetadataEntry("tag", "arg", "tagMD xxx", 7),
				new MetadataEntry("nestedMD", null, "xxx", -1)
				)
		);
	}
	
	/* ------------------------  CONDITIONAL EXPANSION  ------------------------ */
	
	@Test
	public void testIfElseExpansion() throws Exception { testIfElseExpansion$(); }
	public void testIfElseExpansion$() throws Exception {
		testSourceProcessing("#", 
			"> #@{A,B#var(Bactive)} #?var{THEN}-#?var!{NOT_THEN}",
			
			checkMD("> A -NOT_THEN"),
			checkMD("> B THEN-", new MetadataEntry("var", "Bactive", null, 3))
		);
		
		testSourceProcessing("#", 
			"> #@{A,B#var(Bactive)} #?var{THEN,ELSE}-#?var!{NOT_THEN,NOT_ELSE}",
			
			checkMD("> A ELSE-NOT_THEN"),
			checkMD("> B THEN-NOT_ELSE", new MetadataEntry("var", "Bactive", null, 3))
		);
		
		testSourceProcessing("#", "> #?{THEN,ELSE, INVALID}", 4);
		testSourceProcessing("#", "> #@{A ,B #var(Bactive) } #?var{THEN,ELSE, INVALID}", 51);
		
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{1#var1,2#var2,3#var3}"+
			"#:SPLIT\n> #?var1{IF,ELSE} #@{A,B,C}(EXPANSION1) -- #@EXPANSION1 "+
			"#?var1{THEN,ELSE}#?var2{var2}",
			
			checkMD("> ELSE A -- 1 THEN", new MetadataEntry("var1", null, null, 13)),
			checkMD("> ELSE B -- 2 ELSEvar2", new MetadataEntry("var2", null, null, 13)),
			checkMD("> ELSE C -- 3 ELSE", new MetadataEntry("var3", null, null, 13))
		);
		
		// Test conditional exp when conditional is inside referred MD
		testSourceProcessing("#", 
			"#parentMD【> #@{A,B#var(Bactive)} #?var{IF} #?parentMD{parentMDActive}】",
			
			checkMD("> A  parentMDActive",
				new MetadataEntry("parentMD", null, DONT_CHECK, 0)
			),
			checkMD("> B IF parentMDActive", 
				new MetadataEntry("parentMD", null, DONT_CHECK, 0),
				new MetadataEntry("var", "Bactive", null, 3)
			)
		);
	}
	
}