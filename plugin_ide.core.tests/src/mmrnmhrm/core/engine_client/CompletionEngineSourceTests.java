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
package mmrnmhrm.core.engine_client;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import mmrnmhrm.core.engine_client.CompletionEngine_Test.CompletionEngineTestsRequestor;

import org.eclipse.dltk.compiler.env.IModuleSource;

import dtool.ast.definitions.EArcheType;
import dtool.ast.util.NamedElementUtil;
import dtool.engine.common.IDeeNamedElement;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.MockCompilerInstalls;

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
	public void runRefSearchTest_________(RefSearchOptions options) {
		IModuleSource sourceModule_cast = (IModuleSource) sourceModule;
		runCompletionEngineTest(sourceModule_cast, options.offset, options.expectedResults, options.rplLen);
	}
	
	public void runCompletionEngineTest(IModuleSource moduleSource, int offset, String[] expectedResults, int rplLen) {
		DeeCompletionEngine completionEngine;
		completionEngine = CompletionEngine_Test.testCompletionEngine(moduleSource, offset, rplLen,
			MockCompilerInstalls.EMPTY_COMPILER_INSTALL);
		
		CompletionEngineTestsRequestor requestor = (CompletionEngineTestsRequestor) completionEngine.getRequestor();
		checkResults(requestor.results, expectedResults);
	}
	
	@Override
	public void removeDefUnitsFromExpected(Collection<IDeeNamedElement> resultDefUnits) {
		for (Iterator<IDeeNamedElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
			IDeeNamedElement defElement = iterator.next();
			
			if(defElement.getArcheType() == EArcheType.Module) {
				String fqName = NamedElementUtil.getElementTypedQualification(defElement);
				if(fqName.equals("object/") || fqName.equals("std.stdio/")) {
					iterator.remove();
				}
			}
		}
	}
	
}