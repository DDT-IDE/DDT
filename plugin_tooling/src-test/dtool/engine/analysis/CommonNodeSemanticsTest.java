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
import dtool.ast.definitions.Module;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.tests.CommonDToolTest;

public class CommonNodeSemanticsTest extends CommonDToolTest {
	
	public static final String DEFAULT_MODULE = "_tests";
	
	protected static Module parseSource(String source) {
		DeeTestsChecksParser parser = new DeeTestsChecksParser(source);
		return parser.parseModule(DEFAULT_MODULE, null).getNode();
	}
	
	protected static ASTNode parseSourceAndPickNode(String source, int offset) {
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
	
	public static <T> T parseSourceAndPickNode(String source, int offset, Class<T> klass) {
		ASTNode node = parseSourceAndPickNode(source, offset);
		return getMatchingParent(node, klass);
	}
	
}