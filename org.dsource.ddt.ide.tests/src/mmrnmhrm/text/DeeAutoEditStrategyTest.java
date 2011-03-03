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
package mmrnmhrm.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import mmrnmhrm.ui.internal.text.DeeAutoEditStrategy;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.DeeTextTestUtils;

import org.dsource.ddt.lang.text.LangAutoEditStragetyTest;
import org.dsource.ddt.lang.text.LangAutoEditStrategy;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITypedRegion;
import org.junit.Test;

public class DeeAutoEditStrategyTest extends LangAutoEditStragetyTest {
	
	@Override
	protected LangAutoEditStrategy getAutoEditStrategy() {
		if(autoEditStrategy == null) {
			autoEditStrategy = new DeeAutoEditStrategy(createPreferenceStore());
		}
		return autoEditStrategy;
	}
	
	@Override
	protected Document createDocument() {
		Document document = super.createDocument();
		DeeTextTestUtils.installDeePartitioner(document);
		return document;
	}
	
	@Override
	protected Document setupDocument(String sourceBefore, String sourceAfter) {
		Document document = super.setupDocument(sourceBefore, sourceAfter);
		ITypedRegion partition;
		try {
			partition = document.getPartition(DeePartitions.DEE_PARTITIONING, sourceBefore.length(), false);
		} catch (Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		assertEquals(partition.getType(), DeePartitions.DEE_CODE);
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
