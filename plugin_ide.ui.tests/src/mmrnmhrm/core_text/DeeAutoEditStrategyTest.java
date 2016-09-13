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
package mmrnmhrm.core_text;

import org.eclipse.jface.text.Document;
import org.junit.Test;

import melnorme.lang.ide.core.TextSettings_Actual.LangPartitionTypes;
import melnorme.lang.ide.core.text.format.ILastKeyInfoProvider.KeyCommand;
import melnorme.lang.ide.core.text.format.LangAutoEditStrategy;
import melnorme.lang.ide.core_text.LangAutoEditStrategyTest;
import melnorme.lang.ide.core_text.LangDocumentPartitionerSetup;
import mmrnmhrm.core.text.DeeAutoEditStrategy;

public class DeeAutoEditStrategyTest extends LangAutoEditStrategyTest {
	
	@Override
	protected LangAutoEditStrategy getAutoEditStrategy() {
		if(autoEditStrategy == null) {
			lastKeyInfoProvider.lastPressedKey = KeyCommand.OTHER;
			autoEditStrategy = new DeeAutoEditStrategy(LangPartitionTypes.DEE_CODE.getId(), 
				new Mock_LangAutoEditsPreferencesAccess(), lastKeyInfoProvider);
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
		
		s = TABn(indent) + "abc{ /+}+/"; 
		testEnterAutoEdit(s, NL+line("//{")+line("}")+NEUTRAL_SRC1, TABn(indent+1));
		
		s = line("def {")+
			TABn(indent+7) + "/+{+/ }"; 
		testEnterAutoEdit(s, NL + NEUTRAL_SRC1, TABn(indent+0));
		
		s = line("def {")+
			TABn(indent+7) + "{ `}` ( `)`"; 
		testEnterAutoEdit(s, NL +")}"+ NEUTRAL_SRC1, TABn(indent+7+2));
		
		
		s = line("def {")+
			TABn(indent+1) + "func('blah ({ ' "; 
		testEnterEdit(s, NL +NEUTRAL_SRC1+"//)", TABn(indent+2), expectClose(indent+2, ")"));
		
		s = line(TABn(7) + "def { func/+({")+
			TABn(indent+0) + "  {blah+/ } "; 
		testEnterAutoEdit(s, NL + NEUTRAL_SRC1, TABn(indent+7));
		
		s = line(TABn(7) + "deffunc{{")+
			line("deffunc/+{{")+
			TABn(indent+0) + "+/blah}} "; 
		testEnterAutoEdit(s, NL + NEUTRAL_SRC1, TABn(indent+7));
		
	}
	
}