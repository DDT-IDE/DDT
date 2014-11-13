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

import melnorme.lang.tooling.bundles.MockSemanticResolution;
import dtool.ast.references.NamedReference;
import dtool.engine.operations.CodeCompletionOperation;
import dtool.engine.operations.CompletionSearchResult;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.resolver.ReferenceResolver.DirectDefUnitResolve;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.util.NewUtils;

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
			mr = new MockSemanticResolution();
			return;
		}
		TestsSimpleModuleResolver existingMR = moduleResolvers.get(projectFolderName);
		if(existingMR == null) {
			File projectFolder = getProjectDirectory(projectFolderName);
			existingMR = new TestsSimpleModuleResolver(projectFolder);
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
		DirectDefUnitResolve resolveResult = resolveAtOffset(mde.offset);
		assertTrue(resolveResult.pickedRef == null || resolveResult.invalidPickRef);
	}
	
	public DirectDefUnitResolve resolveAtOffset(int offset) {
		return ReferenceResolver.resolveAtOffset(parseResult, offset, mr);
	}
	
	@Override
	protected void runFindTest_________(MetadataEntry mde) {
		doFindTest(mde);
	}
	
	public DirectDefUnitResolve doFindTest(MetadataEntry mde) {
		String[] expectedResults = splitValues(mde.sourceValue);
		return doRunFindTest(mde.offset, expectedResults);
	}
	
	@Override
	public void runFindMissingTest_________(MetadataEntry mde) {
		assertTrue(mde.sourceValue == null);
		DirectDefUnitResolve result = doRunFindTest(mde.offset, NewUtils.EMPTY_STRING_ARRAY);
		assertTrue(result.resolvedDefUnits == null);
		assertTrue(result.pickedRef instanceof NamedReference);
		NamedReference pickedRef_named = (NamedReference) result.pickedRef;
		assertTrue(pickedRef_named.isMissingCoreReference());
	}
	
	public DirectDefUnitResolve doRunFindTest(int offset, String[] expectedResults) {
		DirectDefUnitResolve resolveResult = resolveAtOffset(offset);
		checkResults(resolveResult.getResolvedDefUnits(), expectedResults, false, false);
		return resolveResult;
	}
	
}