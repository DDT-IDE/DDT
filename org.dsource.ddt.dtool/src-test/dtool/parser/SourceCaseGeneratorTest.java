/*******************************************************************************
 * Copyright (c) 2012, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package dtool.parser;

import org.junit.Test;

import dtool.tests.AnnotatedSource;
import dtool.tests.CommonTestUtils;
import dtool.tests.AnnotatedSource.MetadataEntry;

public class SourceCaseGeneratorTest extends CommonTestUtils {
	
	protected TemplatedSourceProcessor tsp;
	
	public void testProcessSource(String source, String marker, GeneratedSourceChecker... checkers) {
		tsp = TemplatedSourceProcessor.processSource(source, marker);
		visitCollection(tsp.getGenCases(), checkers);
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
		assertAreEqual(mde1.extraValue, mde2.extraValue);
		assertAreEqual(mde1.associatedSource, mde2.associatedSource);
		assertAreEqual(mde1.sourceRange, mde2.sourceRange);
	}
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		
		testProcessSource("asdf ## #{,#},#,,#NL,##, ,line}==", "#",
			
			checkMD("asdf # =="),
			checkMD("asdf # }=="),
			checkMD("asdf # ,=="),
			checkMD("asdf # \r=="),
			checkMD("asdf # \n=="),
			checkMD("asdf # \r\n=="),
			checkMD("asdf # #=="),
			checkMD("asdf #  =="),
			checkMD("asdf # line==")
		);
	}
	
	@Test
	public void testEOF() throws Exception { testEOF$(); }
	public void testEOF$() throws Exception {
		testProcessSource("asdf ## #EOF #{##}==", "#",
			
			checkMD("asdf # ")
		);
	}
	
	
	@Test
	public void testMetadata() throws Exception { testMetadata$(); }
	public void testMetadata$() throws Exception {
		testProcessSource("asdf ## #@error:info1{xxx}==", "#",
			
			checkMD("asdf # xxx==", new MetadataEntry("error", "info1", "xxx", 7))
		);
		
		testProcessSource("asdf ## #@error:info1==", "#",
					
			checkMD("asdf # ==", new MetadataEntry("error", "info1", null, 7))
		);
		
		testProcessSource("asdf ## #@error==", "#",
			
			checkMD("asdf # ==", new MetadataEntry("error", null, null, 7))
		);
		
		testProcessSource("asdf ## #@error{xxx}==", "#",
			
			checkMD("asdf # xxx==", new MetadataEntry("error", null, "xxx", 7))
		);
		
		
		testProcessSource("## abcdef #//KEY_ONE:value\n=extra value\n blah", "#",
			
			checkMD("# abcdef ", new MetadataEntry("KEY_ONE", "value", "=extra value\n blah", -1))
		);
		
	}
	
//	@Test
//	public void testMetadataInVariation() throws Exception { testMetadataInVariation$(); }
//	public void testMetadataInVariation$() throws Exception {
//		testProcessSource("asdf ## #{,#@error:info1{xxx},#@meta2{blah}}==", "#",
//		
//			checkMD("asdf # =="),
//			checkMD("asdf # xxx==", new MetadataEntry("error", "info1", "xxx", 7)),
//			checkMD("asdf # blah==", new MetadataEntry("meta2", null, "blah", 7))
//		);
//	}
	
	@Test
	public void testMetaDataAfterEOF() throws Exception { testMetaDataAfterEOF$(); }
	public void testMetaDataAfterEOF$() throws Exception {
		testProcessSource("asdf ## =#EOF #@error:info1{xxx}==", "#",
			
			checkMD("asdf # =", new MetadataEntry("error", "info1", "xxx", -1))
		);
		
		
		testProcessSource("abc#EOF #//KEY_ONE:value\n=extra value\n blah", "#",
			
			checkMD("abc", new MetadataEntry("KEY_ONE", "value", "=extra value\n blah", -1))
		);
	}
	
}