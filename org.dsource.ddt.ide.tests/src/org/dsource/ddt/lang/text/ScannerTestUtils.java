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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;



public class ScannerTestUtils {
	
	protected static final String NL = "\r\n";
	protected static final String TAB = "\t";
	
	public static final String NEUTRAL_SRC1 = 
		line("void func() {")+
		line(TAB+"blah();")+
		line(TAB+"blah2([1, 2, 3]);")+
		line("}")
		;
	public static final String NEUTRAL_SRC2 = NEUTRAL_SRC1; // TODO: should write some sample code
	public static final String NEUTRAL_SRC3 = NEUTRAL_SRC1;
	
	public static String line(String string) {
		return string+NL;
	}
	
	
	protected Document document;
	
	protected Document getDocument() {
		if(document == null) {
			document = createDocument();
		}
		return document;
	}
	
	protected Document createDocument() {
		Document document = new Document();
		assertTrue(ArrayUtil.contains(document.getLegalLineDelimiters(), NL));
		return document;
	}
	
	public static FastPartitioner installPartitioner(Document document, String partitioning,
			IPartitionTokenScanner partitionScanner, String[] legalContentTypes) {
		partitionScanner.setRange(document, 0, document.getLength());
		
		FastPartitioner fp = new FastPartitioner(partitionScanner, legalContentTypes);
		fp.connect(document, false);
		document.setDocumentPartitioner(partitioning, fp);
		return fp;
	}
	
}