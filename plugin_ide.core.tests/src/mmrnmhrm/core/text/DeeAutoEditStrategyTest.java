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
package mmrnmhrm.core.text;

import org.eclipse.jface.text.Document;
import org.junit.Test;

import melnorme.lang.ide.core.text.LangDocumentPartitionerSetup;
import melnorme.lang.ide.core.text.format.FormatterIndentMode;
import melnorme.lang.ide.core.text.format.LangAutoEditStrategy;
import melnorme.lang.ide.core.text.format.LangAutoEditStrategyExt.ILangAutoEditsPreferencesAccessExt;
import melnorme.lang.ide.core.text.format.LangAutoEditStrategyTest;

public class DeeAutoEditStrategyTest extends LangAutoEditStrategyTest {
	
	public static class Mock_LangAutoEditsPreferencesAccess implements ILangAutoEditsPreferencesAccessExt {
		@Override
		public boolean isSmartIndent() {
			return true;
		}
		
		@Override
		public boolean isSmartDeIndent() {
			return true;
		}
		
		@Override
		public boolean closeBraces() {
			return true;
		}
		
		@Override
		public boolean closeBlocks() {
			return true;
		}
		
		@Override
		public boolean isSmartPaste() {
			return true;
		}
		
		@Override
		public FormatterIndentMode getTabStyle() {
			return FormatterIndentMode.TAB;
		}
		
		@Override
		public int getIndentSize() {
			return 4;
		}
		
		@Override
		public boolean parenthesesAsBlocks() {
			return false;
		}
	}
	
	@Override
	protected LangAutoEditStrategy getAutoEditStrategy() {
		if(autoEditStrategy == null) {
			/* FIXME: move to LANG */
			autoEditStrategy = new DeeAutoEditStrategy(DeePartitions.DEE_CODE, null, 
				new Mock_LangAutoEditsPreferencesAccess());
		}
		return autoEditStrategy;
	}
	
	@Override
	protected Document createDocument() {
		Document document = super.createDocument();
		LangDocumentPartitionerSetup.getInstance().setupDocument(document);
		return document;
	}
	
	@Test
	public void testSmartIdent_conflictingSyntax() throws Exception { testSmartIdent_conflictingSyntax$(); }
	public void testSmartIdent_conflictingSyntax$() throws Exception {
		// Test conflicting syntax, when brace characters occurr in non-brace token, like strings, comments, etc.
		
		String s;
		int indent = 0;
		
		s = mklast(indent, "abc{ /+}+/"); 
		testEnterAutoEdit(s, NL+line("//{")+line("}")+NEUTRAL_SRC1, expectInd(indent+1));
		
		s = mkline(indent+0, "def {")+
			mklast(indent+7, "/+{+/ }"); 
		testEnterAutoEdit(s, NL + NEUTRAL_SRC1, expectInd(indent+0));
		
		s = mkline(indent+0, "def {")+
			mklast(indent+7, "{ `}` ( `)`"); 
		testEnterAutoEdit(s, NL +")}"+ NEUTRAL_SRC1, expectInd(indent+7+2));
		
		
		s = mkline(indent+0, "def {")+
			mklast(indent+1, "func('blah ({ ' "); 
		testEnterAutoEdit(s, NL +NEUTRAL_SRC1+"//)", expectInd(indent+2), expectClose(indent+2, ")"));
		
		s = mkline(indent+7, "def { func/+({")+
			mklast(indent+0, "  {blah+/ } "); 
		testEnterAutoEdit(s, NL + NEUTRAL_SRC1, expectInd(indent+7));
		
		s = mkline(indent+7, "deffunc{{")+
			mkline(indent+0, "deffunc/+{{")+
			mklast(indent+0, "+/blah}} "); 
		testEnterAutoEdit(s, NL + NEUTRAL_SRC1, expectInd(indent+7));
		
	}
	
}