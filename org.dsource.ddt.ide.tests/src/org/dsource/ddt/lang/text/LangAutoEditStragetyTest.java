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
package org.dsource.ddt.lang.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.tests.ui.DeeUITests;

import org.dsource.ddt.lang.text.BlockHeuristicsScannner.BlockTokenRule;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.util.TabStyle;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.junit.Test;

public class LangAutoEditStragetyTest extends ScannerTestUtils {
	
	public static final String GENERIC_CODE = DeeUITests.readResource("sampledefs.d");
	public static final String NEUTRAL_SRCX = GENERIC_CODE;
	
	protected LangAutoEditStrategy autoEditStrategy;
	
	protected LangAutoEditStrategy getAutoEditStrategy() {
		if(autoEditStrategy == null) {
			PreferenceStore prefStore = createPreferenceStore();
			autoEditStrategy = new LangAutoEditStrategy(prefStore) {
				@Override
				protected BlockHeuristicsScannner createBlockHeuristicsScanner(IDocument doc) {
					BlockTokenRule[] blockTokens = BlockHeuristicsScannnerTest.SAMPLE_BLOCK_TOKENS;
					return new BlockHeuristicsScannner(doc, null, null, blockTokens);
				};
			};
		}
		return autoEditStrategy;
	}
	
	protected PreferenceStore createPreferenceStore() {
		PreferenceStore prefStore = new PreferenceStore();
		prefStore.setValue(LangAutoEditPreferenceConstants.AE_SMART_INDENT, true);
		prefStore.setValue(LangAutoEditPreferenceConstants.AE_SMART_DEINDENT, true);
		prefStore.setValue(LangAutoEditPreferenceConstants.AE_CLOSE_BRACES, true);
		prefStore.setValue(LangAutoEditPreferenceConstants.AE_CLOSE_BRACKETS, true);
		prefStore.setValue(LangAutoEditPreferenceConstants.AE_CLOSE_STRINGS, true);
		prefStore.setValue(PreferenceConstants.EDITOR_SMART_PASTE, true);
		
		prefStore.setValue(CodeFormatterConstants.FORMATTER_TAB_SIZE, 4);
		prefStore.setValue(CodeFormatterConstants.FORMATTER_TAB_CHAR, TabStyle.TAB.getName());
		return prefStore;
	}
	
	protected DocumentCommand createDocumentCommand(int start, int length, String text) {
		DocumentCommand documentCommand = new DocumentCommand() {};
		documentCommand.doit = true;
		documentCommand.text = text;
		
		documentCommand.offset = start;
		documentCommand.length = length;
		
		documentCommand.owner = null;
		documentCommand.caretOffset = -1;
		documentCommand.shiftsCaret = true;
		return documentCommand;
	}
	
	@Test
	public void testSmartIndentBasic() {
		testEnterAutoEdit("void main(){}", "blah", NL); // balance 0 : 0
		
		testEnterAutoEdit("void main(){", NL+"}", NL+TAB); // balance 0 : 1 (closed)
		testEnterAutoEdit("void main(){", "}",    NL+TAB);
	}
	
	@Test
	public void testSmartIndentBasic2() {
		// balance 0 : 1(unclosed)
		testEnterAutoEdit("void main{", ""                             , NL+TAB+NL+"}", (NL+TAB).length());
		testEnterAutoEdit("void main(",      NL+"func(){}"+NL+"blah();", NL+TAB+NL+")", (NL+TAB).length());
		testEnterAutoEdit("vo() main{", "  "+NL+"func(){}"+NL+"blah();", NL+TAB+NL+"}", (NL+TAB).length());
		// balance 0 : 1(unclosed but don't close)
		testEnterAutoEdit("void main(){",       "func(){}"+NL+"blah();", NL+TAB, -1);
	}
	
	@Test
	public void testSmartIndentBasic3() {
		String s;
		
		s = line("func{")+
			TAB+"abc}"; // balance -1 : 0
		testEnterAutoEdit(s, "}"+NEUTRAL_SRC1, NL);

		s = line("func{{")+
			TAB+"abc}}"; // balance -2 : 0
		testEnterAutoEdit(s, "}"+NEUTRAL_SRC1, NL);
		
		s = line(TAB+"func((")+
			TAB+"abc))"; // balance -2 : 0	 '('
		testEnterAutoEdit(s, NEUTRAL_SRC1+")", NL+TAB);
	}
	
