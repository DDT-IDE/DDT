/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.ISourceModule;

import mmrnmhrm.core.codeassist.CompletionEngine_Test.CompletionEngineTestsRequestor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.resolver.api.PrefixDefUnitSearchBase.ECompletionResultStatus;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;

public class CompletionEngineSourceTests extends CoreResolverSourceTests {
	
	public CompletionEngineSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	@Override
	public void runFindTest_________(MetadataEntry mde) {
	}
	
	@Override
	public void runFindMissingTest_________(MetadataEntry mde) {
	}
	
	@Override
	public void runFindFailTest_________(MetadataEntry mde) {
	}
	
	@Override
	public void runRefSearchTest(int offset, String searchParams, ECompletionResultStatus expectedStatusCode, 
		String[] expectedResults, String relexStartPosMarker) {
		int rplLen = 0;
		String rplLenStr = searchParams; 
		if(rplLenStr != null) {
			rplLen = Integer.parseInt(rplLenStr);
		}
		
		runRefSearchTest(offset, expectedResults, rplLen);
	}
	
	public void runRefSearchTest(int offset, String[] expectedResults, int rplLen) {
		runCompletionEngineTest((IModuleSource) sourceModule, offset, expectedResults, rplLen);
		if(moduleSource != null) {
			// Run this variation of the test with something that is not a IModuleSource
			assertTrue(!(moduleSource instanceof ISourceModule));
			runCompletionEngineTest(moduleSource, offset, expectedResults, rplLen);
		}
	}
	
	public void runCompletionEngineTest(IModuleSource moduleSource, int offset, String[] expectedResults, int rplLen) {
		DeeCompletionEngine completionEngine;
		completionEngine = CompletionEngine_Test.testCompletionEngine(moduleSource, offset, rplLen);
		
		CompletionEngineTestsRequestor requestor = (CompletionEngineTestsRequestor) completionEngine.getRequestor();
		if(expectedResults != null) {
			checkResults(requestor.results, expectedResults);
		}
	}
	
	@Override
	protected HashSet<String> prepareResultProposals(Collection<DefUnit> results, boolean compareUsingName) {
		for (Iterator<DefUnit> iterator = results.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			if(defUnit.getArcheType() == EArcheType.Module) {
				String fqName = getDefUnitFullyTypedName(defUnit);
				if(fqName.equals("object") || fqName.equals("std.stdio")) {
					iterator.remove();
				}
			}
		}
		HashSet<String> trimedResults = super.prepareResultProposals(results, compareUsingName);
		return trimedResults;
	}
	
}