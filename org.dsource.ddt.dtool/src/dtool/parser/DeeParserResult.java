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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dtool.ast.ASTHomogenousVisitor;
import dtool.ast.ASTNode;
import dtool.ast.definitions.Module;
import dtool.parser.AbstractParser.NodeResult;
import dtool.parser.ParserError.ErrorSourceRangeComparator;

public class DeeParserResult {
	
	public final String source;
	public final ASTNode node;
	public final boolean ruleBroken;
	public final Module module;
	public final List<ParserError> errors;
	
	public DeeParserResult(NodeResult<? extends ASTNode> result, DeeParser parser) {
		this(parser.getSource(), result.node, result.ruleBroken, parser.lexerErrors);
		parser.lexerErrors = null;
	}
	
	protected DeeParserResult(String source, ASTNode node, boolean ruleBroken, ArrayList<ParserError> lexerErrors) {
		this.source = source;
		this.node = node;
		this.ruleBroken = ruleBroken;
		this.module = node instanceof Module ? (Module) node : null;
		this.errors = lexerErrors == null ? null : Collections.unmodifiableList(collectErrors(lexerErrors, node));
	}
	
	public boolean hasSyntaxErrors() {
		return errors != null && errors.size() > 0;
	}
	
	public Module getParsedModule() {
		assertNotNull(module);
		return module;
	}
	
	// TODO: this could be optimized
	protected static ArrayList<ParserError> collectErrors(final ArrayList<ParserError> errors, ASTNode node) {
		if(node != null) {
			node.accept(new ASTHomogenousVisitor() {
				@Override
				public void postVisit(ASTNode node) {
					for (ParserError parserError : node.getData().getNodeErrors()) {
						errors.add(parserError);
					}
				}
			});
		}
		Collections.sort(errors, new ErrorSourceRangeComparator());
		return errors;
	}
	
}