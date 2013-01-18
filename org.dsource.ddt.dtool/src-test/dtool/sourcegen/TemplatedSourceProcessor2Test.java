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
import dtool.sourcegen.TemplatedSourceProcessor2.TemplatedSourceException;
import dtool.tests.CommonTestUtils;

public class TemplatedSourceProcessor2Test extends CommonTestUtils {
	
	public void testSourceProcessing(String marker, String source, GeneratedSourceChecker... checkers) {
		TemplatedSourceProcessor2 tsp = new TemplatedSourceProcessor2() { 
			@Override
			protected void reportError(int offset) throws TemplatedSourceException {
				assertFail();
			};
		};
		visitContainer(tsp.processSource_unchecked(marker, source), checkers);
	}
	
	public void testSourceProcessing(String marker, String source, int errorOffset) {
		try {
			TemplatedSourceProcessor2.processTemplatedSource(marker, source);
			assertFail();
		} catch(TemplatedSourceException tse) {
			assertTrue(tse.errorOffset == errorOffset);
		}
	}
	
	protected abstract class GeneratedSourceChecker implements Visitor<AnnotatedSource> {} 
	protected GeneratedSourceChecker checkMD(final String source, final MetadataEntry... metadataArray) {
		return new GeneratedSourceChecker () {
			@Override
			public void visit(AnnotatedSource genSource) {
				assertEquals(genSource.source, source);
				assertEquals(genSource.metadata.size(), metadataArray.length);
				for (int i = 0; i < metadataArray.length; i++) {
					checkMetadata(metadataArray[i], genSource.metadata.get(i));
				}
			}
		};
	}
	
	protected void checkMetadata(MetadataEntry mde1, MetadataEntry mde2) {
		assertAreEqual(mde1.name, mde2.name);
		assertAreEqual(mde1.value, mde2.value);
		assertAreEqual(mde1.associatedSource, mde2.associatedSource);
		assertAreEqual(mde1.offset, mde2.offset);
	}
	
	
	@Test
	public void testSplit() throws Exception { testSplit$(); }
	public void testSplit$() throws Exception {
		 
		testSourceProcessing("#", 
			"#:SPLIT ___________________\ncase1\nasdfasdf"+
			"#:SPLIT comment\ncase ##2\nblahblah\n#:SPLIT comment\r\n"+ 
			"#:SPLIT\n case ##:3\nblahblah\n"
			,
			checkMD("case1\nasdfasdf"),
			checkMD("case #2\nblahblah\n"),
			checkMD(""),
			checkMD(" case #:3\nblahblah\n")
		);
		
		
		testSourceProcessing("#", 
			"case ##1\nasdfasdf"+
			"#:SPLIT comment\ncase ##2\nblahblah\n"
			,
			checkMD("case #1\nasdfasdf"),
			checkMD("case #2\nblahblah\n")
		);
		
		testSourceProcessing("#", 
			"#:SPLIT _____\ncase1\na#:XPLIT sdfasdf"+
			"#:SPLIT\n case3\nblahblah\n"
			,
			8
		);
	}
	
	@Test
	public void testExpansion() throws Exception { testExpansion$(); }
	public void testExpansion$() throws Exception {
		
		testSourceProcessing("#", 
			"asdf ## #{,#},#,,##, ,line}==",
			
			checkMD("asdf # =="),
			checkMD("asdf # }=="),
			checkMD("asdf # ,=="),
			checkMD("asdf # #=="),
			checkMD("asdf #  =="),
			checkMD("asdf # line==")
		);
		
		testSourceProcessing("#", 
			"asdf #{,#},## #{a,xxx#}#,},last}==",
			
			checkMD("asdf =="),
			checkMD("asdf }=="),
			checkMD("asdf # a=="),
			checkMD("asdf # xxx},=="),
			checkMD("asdf last==")
		);
		
		testSourceProcessing("#!", 
			"asdf #ok #!{,#!},#!#! #!{a,xxx#!}#!,},last#}!==",
			
			checkMD("asdf #ok !=="),
			checkMD("asdf #ok }!=="),
			checkMD("asdf #ok #! a!=="),
			checkMD("asdf #ok #! xxx},!=="),
			checkMD("asdf #ok last#!==")
		);
		
		// Syntax errors:
		testSourceProcessing("#", "> #,", 3); 
		testSourceProcessing("#", "> #}", 3); 
		
		testSourceProcessing("#", "foo #@{", 7); 
		testSourceProcessing("#", "foo #@==", 6);
		testSourceProcessing("#", "foo #@!", 7);
		testSourceProcessing("#", "foo #@EXPANSION1", 16); // no data
		testSourceProcessing("#", "foo #@EXPANSION1{", 17); 
		testSourceProcessing("#", "foo #@EXPANSION1{12,}:", 22);
		testSourceProcessing("#", "foo #@EXPANSION1{12,}:EXP)", 22+3);
		testSourceProcessing("#", "foo #@EXPANSION1{12,}(EXP:", 22+3);
		
		testSourceProcessing("#", "foo #@EXPANSION1{12#:SPLIT\n}", 19);
		testSourceProcessing("#", "foo #@EXPANSION1{12#:END:\n}", 20);
		
		testSourceProcessing("#", "foo #@EXPANSION1{12}(#:SPLIT\n)", 21);
		testSourceProcessing("#", "foo #@EXPANSION1{12}(xxx:END:\n)", 21+3);
	}
	
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
		
