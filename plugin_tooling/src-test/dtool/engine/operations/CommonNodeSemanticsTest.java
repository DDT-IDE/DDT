/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.operations;


import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.engine.modules.NullModuleResolver;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.resolver.DefUnitResultsChecker;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.tests.CommonDToolTest;

public class CommonNodeSemanticsTest extends CommonDToolTest {
	
	protected Module parseSource(String source) {
		DeeTestsChecksParser parser = new DeeTestsChecksParser(source);
		return parser.parseModule("_tests", null).getNode();
	}
	
	protected ASTNode parseSourceAndPickNode(String source, int offset) {
		Module module = parseSource(source);
		return ASTNodeFinder.findElement(module, offset);
	}
	
	protected void testResolveSearchInMembersScope(INamedElement defVar, String... expectedResults) {
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(null, 0, new NullModuleResolver());
		defVar.resolveSearchInMembersScope(search);
		
		DefUnitResultsChecker resultsChecker = new DefUnitResultsChecker(search.getResults());
		resultsChecker.removeIgnoredDefUnits(true, true);
		resultsChecker.checkResults(expectedResults);
	}
	
}