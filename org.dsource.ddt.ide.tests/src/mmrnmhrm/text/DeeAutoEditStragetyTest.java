package mmrnmhrm.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.tests.ui.DeeUITests;
import mmrnmhrm.ui.internal.text.DeeAutoEditStrategy;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.util.TabStyle;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.junit.Test;

import dtool.tests.DeeTestUtils;

public class DeeAutoEditStragetyTest extends DeeTestUtils {
	
	private static final String NL = "\r\n";
	private static final String TAB = "\t";
	
	private static final String GENERIC_CODE = DeeUITests.readResource("sampledefs.d");
	
	protected DeeAutoEditStrategy autoEditStrategy;
	protected Document document;
	
	protected Document getDocument() {
		if(document == null) {
			document = new Document();
			assertTrue(ArrayUtil.contains(document.getLegalLineDelimiters(), NL));
		}
		return document;
	}
	
	protected DeeAutoEditStrategy getAutoEditStrategy() {
		if(autoEditStrategy == null) {
			PreferenceStore prefStore = new PreferenceStore();
			prefStore.setValue(PreferenceConstants.EDITOR_SMART_INDENT, true);
			prefStore.setValue(PreferenceConstants.EDITOR_SMART_PASTE, true);
			prefStore.setValue(PreferenceConstants.EDITOR_CLOSE_BRACES, true);
			
			prefStore.setValue(CodeFormatterConstants.FORMATTER_TAB_SIZE, 4);
			prefStore.setValue(CodeFormatterConstants.FORMATTER_TAB_CHAR, TabStyle.TAB.getName());
			autoEditStrategy = new DeeAutoEditStrategy(DeePartitions.DEE_PARTITIONING, prefStore);
		}
		return autoEditStrategy;
	}
	
	public static DocumentCommand createDocumentCommand(int start, int length, String text) {
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
		testEnterAutoEdit("void main(){", "", NL+TAB+NL+"}", -1);
		testEnterAutoEdit("void main(){", "}", NL+TAB+NL, -1);
	}
	
	private void testEnterAutoEdit(String sourceText, String sourceAfter, String expectedEdit, int caretOffset) {
		int keypressOffset = sourceText.length();
		Document document = getDocument();
		document.set(sourceText + sourceAfter);
		DocumentCommand docCommand = createDocumentCommand(keypressOffset, 0, NL);
		getAutoEditStrategy().customizeDocumentCommand(document, docCommand);
		checkCommand(docCommand, expectedEdit, keypressOffset, 0, caretOffset);
	}
	
	protected void checkCommand(DocumentCommand documentCommand, String text, int offset, int length, int caretOffset) {
		assertEquals(documentCommand.text, text);
		assertTrue(documentCommand.offset == offset);
		assertTrue(documentCommand.length == length);
		assertTrue(documentCommand.caretOffset == caretOffset);
		assertTrue(documentCommand.shiftsCaret == (caretOffset != -1));
	}
	
	
	@Test
	public void testSmartIndent() throws Exception { testSmartIndent$(); }
	public void testSmartIndent$() throws Exception {
		testScenarios("void main","");
		testScenarios(TAB+"void main",TAB);
		testScenarios("{ \n\t blah() }"+NL+"\t  void main","\t  ");
		testScenarios("{ \n\t blah() {"+NL+"\t\tvoid main","\t\t");
		testScenarios("} \n\t blah() }"+NL+"\t  void main","\t  ");
	}
	
	private void testScenarios(String preSource, String firstIndent) {
		String ___ = firstIndent+TAB; // The expected indent
		testAddIndentCase(preSource + "{", ___, "}");
		
		testAddIndentCase(preSource + "(){{", ___ + ___, "}");
		
		testAddIndentCase(preSource + "(", ___, ")");
		
		testAddIndentCase(preSource + "({) {", ___ + ___, "}"); // ???
		
		
		testReduceIndent(preSource, ___);
	}
	
	private void testAddIndentCase(String sourceText, String ___, String newB) {
		testEnterAutoEdit(sourceText    , "", NL+___+NL+newB, -1);
		testEnterAutoEdit(sourceText+" ", "", NL+___+NL+newB, -1);
		
		testEnterAutoEdit(sourceText    , newB, NL+___+NL, -1);
		testEnterAutoEdit(sourceText+"\t", newB, NL+___+NL, -1);
		testEnterAutoEdit(sourceText, NL+newB, NL+___, -1);
	}
	
	private void testReduceIndent(String preSource, String ___) {
		String ___1 = reduceIndent(___, 1);
		String ___2 = reduceIndent(___, 2);
		testEnterAutoEdit(preSource + "}", "", NL+___1, -1);
		testEnterAutoEdit(preSource + "}", "{", NL+___1, -1);
		testEnterAutoEdit(preSource + "})", "{ blah(", NL+___2, -1);
	}
	
	private String reduceIndent(String indent, int level) {
		if(indent.length() == 0 || level == 0){
			return indent;
		}
		return reduceIndent(indent.replaceFirst("( ? ? ?\t)|(    )", indent), level-1);
	}
	
	@Test
	public void testSmartDeIdent() throws Exception { testSmartDeIdent$(); }
	public void testSmartDeIdent$() throws Exception {
		
//		testAutoEdit("void main{"+NL+TAB, "{", NL+___1, -1);
		testBackspaceAutoEdit("void main() {", NL+TAB, NL+"}"); 
		
		testBackspaceAutoEdit(TAB+"void main() {", NL+TAB+TAB, "}");
		testBackspaceAutoEdit(GENERIC_CODE+ TAB+"void main() {",NL+TAB+TAB+(TAB), NL+"}", (TAB).length());
	}
	
	
	private void testBackspaceAutoEdit(String srcPre, String srcIdent, String sourceAfter) {
		testBackspaceAutoEdit(srcPre, srcIdent, sourceAfter, srcIdent.length());
	}

	private void testBackspaceAutoEdit(String srcPre, String srcIdent, String sourceAfter, int indentLen) {
		Document document = getDocument();
		document.set(srcPre + srcIdent + sourceAfter);
		int keypressOffset = srcPre.length() + indentLen;
		DocumentCommand docCommand = createDocumentCommand(keypressOffset-1, 1, ""); //Backspace
		getAutoEditStrategy().customizeDocumentCommand(document, docCommand);
		checkCommand(docCommand, "", srcPre.length(), indentLen, -1);
	}
	
	@Test
	public void testNoAutoEdit() throws Exception { testNoAutoEdit$(); }
	public void testNoAutoEdit$() throws Exception {
		// TODO: test cases which should not creat any auto-edit 
	}
	
}