	protected final void testEnterAutoEdit(String sourceBefore, String sourceAfter, String expectedEdit) {
		testEnterAutoEdit(sourceBefore, sourceAfter, expectedEdit, -1);
	}
	
	protected void testEnterAutoEdit(String sourceBefore, String sourceAfter, String expectedInsert, int offsetDelta) {
		Document document = setupDocument(sourceBefore, sourceAfter);
		runStrategyAndCheck(sourceBefore, expectedInsert, offsetDelta, document);
	}
	
	protected void runStrategyAndCheck(String sourceBefore, String expectedInsert, int offsetDelta, Document document) {
		int keypressOffset = sourceBefore.length();
		DocumentCommand docCommand = createDocumentCommand(keypressOffset, 0, NL);
		getAutoEditStrategy().customizeDocumentCommand(document, docCommand);
		int caretOffset = (offsetDelta == -1) ? -1 : sourceBefore.length() + offsetDelta;
		checkCommand(docCommand, expectedInsert, keypressOffset, 0, caretOffset);
	}
	
	protected Document setupDocument(String sourceBefore, String sourceAfter) {
		Document document = getDocument();
		document.set(sourceBefore + sourceAfter);
		return document;
	}
	
	protected void checkCommand(DocumentCommand documentCommand, String text, int offset, int length) {
		checkCommand(documentCommand, text, offset, length, -1);
	}
	
	protected void checkCommand(DocumentCommand documentCommand, String text, int offset, int length, int caretOffset) {
		assertEquals(documentCommand.text, text);
		assertTrue(documentCommand.offset == offset);
		assertTrue(documentCommand.length == length);
		assertTrue(documentCommand.caretOffset == caretOffset);
		assertTrue(documentCommand.shiftsCaret == (caretOffset == -1));
	}
	
	
	@Test
	public void testSmartIndent() throws Exception { testSmartIndent$(); }
	public void testSmartIndent$() throws Exception {
		int indent = 0;
		String s;
		
		s = mkline(indent, "func(")+
			mklast(indent, "abc{"); // test 0 : 1
		testEnterAutoEdit(s, NL +"})"+ NEUTRAL_SRC1, expectInd(indent+1));
		
		s = mkline(indent, "func{")+
			mklast(indent, "}abc{"); // test -1 : 1
		testEnterAutoEdit(s, NL +"}"+ NEUTRAL_SRC1, expectInd(indent+1));
		
		indent = 1;
		
		s = mkline(indent, "func{")+
			mklast(indent, "}blah("); // test another -1 : 1   
		testEnterAutoEdit(s, NL +")"+ NEUTRAL_SRCX, expectInd(indent+1));
		
		
		s = mkline(indent, "func{")+
			mklast(indent, "}blah("); // test -1 : 1 with close   
		testEnterAutoEdit(s, NL+NEUTRAL_SRC1, expectClose(indent+1, ")"), expectInd(indent+1).length());
		
		indent = 0;
		s = mkline(indent  , "func{")+
			mkline(indent+4, "func{()}")+ // test interim lines with irregular ident
			mklast(indent+1, "}blah("); // test -1 : 1 with close   
		testEnterAutoEdit(s, NL+NEUTRAL_SRC1, expectClose(indent+2, ")"), expectInd(indent+2).length());
		
		
		
		s = mkline(indent, "func{{{")+
			mklast(indent, TAB+"abc}}}"); // test -3 : 0
		testEnterAutoEdit(s, NL+NEUTRAL_SRCX, expectInd(indent+0));

		s = mkline(indent+7, "func({{{")+  // start block still has : 2 open block
			mklast(indent  , TAB+"abc}}"); // test -2 : 0
		testEnterAutoEdit(s, NL+NEUTRAL_SRCX, expectInd(indent+7+2));

		indent = 0;
		s = mkline(indent, "func")+
			mklast(indent, TAB+"abc}}}"); // test -3 : 0 with zero indent
		testEnterAutoEdit(s, NL+NEUTRAL_SRCX, expectInd(indent+0));
		
		s = mkline(indent, "func")+
			mklast(indent, "abc}}}");     // test -3 : 0 with zero indent
		testEnterAutoEdit(s, NL+NEUTRAL_SRCX, expectInd(indent+0));

		
		s = mkline(indent, "func{{{")+
			mkline(indent+4, "func{()}")+ // test interim lines with irregular ident
			mklast(indent, TAB+"abc}}");  // -2 : 0
		testEnterAutoEdit(s, NL+NEUTRAL_SRC1, expectInd(indent+1)); 
		
		indent = 2;
		s = mkline(indent, NEUTRAL_SRC1)+ // more lines
			mkline(indent, "}}func{{{")+  // matching start block is -2 : 3
			mkline(indent, NEUTRAL_SRC1)+ // more lines
			mkline(indent-2, "func{()}")+ // interim lines with irregular ident (negative)
			mklast(indent, TAB+"abc(blah{}) blah}}"); // -2 : 0
		testEnterAutoEdit(s, NL+NEUTRAL_SRCX, expectInd(indent+1));
	}
	
