/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

import dtool.ast.ASTNode;
import dtool.ast.definitions.Module;

public class DeeParserResult {
	
	public final String source;
	public final List<LexElement> tokenList;
	public final ASTNode node;
	public final boolean ruleBroken;
	public final Module module;
	public final List<ParserError> errors;
	
	public DeeParserResult(String source, AbstractList<LexElement> tokenList, ASTNode node, boolean ruleBroken,
		List<ParserError> errors) {
		this.source = source;
		this.tokenList = Collections.unmodifiableList(tokenList);
		this.node = node;
		this.ruleBroken = ruleBroken;
		this.module = node instanceof Module ? (Module) node : null;
		this.errors = Collections.unmodifiableList(errors);
		assertTrue(node == null || node.getData().isLocallyAnalyzedStatus());
	}
	
	public boolean hasSyntaxErrors() {
		return errors != null && errors.size() > 0;
	}
	
	public Module getParsedModule() {
		assertNotNull(module);
		return module;
	}
	
	public List<ParserError> getErrors() {
		return errors;
	}
	
	public static class ParsedModule extends DeeParserResult {
		
		public ParsedModule(String source, AbstractList<LexElement> tokenList, Module node, boolean ruleBroken,
				List<ParserError> errors) {
			super(source, tokenList, node, ruleBroken, errors);
		}
		
	}
	
}