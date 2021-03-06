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

import java.nio.file.Path;
import java.util.AbstractList;
import java.util.List;

import dtool.ast.definitions.Module;
import dtool.parser.common.LexElement;
import dtool.parser.common.LexerResult;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.common.ParserError;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;

public class DeeParserResult extends LexerResult {
	
	public final ASTNode node;
	public final boolean ruleBroken;
	public final Module module;
	public final Indexable<ParserError> errors;
	
	public DeeParserResult(String source, AbstractList<LexElement> tokenList, ASTNode node, boolean ruleBroken,
		List<ParserError> errors) {
		super(source, tokenList);
		this.node = node;
		this.ruleBroken = ruleBroken;
		this.module = node instanceof Module ? (Module) node : null;
		this.errors = new ArrayList2<ParserError>(errors); // Ensure unmodifiable private copy
		assertTrue(node == null || node.isSemanticReady());
	}
	
	public boolean hasSyntaxErrors() {
		return errors != null && errors.size() > 0;
	}
	
	public Module getModuleNode() {
		assertNotNull(module);
		return module;
	}
	
	public Indexable<ParserError> getErrors() {
		return errors;
	}
	
	public static class ParsedModule extends DeeParserResult {
		
		public final Path modulePath; // optional, can be null
		
		public ParsedModule(String source, AbstractList<LexElement> tokenList, Module node, boolean ruleBroken,
				List<ParserError> errors, Path modulePath) {
			super(source, tokenList, node, ruleBroken, errors);
			this.modulePath = modulePath;
		}
		
	}
	
}