	protected String mkline(int indent, String string) {
		return line(TABn(indent) + string);
	}
	
	protected String mklast(int indent, String string) {
		return TABn(indent) + string;
	}
	
	protected static String TABn(int indent) {
		return LangAutoEditsPreferencesAdapter.stringNTimes(TAB, indent);
	}
	
	protected static String expectInd(int indent) {
		return NL+TABn(indent);
	}
	
	protected static String expectClose(int indent, String close) {
		return NL+TABn(indent) +NL+TABn(indent-1)+close ;
	}
	
	
	@Test
	public void testSmartIdent_SyntaxErrors() throws Exception { testSmartIdent_SyntaxErrors$(); }
	public void testSmartIdent_SyntaxErrors$() throws Exception {
		String s;
		int indent = 0;
		
		s = mkline(indent, "func")+
			mklast(indent, "abc{"); // test 0 : 1 (with syntax error)
		testEnterAutoEdit(s, NL +"})"+ NEUTRAL_SRC1, expectInd(indent+1));
		
		s = mkline(indent, "func{")+
			mklast(indent, TAB+"{ab(c}"); // test 0 : 0 (corrected)
		testEnterAutoEdit(s, NL +"}"+ NEUTRAL_SRCX, expectInd(1+indent));

		s = mkline(indent, "func{")+
			mklast(indent, TAB+"{ab)c}"); // test 0 : 0 (corrected)
		testEnterAutoEdit(s, NL +"}"+ NEUTRAL_SRC3, expectInd(1+indent));

		indent = 1;
		s = mkline(indent, "func{")+
			mklast(indent, TAB+"(ab{c)"); // test 0 : 2 (corrected)
		testEnterAutoEdit(s, NL +"}"+ NEUTRAL_SRC1, expectInd(1+indent+2));

		s = mkline(indent, "func{")+
			mklast(indent, TAB+"(ab}c)"); // test -1 : 0 (corrected)
		testEnterAutoEdit(s, NL +"}"+ NEUTRAL_SRCX, expectInd(indent));

		
		s = mkline(indent, "func{")+
			mklast(indent, "}blah{)"); // test -1 : 1 (corrected)
		testEnterAutoEdit(s, NL +"}"+ NEUTRAL_SRC3, expectInd(indent+1));
		
		
		s = mkline(indent, "func{")+
			mklast(indent, "}blah{)"); // test -1 : 1 with close   
		testEnterAutoEdit(s, NL+/*}*/ NEUTRAL_SRC1, expectClose(indent+1, "}"), expectInd(indent+1).length());
		
		
		s = mkline(indent, "func{{){")+    // (corrected)
			mklast(indent, TAB+"abc}}(}"); // test -3 : 0 (corrected)
		testEnterAutoEdit(s, NL+NEUTRAL_SRCX, expectInd(indent+0));

		s = mkline(indent, "func{({")+    // (corrected on EOF)
			mklast(indent, TAB+"aaa}})"); // test -3 : 0
		testEnterAutoEdit(s, NL+NEUTRAL_SRC3, expectInd(indent+0));

		s = mkline(indent, "func(")+    // decoy
			mkline(indent+7, "{func{")+ // (corrected on '{' superblock )
			mklast(indent, "aaa})");    
		testEnterAutoEdit(s, NL+NEUTRAL_SRC1, expectInd(indent+7+1));
	}
	
