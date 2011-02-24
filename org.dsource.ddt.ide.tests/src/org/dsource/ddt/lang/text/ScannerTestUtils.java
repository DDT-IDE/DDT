package org.dsource.ddt.lang.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.jface.text.Document;



public class ScannerTestUtils {
	
	protected static final String NL = "\r\n";
	protected static final String TAB = "\t";
	
	public static final String NEUTRAL_SRC1 = 
		line("void func() {")+
		line(TAB+"blah();")+
		line(TAB+"blah2([1, 2, 3]);")+
		line("}")
		;
	public static final String NEUTRAL_SRC2 = NEUTRAL_SRC1; // TODO: should write some more
	public static final String NEUTRAL_SRC3 = NEUTRAL_SRC1; // TODO: should write some more
	
	public static String line(String string) {
		return string+NL;
	}
	
	
	protected Document document;
	
	protected Document getDocument() {
		if(document == null) {
			document = new Document();
			assertTrue(ArrayUtil.contains(document.getLegalLineDelimiters(), NL));
		}
		return document;
	}
	
}