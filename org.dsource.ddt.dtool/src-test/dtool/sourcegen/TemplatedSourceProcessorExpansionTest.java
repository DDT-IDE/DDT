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

import static dtool.sourcegen.TemplatedSourceProcessor.StandardErrors.MISMATCHED_VARIATION_SIZE;
import static dtool.sourcegen.TemplatedSourceProcessor.StandardErrors.REDEFINITION;
import static dtool.sourcegen.TemplatedSourceProcessor.StandardErrors.UNDEFINED_REFER;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessor.StandardErrors;

/* BASIC EXPANSION FORMATS:
A:  #@{1, 2, 3}         Unnamed-Expansion
B:  #@EXP{1, 2, 3}      Definition-Expansion
B2: #@EXP!{1, 2, 3}     Definition-only
Bx: #@EXP               Full-Reference

R1: #@{1, 2, 3}(EXP)    Expansion, pairing with active(EXP)
R2: #@EXP2(EXP)         Refer-Expansion(EXP2), pairing with active(EXP)
R3: #@EXP2{1,2,3}(EXP)  Definition-Expansion, pairing with active(EXP)

H:  #@^EXP              Unpaired Full-Reference
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TemplatedSourceProcessorExpansionTest extends TemplatedSourceProcessorCommonTest {
	
	@Test
	public void test1_ExpansionSyntax() throws Exception { testExpansionSyntax$(); }
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
	public void test2_ExpansionAdvancedSyntax() throws Exception { testExpansionAdvancedSyntax$(); }
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
			checkMD("> ", new MetadataEntry("MD", null, "asd, line\n ==", 2, false)));
		
		
		// Syntax errors: interactions:
		
		testSourceProcessing("#", "foo #@EXPANSION1{12,}:EXP:", checkMD("foo 12:EXP:"), checkMD("foo :EXP:"));
		
		testSourceProcessing("#", "> #,", 3); 
		testSourceProcessing("#", "> #}", 3); 
		
		testSourceProcessing("#", "foo #@EXPANSION1{12#:SPLIT\n}", 19);
		testSourceProcessing("#", "foo #@EXPANSION1{12#:END:\n}", 20+4);
		
		testSourceProcessing("#", "foo #@EXPANSION1{12}(#:SPLIT\n)", 21);
		testSourceProcessing("#", "foo #@EXPANSION1{12}(xxx:END:\n)", 21+3);
		
		
		for (int i = 0; i < TemplatedSourceProcessorParser.OPEN_DELIMS.length; i++) {
			String openDelim = TemplatedSourceProcessorParser.OPEN_DELIMS[i];
			if(openDelim.equals("{"))
				continue;
			String close = TemplatedSourceProcessorParser.CLOSE_DELIMS[i];
			
			testSourceProcessing("#", prepString("asdf #@EXP►,}◙► #◄,last#◙}◄==", openDelim, close),
				
				checkMD(prepString("asdf ,}==", openDelim, close)),
				checkMD(prepString("asdf ► ◄,last●}==", openDelim, close))
			);
			testSourceProcessing("#", prepString("asdf #► ", openDelim, close), 6);
		}
	}
	
	@Test
	public void test3_Expansion() throws Exception { testExpansion$(); }
	public void testExpansion$() throws Exception {
		
		// A: Unnamed-Expansion
		testSourceProcessing("#", 
			"foo #@{var1,var2#,,var3##}==",
			
			checkMD("foo var1=="),
			checkMD("foo var2,=="),
			checkMD("foo var3#==")
		);
		
		// B:  #@EXP{1, 2, 3}      Definition-Expansion
		//Error: redefined:
		testSourceProcessing("#", "foo #@EXP1{a,b} -- #@EXP1{a,b}", REDEFINITION, "EXP1"); 
		testSourceProcessing("#", "foo #@EXP1{a,#@EXP1{a,b}}", REDEFINITION, "EXP1");
		testSourceProcessing("#", "foo #@EXP1《¤》", StandardErrors.NO_ARGUMENTS, "EXP1");
		
		// B2: #@EXP!{1, 2, 3}     Definition only
		
		testSourceProcessing("#", "foo #@EXPANSION1! -- #@EXPANSION1{a,b}", 17); // Bad syntax: no args
		testSourceProcessing("#", "foo #@! -- #@EXPANSION1{a,b}", 7); // Bad syntax: no id
		
		testSourceProcessing("#", "foo #@EXPANSION1!{a,b} -- #@EXPANSION1{a,b}", REDEFINITION, "EXPANSION1"); 
		
		testSourceProcessing("#", "> #@EXPANSION1!{A,B,C} b", 
			checkMD(">  b"));
		
		// Bx: #@EXP               Full-Reference
		testSourceProcessing("#", "> #@EXP2", UNDEFINED_REFER, "EXP2");
		
		testSourceProcessing("#", 
			"#@EXPANSION1{var1,var2,var3} == #@EXPANSION1",
			checkMD("var1 == var1"),
			checkMD("var2 == var2"),
			checkMD("var3 == var3")
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
		
		testSourceProcessing("#", 
			"#@EXPANSION1!{var1,var2,var3}"+ "#@EXPANSION1 == #@EXPANSION1",
			checkMD("var1 == var1"),
			checkMD("var2 == var2"),
			checkMD("var3 == var3")
		);
		
		testSourceProcessing("#", "foo #@EXPANSION1!{a,#@EXPANSION1{a,b}} #@EXPANSION1", REDEFINITION, "EXPANSION1"); 
		
		
		//R1: #@{1, 2, 3}(EXP)    Expansion, pairing with active(EXP)
		//R2: #@EXP2(EXP)         Refer-Expansion(EXP2), pairing with active(EXP)
		//R3: #@EXP2{1,2,3}(EXP)  Definition-Expansion, pairing with active(EXP)
		
		
		testSourceProcessing("#", "> #@(EXPANSION1)", 16); // Syntax error
		// Error: undefined ref
		testSourceProcessing("#", "> #@{A,B,C}(EXPANSION1)", UNDEFINED_REFER, ":EXPANSION1");
		testSourceProcessing("#", "> #@EXP2(EXPANSION1)", UNDEFINED_REFER, "EXP2:EXPANSION1");
		testSourceProcessing("#", "> #@EXP2{A,B,C}(EXPANSION1)", UNDEFINED_REFER, "EXP2:EXPANSION1");
		testSourceProcessing("#", "#@H_EXP!{z1,z2,z3}"+ "> #@H_EXP(EXPANSION1)", UNDEFINED_REFER, "H_EXP:EXPANSION1");
		//Error: Mismatched argument count:
		testSourceProcessing("#", "> #@EXP1{a,b} -- #@{a}(EXP1)", MISMATCHED_VARIATION_SIZE, ":EXP1"); 
		testSourceProcessing("#", "> #@EXP1{a,b} -- #@{a,b,c}(EXP1)", MISMATCHED_VARIATION_SIZE, ":EXP1");
		testSourceProcessing("#", "> #@EXP1{a,b} -- #@EXP2{a,b,c}(EXP1)", MISMATCHED_VARIATION_SIZE, "EXP2:EXP1");
		testSourceProcessing("#", "> #@H_EXP{a,b} -- #@{a,b,c}(H_EXP)", MISMATCHED_VARIATION_SIZE, ":H_EXP");
		
		
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,var2,var3}==#@{A,B,C}(EXPANSION1)",
			
			checkMD("foo var1==A"),
			checkMD("foo var2==B"),
			checkMD("foo var3==C")
		);
		
		testSourceProcessing("#", 
			"#@EXPANSION1!{var1,var2,var3}"+ "#@EXPANSION1 == #@{A,B,C}(EXPANSION1)",
			
			checkMD("var1 == A"),
			checkMD("var2 == B"),
			checkMD("var3 == C")
		);
		
		testSourceProcessing("#", 
			"#@EXPANSION1!{var1,var2,var3}" + "#@{A,B,C}(EXPANSION1) == #@EXPANSION1",
			
			checkMD("A == var1"),
			checkMD("B == var2"),
			checkMD("C == var3")
		);
		
		testSourceProcessing("#", "#@EXP2!{A,B,C}"+
			"#@EXP1{var1,var2,var3}"+"==#@EXP2(EXP1) -- #@{x,y,z}(EXP1)",
			
			checkMD("var1==A -- x"),
			checkMD("var2==B -- y"),
			checkMD("var3==C -- z")
		);
		testSourceProcessing("#", 
			"#@EXP1{var1,var2,var3}"+"==#@EXP2{A,B,C}(EXP1) -- #@{x,y,z}(EXP1)",
			
			checkMD("var1==A -- x"), 
			checkMD("var2==B -- y"),
			checkMD("var3==C -- z")
		);
		
		// Make sure both H_EXP and EXP ids can be referred (master id != master element id)
		testSourceProcessing("#", 
			"#@H_EXP!{var1,var2,var3}" + "#@EXP{A,B,C}(H_EXP) #@{x,y,z}(EXP)--#@H_EXP",
			
			checkMD("A x--var1"), 
			checkMD("B y--var2"),
			checkMD("C z--var3")
		);
		
		// test indirect pairing master: EXP2->EXP1
		testSourceProcessing("#", 
			"#@EXP1{var1,var2,var3}"+"#@EXP2!{z1,z2,z3}"+"> #@EXP2(EXP1) -- #@{A,B,C}(EXP2)",
			
			checkMD("var1> z1 -- A"),
			checkMD("var2> z2 -- B"),
			checkMD("var3> z3 -- C")
		);
		// another indirect pairing master (through define-only) : EXP2->EXP1 
		testSourceProcessing("#", 
			"#@EXP1{var1,var2,var3}"+"#@EXP2!{z1,z2,z3}(EXP1)"+"> #@EXP2 -- #@{A,B,C}(EXP2)",
			
			checkMD("var1> z1 -- A"),
			checkMD("var2> z2 -- B"),
			checkMD("var3> z3 -- C")
		);
		
		testSourceProcessing("#", 
			"#@EXPANSION1{var1,var2,var3}==#@EXP2{VAR1,VAR2,VAR3}(EXPANSION1) ||"+
			" #@EXPANSION1•X == #@EXP2",
			
			checkMD("var1==VAR1 || var1X == VAR1"),
			checkMD("var2==VAR2 || var2X == VAR2"),
			checkMD("var3==VAR3 || var3X == VAR3")
		);
		
		
		// H:  #@^EXP              Unnamed-Expansion with argument referral(EXP)
		
		testSourceProcessing("#", "> #@^{1,2,3}", 5); // Bad syntax: no name
		testSourceProcessing("#", "> #@^EXP1!{1,2,3}", 10); // Bad syntax: has define only
		testSourceProcessing("#", "> #@^EXP1{1,2,3}", 16); // Bad syntax: ^ with arguments (makes definition)
		testSourceProcessing("#", "> #@^EXP1(EXP2)", 10); // Bad syntax: has refer id
		
		GeneratedSourceChecker[] expectedCasesH = array(
			checkMD("> var1 -- var1"), checkMD("> var1 -- var2"), checkMD("> var1 -- var3"),
			checkMD("> var2 -- var1"), checkMD("> var2 -- var2"), checkMD("> var2 -- var3"),
			checkMD("> var3 -- var1"), checkMD("> var3 -- var2"), checkMD("> var3 -- var3"));
		
		testSourceProcessing("#",
			"#@EXP1!{var1,var2,var3}> #@^EXP1 -- #@EXP1", expectedCasesH
		);
		testSourceProcessing("#", 
			"> #@EXP1{var1,var2,var3} -- #@^EXP1", expectedCasesH
		);
		
		testSourceProcessing("#", 
			"#@EXP1!{var1,var2,var3}"+ "#@EXP2!{1,2,3}"+
			"> #@EXP1 -- #@EXP2 - #@EXP1(EXP2)", 
			
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
		
		// ------------- Test some nesting issues: ------------- 
		
		testSourceProcessing("#", 
			"#@EXP{var1,#@SUB【var2A●var2B】,var3}==#@{A,#@SUB,#@【C】}(EXP)",
			checkMD("var1==A"),
			checkMD("var2A==var2A"),
			checkMD("var2B==var2B"),
			checkMD("var3==C")
		);
		testSourceProcessing("#", 
			"#@EXP{var1,#@SUB【var2A●var2B】,var3}==#@{A,#@【var2A●var2B】(SUB),#@【C】}(EXP)",
			checkMD("var1==A"), 
			checkMD("var2A==var2A"), 
			checkMD("var2B==var2B"), 
			checkMD("var3==C")
		);
		
		testSourceProcessing("#", 
			"#@EXPANSION1{var1,var2,var3}==#@{A,#@【B1●B2】,C}(EXPANSION1)",
			checkMD("var1==A"), 
			checkMD("var2==B1"), 
			checkMD("var2==B2"), 
			checkMD("var3==C")
		);
		
		
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,#@【var2A●var2B】,var3}==#@{A,B,C}(EXPANSION1)",
			checkMD("foo var1==A"),
			checkMD("foo var2A==B"),
			checkMD("foo var2B==B"),
			checkMD("foo var3==C")
		);
		
		// Visibility of nested-definitions:
		testSourceProcessing("#", ">#@{#@INNER_EXP{A,B,C},#@INNER_EXP{A,B}}", 
			checkMD(">A"),checkMD(">B"),checkMD(">C"),
			checkMD(">A"),checkMD(">B"));
		
		testSourceProcessing("#", "> #@{#@INNER_EXP{A,B,C}, #@INNER_EXP}", UNDEFINED_REFER, "INNER_EXP");
		testSourceProcessing("#", "> #@{#@INNER_EXP{A,B,C}, } #@INNER_EXP", UNDEFINED_REFER, "INNER_EXP");
		
		// Define-only with nested: TODO:
		testSourceProcessing("#", 
			"#@H_EXP!{var1,#@SUB【var2A●var2B】,var3}"+"#@H_EXP==#@H_EXP",
			checkMD("var1==var1"),
			checkMD("var2A==var2A"),
			checkMD("var2B==var2B"),
			checkMD("var3==var3")
		);
		
		// Nested-definitions:
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
	public void test4_Discard() throws Exception { testDiscard$(); }
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
	public void test5_ExpansionWithSplit() throws Exception { testExpansionWithSplit$(); }
	public void testExpansionWithSplit$() throws Exception {
		
		testSourceProcessing("#", 
			"#:SPLIT ____\n"+"#@EXPANSION1{var1,var2#,,var3##}"+
			"#:SPLIT\n> #@EXPANSION1",
			StandardErrors.UNDEFINED_REFER, "EXPANSION1"
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____header____\n"+
			"#@EXPANSION1{var1,var2,var3}"+
			"#@EXPANSION2{A,BB,CCC}"+
			"#:SPLIT ___\n> #@EXPANSION2{xxxA,xxxb,xxxc}",
			StandardErrors.REDEFINITION, "EXPANSION2"
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
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}━━\n"+
			"> #@OUTER{.#@EXPANSION1.,B} -- #@EXPANSION1",
			
			checkMD("> .var1. -- var1"),
			checkMD("> .var2. -- var2"),
			checkMD("> .var3. -- var3"),
			checkMD("> B -- var1"), checkMD("> B -- var2"), checkMD("> B -- var3")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}━━\n"+
			"> #@EXPANSION1 -- #@OUTER{.#@EXPANSION1.,B}",
			
			checkMD("> var1 -- .var1."), checkMD("> var1 -- B"), 
			checkMD("> var2 -- .var2."), checkMD("> var2 -- B"),
			checkMD("> var3 -- .var3."), checkMD("> var3 -- B")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}━━\n"+
			"> #@{b,.#@EXPANSION1.} -- #@OUTER{.#@EXPANSION1.,B}",
			
			checkMD("> b -- .var1."),
			checkMD("> b -- .var2."),
			checkMD("> b -- .var3."),
			checkMD("> b -- B"),
			
			checkMD("> .var1. -- .var1."), checkMD("> .var1. -- B"), 
			checkMD("> .var2. -- .var2."), checkMD("> .var2. -- B"),
			checkMD("> .var3. -- .var3."), checkMD("> .var3. -- B")
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
		

		
	}
	
	/* ------------------------  METADATA-EXPANSION interactions ------------------------ */
	
	@Test
	public void test6_Metadata_Interactions() throws Exception { testMetadata_Interactions$(); }
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
				new MetadataEntry("error", "arg1,arg2,arg3", " line1\nline2\nline3\n", 12, false)
			)
		);
		
		// Unnamed definitions in header
		testSourceProcessing("#",  "#:HEADER ____\n"+"> #@{A,B,C}", 
			2);
		
		// Performance test:
		AnnotatedSource[] processTemplatedSource = TemplatedSourceProcessor.processTemplatedSource("#", 
			"#:HEADER ____\n"+
			">#@N{X#tag(arg){xxx} #tag2(arg){xxx} #tag3(arg){xxx}}"+
			" #@N2!{a#@N,b#@N,c#@N,d#@N,e#@N,f#@N),g#@N,h#@N,k#@N,l#@N}"+
			" #@N3{#@N2,#@N2,#@N2,#@N2,#@N2,#@N2),#@N2,#@N2,#@N2,#@N2}"+
			" #@N4{#@N2,#@N2,#@N2,#@N2,#@N2,#@N2),#@N2,#@N2,#@N2,#@N2}"+
			" #@N5{#@N2,#@N2,#@N2,#@N2,#@N2,#@N2),#@N2,#@N2,#@N2,#@N2}"+
			" #@N6{#@N2,#@N2,#@N2,#@N2,#@N2,#@N2),#@N2,#@N2,#@N2,#@N2}"+
			" #@N7{#@N2,#@N2,#@N2,#@N2,#@N2,#@N2),#@N2,#@N2,#@N2,#@N2}"+
			"==");
		
		System.out.println(processTemplatedSource.length);
	}
	
	@Test
	public void test6b_ExpansionInMetadata() throws Exception { testExpansionInMetadata$(); }
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
		
		MetadataEntry top;
		testSourceProcessing("#", 
			"> #@EXP{AA,B,CCCC} #tag(arg):\ntagMD #nestedMD{xxx}",
			
			checkMD("> AA ", 
				top = new MetadataEntry("tag", "arg", "tagMD xxx", 5, false),
				new MetadataEntry("nestedMD", null, "xxx", 6, top))
				,
			checkMD("> B ", 
				top = new MetadataEntry("tag", "arg", "tagMD xxx", 4, false),
				new MetadataEntry("nestedMD", null, "xxx", 6, top))
				,
			checkMD("> CCCC ", 
				top = new MetadataEntry("tag", "arg", "tagMD xxx", 7, false),
				new MetadataEntry("nestedMD", null, "xxx", 6, top)
				)
		);
	}
	
	/* ------------------------  CONDITIONAL EXPANSION  ------------------------ */
	
	@Test
	public void test9_IfElseExpansion() throws Exception { testIfElseExpansion$(); }
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