		// Syntax errors
		testSourceProcessing("#", "badsyntax #foo(=={", 18 );
		testSourceProcessing("#", "badsyntax #foo(==){asdf", 18+5 );
		
		testSourceProcessing("#", "badsyntax #foo(==#:SPLIT\n)", 17);
		testSourceProcessing("#", "badsyntax #foo(==#:END:", 18);
		testSourceProcessing("#", "badsyntax #foo(){xxx#:SPLIT\n)", 17+3);
		testSourceProcessing("#", "badsyntax #foo(){xxx#:END:", 17+3+1);
		
		
		//multineLine MD syntax
		testSourceProcessing("#", 
			"multilineMD #error(arg1,arg2,arg3): line1\nline2\nline3\n#:END:\nlineOther4\n",
			
			checkMD("multilineMD lineOther4\n", 
				new MetadataEntry("error", "arg1,arg2,arg3", " line1\nline2\nline3\n", 12))
		);
		testSourceProcessing("#", 
			"multilineMD #error(arg1,arg2,arg3):",
			
			checkMD("multilineMD ", 
				new MetadataEntry("error", "arg1,arg2,arg3", "", 12))
		);
		
		testSourceProcessing("#", 
			"multilineMD #error(arg1,arg2,arg3): line1\nline2\nline3\n#:SPLIT:\nlineOther4\n",
			
			checkMD("multilineMD ", 
				new MetadataEntry("error", "arg1,arg2,arg3", " line1\nline2\nline3\n", 12)),
			checkMD("lineOther4\n")
		);
		
