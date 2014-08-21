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
package dtool.engine.analysis;


import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.Expression;
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
	
	@SuppressWarnings("unchecked")
	public static <T> T getMatchingParent(ASTNode node, Class<T> klass) {
		if(node == null) {
			return null;
		}
		
		if(klass.isInstance(node)) {
			return (T) node;
		}
		return getMatchingParent(node.getParent(), klass);
	}
	
	public Expression parseSourceAndPickNode(String source, int offset, Class<Expression> klass) {
		ASTNode node = parseSourceAndPickNode(source, offset);
		return getMatchingParent(node, klass);
	}
	
	protected static void testResolveSearchInMembersScope(INamedElement defVar, String... expectedResults) {
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(null, 0, new NullModuleResolver());
		defVar.resolveSearchInMembersScope(search);
		
		DefUnitResultsChecker resultsChecker = new DefUnitResultsChecker(search.getResults());
		resultsChecker.removeIgnoredDefUnits(true, true);
		resultsChecker.checkResults(expectedResults);
	}
	
}