	/* ---------------------------------------*/
	
	public static boolean NOT_DONE = false;
	@Test
	public void testSmartDeIndent() throws Exception { testSmartDeIndent$(); }
	public void testSmartDeIndent$() throws Exception {
		if(true) return; // TODO deIndent
		
		String s;
		int indent = 0;
		
		s = mklast(0, "void main() {");
		testDeIndentAutoEdit(s, NL+TAB, NL+"}"); // Deindent NL 
		
		indent = 1;
		s = NEUTRAL_SRC1+
			mklast(indent, "void main{} (");
		testDeIndentAutoEdit(s, expectInd(indent+1), NL+")"); // Deindent NL 
		
		
		s = NEUTRAL_SRC1+
			mklast(indent, "void main{({");
		testDeIndentAutoEdit(s, expectInd(indent+3), NL+")"); // Deindent NL 
		
		s = NEUTRAL_SRC1+
			mkline(indent+0, "void main{({")+
			mklast(indent+1, "void main()"); // test with 0 : 0 balance
		testDeIndentAutoEdit(s, expectInd(indent+1), NL+"}"); // Deindent NL 
		
		
		s = NEUTRAL_SRC3+
			mklast(indent, "void main{({)(");
		testDeIndentAutoEdit(s, expectInd(indent+3), NL+"}"); // Deindent NL 
		
	}
	
	
	protected void testDeIndentAutoEdit(String srcPre, String srcIndent, String sourceAfter) {
		DocumentCommand bsCommand = applyBackSpaceCommand(srcPre + srcIndent, sourceAfter);
		checkCommand(bsCommand, "", srcPre.length(), srcIndent.length());
		
		DocumentCommand delCommand = applyDelCommand(srcPre, srcIndent + sourceAfter);
		checkCommand(delCommand, "", srcPre.length(), srcIndent.length());
		
		testBackSpaceCommandWithNoEffect(srcPre + srcIndent +TAB, sourceAfter );
		
		assertTrue(srcIndent.length() > 2);
		// AutoEdit should not apply in the middle of indent element, test that
		String srcPre2 = srcPre + srcIndent.substring(0, srcIndent.length()-1);
		String srcAfter2 = srcIndent.substring(srcIndent.length()-1, srcIndent.length()) + sourceAfter;
		testBackSpaceCommandWithNoEffect(srcAfter2, srcPre2);
		testDeleteCommandWithNoEffect(srcPre2, srcAfter2);
	}
	
	protected DocumentCommand applyBackSpaceCommand(String srcPre, String sourceAfter) {
		getDocument().set(srcPre + sourceAfter);
		int keypressOffset = srcPre.length();
		DocumentCommand docCommand = createDocumentCommand(keypressOffset-1, 1, "");
		getAutoEditStrategy().customizeDocumentCommand(getDocument(), docCommand);
		return docCommand;
	}
	
	protected DocumentCommand applyDelCommand(String srcPre, String sourceAfter) {
		getDocument().set(srcPre + sourceAfter);
		int keypressOffset = srcPre.length();
		DocumentCommand docCommand = createDocumentCommand(keypressOffset, 1, "");
		getAutoEditStrategy().customizeDocumentCommand(getDocument(), docCommand);
		return docCommand;
	}
	
	protected void testBackSpaceCommandWithNoEffect(String sourcePre, String sourceAfter) {
		DocumentCommand bsCommand = applyBackSpaceCommand(sourcePre, sourceAfter);
		checkCommand(bsCommand, "", sourcePre.length()-1, 1);
	}
	
	protected void testDeleteCommandWithNoEffect(String sourcePre, String sourceAfter) {
		DocumentCommand delCommand = applyDelCommand(sourcePre, sourceAfter);
		checkCommand(delCommand, "", sourcePre.length(), 1);
	}
	
}