		// All toghether
		testSourceProcessing("#", 
			"foo1 ## #error_EXP(asdf,3,4){xxx}=="+
			"asdf ## #error(info1)=="+
			"asdf ## #error=="+
			"asdf ## #error{xxx}=="+
			"multilineMD #error(arg1,arg2,arg3): line1\nline2\nline3\n#:END:\nlineOther4\n",
			
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
				new MetadataEntry("error", "arg1,arg2,arg3", " line1\nline2\nline3\n", 7 +5+7 +2+7 +2+7 +3+2+12)
			)
		);
	}
	
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
			"multilineMD #error(arg1,arg2,arg3): line1\nline2\nline3\n#:END:\nlineOther4\n",
			
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
		AnnotatedSource[] processTemplatedSource = TemplatedSourceProcessor2.processTemplatedSource("#", 
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
			"> #@EXPANSION1{var1,var2,var3xxx} #tag(arg1,arg2,arg3){mdsource:#@(EXPANSION1)}",
			
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
			"> #@EXP{AA,B,CCCC} #tag(arg):tagMD: #nestedMD{xxx}",
			
			checkMD("> AA ", 
				new MetadataEntry("tag", "arg", "tagMD: xxx", 5),
				new MetadataEntry("nestedMD", null, "xxx", 5+7))
				,
			checkMD("> B ", 
				new MetadataEntry("tag", "arg", "tagMD: xxx", 4),
				new MetadataEntry("nestedMD", null, "xxx", 4+7))
				,
			checkMD("> CCCC ", 
				new MetadataEntry("tag", "arg", "tagMD: xxx", 7),
				new MetadataEntry("nestedMD", null, "xxx", 7+7)
				)
		);
	}
	
	@Test
	public void testPairedExpansion() throws Exception { testPairedExpansion$(); }
	public void testPairedExpansion$() throws Exception {
		
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,var2#,,var3##}==#@{A,B,C}:EXPANSION1:",
			
			checkMD("foo var1==A"),
			checkMD("foo var2,==B"),
			checkMD("foo var3#==C")
		);
		
		testSourceProcessing("#", 
			"#@EXPANSION1!{var1,var2,var3}"+
			"> #@!(EXPANSION1) #@{A,B,C}(EXPANSION1) -- #@(EXPANSION1)",
			
			checkMD(">  A -- var1"),
			checkMD(">  B -- var2"),
			checkMD(">  C -- var3")
		);
		
		testSourceProcessing("#", 
			"#@EXPANSION1!{var1,var2#,,var3##}foo ==#@!(EXPANSION1) #@{A,B,C}(EXPANSION1)",
			
			checkMD("foo == A"),
			checkMD("foo == B"),
			checkMD("foo == C")
		);
		
		testSourceProcessing("#", "foo #@:EXPANSION1:", 4); // Error: undefined ref
		testSourceProcessing("#", "foo #@{A,B,C}(EXPANSION1)", 4); // Error: undefined ref
		testSourceProcessing("#", "foo #@EXPANSION1!{a,b,c}#@{A,B,C}(EXPANSION1)", 4); // Error: non active ref
		testSourceProcessing("#", "foo #@EXPANSION1{a,b} -- #@EXPANSION1{a,b}", 9); //Error: redefined
		
		//Error: Mismatched argument count:
		testSourceProcessing("#", "> #@EXPANSION1{a,b} -- #@{a}(EXPANSION1)", 7); 
		testSourceProcessing("#", "> #@EXPANSION1{a,b} -- #@{a,b,c}(EXPANSION1)", 7);
		
		
		testSourceProcessing("#", 
			"#@EXP1{var1,var2,var3}"+
			"#@EXP2!{z1,z2,z3}"+
			"> #@EXP2(EXP1) -- #@{A,B,C}(EXP1)",
			
			checkMD("var1> z1 -- A"),
			checkMD("var2> z2 -- B"),
			checkMD("var3> z3 -- C")
		);
		
		// ---
		
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1 ,var2#,,var3##}==#@EXP1ALT{VAR1,VAR2,VAR3}:EXPANSION1: ||"+
			" #@:EXPANSION1: == #@:EXP1ALT:",
			
			checkMD("foo var1 ==VAR1 || var1  == VAR1"),
			checkMD("foo var2,==VAR2 || var2, == VAR2"),
			checkMD("foo var3#==VAR3 || var3# == VAR3")
		);
		
		
		testSourceProcessing("#", 
			"foo #@EXPANSION1{var1,var2#,,var3##} == #{a,xxx} -- #@(EXPANSION1)",
			
			checkMD("foo var1 == a -- var1"),
			checkMD("foo var1 == xxx -- var1"),
			checkMD("foo var2, == a -- var2,"),
			checkMD("foo var2, == xxx -- var2,"),
			checkMD("foo var3# == a -- var3#"),
			checkMD("foo var3# == xxx -- var3#")
		);
		
	}
	
	@Test
	public void testPairedExpansionWithSplit() throws Exception { testPairedExpansionWithSplit$(); }
	public void testPairedExpansionWithSplit$() throws Exception {
		
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
			"#:SPLIT\n> #@(EXPANSION1)",
			
			checkMD("> var1"),
			checkMD("> var2"),
			checkMD("> var3")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@(EXPANSION1) == #@{A,B,C}(EXPANSION1)",
			
			checkMD("> var1 == A"),
			checkMD("> var2 == B"),
			checkMD("> var3 == C")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@(EXPANSION1) == #@(EXPANSION1)",
			
			checkMD("> var1 == var1"),
			checkMD("> var2 == var2"),
			checkMD("> var3 == var3")
		);
		
		// Activate only
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n>#@!(EXPANSION1) #@{A,B,C}(EXPANSION1) -- #@(EXPANSION1)",
			
			checkMD("> A -- var1"),
			checkMD("> B -- var2"),
			checkMD("> C -- var3")
		);
		
		// Across cases
		testSourceProcessing("#", 
			"#:HEADER ____header____\n"+
			"#@EXPAN_X{X,ZZ}"+
			"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT ___\n1: #@!(EXPANSION1)#@EXPANSION3{xxxA,xxxB,xxxC}:EXPANSION1: == #@(EXPANSION1)"+
			"#:SPLIT ___\n2: #@!(EXPANSION1)"+
			"#@(EXPAN_X) _ #@EXPANSION3{xA,xxB,xxxC}:EXPANSION1: == #@{a,bb}(EXPAN_X)",
			
			checkMD("1: xxxA == var1"),
			checkMD("1: xxxB == var2"),
			checkMD("1: xxxC == var3"),
			
			checkMD("2: X _ xA == a"),
			checkMD("2: ZZ _ xA == bb"),
			checkMD("2: X _ xxB == a"),
			checkMD("2: ZZ _ xxB == bb"),
			checkMD("2: X _ xxxC == a"),
			checkMD("2: ZZ _ xxxC == bb")
		);
		
		
		// Nested
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@OUTER{.#@(EXPANSION1).,B} -- #@(EXPANSION1)",
			
			checkMD("> .var1. -- var1"),
			checkMD("> .var2. -- var2"),
			checkMD("> .var3. -- var3"),
			checkMD("> B -- var1"), checkMD("> B -- var2"), checkMD("> B -- var3")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@OUTER{.#@(EXPANSION1).,~#@(EXPANSION1)~} -- #@(EXPANSION1)",
			
			checkMD("> .var1. -- var1"),
			checkMD("> .var2. -- var2"),
			checkMD("> .var3. -- var3"),
			checkMD("> ~var1~ -- var1"),
			checkMD("> ~var2~ -- var2"),
			checkMD("> ~var3~ -- var3")
		);
		
		testSourceProcessing("#", 
			"#:HEADER ____\n"+"#@EXPANSION1{var1,var2,var3}"+
			"#:SPLIT\n> #@(EXPANSION1) -- #@OUTER{.#@(EXPANSION1).,B}",
			
			checkMD("> var1 -- .var1."), checkMD("> var1 -- B"), 
			checkMD("> var2 -- .var2."), checkMD("> var2 -- B"),
			checkMD("> var3 -- .var3."), checkMD("> var3 -- B")
		);
		
	}
	
}