/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.MiscUtil.nullToOther;

import java.io.File;

import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.utilbox.misc.Location;
import dtool.ast.references.NamedReference;
import dtool.engine.operations.CodeCompletionOperation;
import dtool.engine.operations.FindDefinitionOperation;
import dtool.engine.operations.FindDefinitionResult;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;

public class ResolverSourceTests extends BaseResolverSourceTests {
	
	public ResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	protected ParsedModule parseResult;
	
	@Override
	public void prepareTestCase(String moduleName, String projectFolderName, AnnotatedSource testCase) {
		moduleName = nullToOther(moduleName, DEFAULT_MODULE_NAME);
		parseResult = DeeParser.parseSource(testCase.source, moduleName);
		
		if(projectFolderName == null || projectFolderName.isEmpty()) {
			mr = new EmptySemanticResolution();
			return;
		}
		TestsSimpleModuleResolver existingMR = moduleResolvers.get(projectFolderName);
		if(existingMR == null) {
			File projectFolder = getProjectDirectory(projectFolderName);
			existingMR = new TestsSimpleModuleResolver(Location.create_fromValid(projectFolder.toPath()));
			moduleResolvers.put(projectFolderName, existingMR); // Cache the MR data
		}
		if(moduleName != null) {
			existingMR.setExtraModule(moduleName, parseResult);
		} else {
			existingMR.setExtraModule(null, null);
		}
		mr = existingMR;
		assertNotNull(mr);
	}
	
	@Override
	public void runRefSearchTest_________(RefSearchOptions options) {
		
		CompletionSearchResult completion = CodeCompletionOperation.completionSearch(parseResult, options.offset, mr);
		
		assertEquals(completion.getResultCode(), options.expectedStatusCode);
		assertEquals(completion.getReplaceLength(), options.rplLen);
		checkResults(completion.getResults(), options.expectedResults);
	}
	
	@Override
	protected void runFindFailTest_________(MetadataEntry mde) {
		FindDefinitionResult findDefResult = resolveAtOffset(mde.offset);
		assertTrue(!findDefResult.isValidPickRef());
	}
	
	public FindDefinitionResult resolveAtOffset(int offset) {
		return FindDefinitionOperation.findDefinition(parseResult.module, offset, mr);
	}
	
	@Override
	public void runFindMissingTest_________(MetadataEntry mde) {
		assertTrue(mde.sourceValue == null);
		FindDefinitionResult findDefResult = resolveAtOffset(mde.offset);
		assertTrue(findDefResult.isValidPickRef() == false);
		assertTrue(findDefResult.pickedReference instanceof NamedReference);
		NamedReference pickedRef_named = (NamedReference) findDefResult.pickedReference;
		assertTrue(pickedRef_named.isMissingCoreReference());
	}
	
	@Override
	protected void runFindTest_________(MetadataEntry mde) {
		doFindTest(mde);
	}
	
	public void doFindTest(MetadataEntry mde) {
		String[] expectedResults = splitValues(mde.sourceValue);
		FindDefinitionResult findDefResult = resolveAtOffset(mde.offset);
		assertTrue(findDefResult.isValidPickRef());
		checkResults(findDefResult.resultsRaw, expectedResults, false, false, false);
		resolveAtOffset(mde.offset);
	}
	
}