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
package mmrnmhrm.ui.text;

import melnorme.lang.ide.core.text.LangDocumentPartitionerSetup;
import melnorme.lang.ide.core.text.Scanner_BaseTest;
import mmrnmhrm.core.text.DeePartitionScanner;
import mmrnmhrm.core.text.DeePartitions;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.FastPartitioner;

public class DeeTextTestUtils {
	
	public static final String[] LEGAL_CONTENT_TYPES = LangDocumentPartitionerSetup.LEGAL_CONTENT_TYPES; // accessor
	
	public static FastPartitioner installDeePartitioner(Document document) {
		DeePartitionScanner partitionScanner = new DeePartitionScanner();
		String partitioning = DeePartitions.PARTITIONING_ID;
		return Scanner_BaseTest.installPartitioner(document, partitioning, partitionScanner, LEGAL_CONTENT_TYPES);
	}
}
