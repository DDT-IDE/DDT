package org.dsource.ddt.lang.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import mmrnmhrm.ui.internal.text.BlockHeuristicsScannner;
import mmrnmhrm.ui.internal.text.IDeeHeuristicSymbols;
import mmrnmhrm.ui.internal.text.BlockHeuristicsScannner.BlockTokenRule;

import org.eclipse.jface.text.BadLocationException;
import org.junit.Test;


public class BlockHeuristicsScannnerTest extends ScannerTestUtils {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testScanToBlockStart("", "{blah", "}", 0);
		testScanToBlockStart("", "blah", "}", 1);
		testScanToBlockStart("", "(blah", ")", 0);
		testScanToBlockStart("", "blah", ")", 1);
		
		testScanToBlockStart("", "{", "}", 0);
		testScanToBlockStart("", "", "}", 1);
	}
	
	public BlockHeuristicsScannner setupScanner(String srcBefore, String srcAfter) {
		BlockTokenRule[] ar = new BlockTokenRule[] {
			new BlockTokenRule('{', '}'),
			new BlockTokenRule('(', ')')
		};
		BlockHeuristicsScannner scanner = new BlockHeuristicsScannner(getDocument(), ar);
		
		getDocument().set(srcBefore + srcAfter);
		return scanner;
	}
	
	protected void testScanToBlockStart(String srcPre, String scrBlock, String srcAfter, int expecBalance)
			throws BadLocationException {
		testScanToBlockStart(srcPre, scrBlock, srcAfter, expecBalance, false);
	}
	
	protected void testScanToBlockStart(String srcPre, String srcBlock, String srcAfter, int expecBalance,
			boolean expecInvalidLeft)
			throws BadLocationException {
		String srcBefore = srcPre + srcBlock;
		BlockHeuristicsScannner scanner = setupScanner(srcBefore, srcAfter);
		char closeChar = srcAfter.charAt(0);
		assertNotNull(scanner.getOpeningPeer(closeChar));
		
		int balance = scanner.scanToBlockStart(srcBefore.length());
		
		assertTrue(scanner.getPosition() == srcPre.length());
		assertTrue(balance == expecBalance);
		assertTrue((scanner.token == IDeeHeuristicSymbols.TOKEN_INVALID) == expecInvalidLeft);
		
		if(balance == 0) {
			int blockEndOffset = srcBefore.length();
			if (scanner.token != IDeeHeuristicSymbols.TOKEN_INVALID) {
				scanner.scanToBlockEnd(scanner.getPosition());
				assertEquals(scanner.getPosition(), blockEndOffset);
			} else {
				scanner.scanToBlockEnd(scanner.getPosition()-1);
				assertTrue(scanner.getPosition() == document.getLength()-1 || scanner.getPosition() > blockEndOffset);
			}
		}
	}
	
	public static final boolean LEFT_INVALID = true; 
	
	@Test
	public void testScanToBlockStart() throws Exception { testScanToBlockStart$(); }
	public void testScanToBlockStart$() throws Exception {
		testScanToBlockStart("{{", "{blah", "}", 0);
		testScanToBlockStart("}}", "{blah", "} {}", 0);
		
		testScanToBlockStart("", "}}aaaa", "}" +NEUTRAL_SRC1, 3);
		testScanToBlockStart("", "} {abc(foo) blah;} }} aaaa", "}" +NEUTRAL_SRC2, 4);
		testScanToBlockStart("", "} (abc{foo} blah;) }} aaaa", "}" +NEUTRAL_SRC2, 4);
		
		testScanToBlockStart(NEUTRAL_SRC1+"{{() ({", "{blah(aaa)", "}", 0);
		
		testScanToBlockStart(NEUTRAL_SRC2+"{{() ({", "{blah((aaa { (asd) }  ))", "}", 0);
		
		// Now some syntax errors:
		testScanToBlockStart(NEUTRAL_SRC1+"{(", "{ )", "}", 0);
		testScanToBlockStart(NEUTRAL_SRC1+"{(", "{ (", "}", 0);
		
		testScanToBlockStart(NEUTRAL_SRC2+"{(", "( {( }", ")", 0);
		testScanToBlockStart(NEUTRAL_SRC2+"{", "", ")", 0, LEFT_INVALID);
		testScanToBlockStart(NEUTRAL_SRC2+"{", "   ", ")", 0, LEFT_INVALID);
		testScanToBlockStart(NEUTRAL_SRC2+"{", "   ))", ")", 0, LEFT_INVALID);

		testScanToBlockStart("", "   ()", "}", 1);
		testScanToBlockStart("", "   ) (", "}", 2);
		testScanToBlockStart("(({", "   ()", ")", 0, LEFT_INVALID);
		testScanToBlockStart("(({", "   ) [", ")", 0, LEFT_INVALID);
		
		testScanToBlockStart(NEUTRAL_SRC1+"(", "(({)) ))})", ")", 0);
		testScanToBlockStart(NEUTRAL_SRC1+"(", "( {(( ((} ", ")", 0);
		testScanToBlockStart(NEUTRAL_SRC1+"(({", "  {} {()} ) {(( ((} ", ")", 0, LEFT_INVALID);
		
	}
	
}
