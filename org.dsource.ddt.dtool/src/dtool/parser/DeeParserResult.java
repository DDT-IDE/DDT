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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTVisitor;
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
	
	protected DeeParserResult(NodeResult<? extends ASTNode> result, DeeParser parser) {
		this(parser.getSource(), result.node, result.ruleBroken, initErrors(parser.lexerErrors, result.node));
		parser.lexerErrors = null;
	}
	
	protected DeeParserResult(String source, ASTNode node, boolean ruleBroken, List<ParserError> errors) {
		this.source = source;
		this.node = node;
		this.ruleBroken = ruleBroken;
		this.module = node instanceof Module ? (Module) node : null;
		this.errors = Collections.unmodifiableList(errors);
		assertTrue(node == null || node.getData().isLocallyAnalyzedStatus());
	}
	
	public static List<ParserError> initErrors(ArrayList<ParserError> lexerErrors, ASTNode resultNode) {
		return lexerErrors == null ? null : collectErrors(lexerErrors, resultNode);
	}
	
	public boolean hasSyntaxErrors() {
		return errors != null && errors.size() > 0;
	}
	
	public Module getParsedModule() {
		assertNotNull(module);
		return module;
	}
	
	public List<ParserError> getErrors() {
		return CoreUtil.blindCast(errors);
	}
	
	// TODO: this could be optimized
	protected static ArrayList<ParserError> collectErrors(final ArrayList<ParserError> errors, ASTNode node) {
		if(node != null) {
			node.accept(new ASTVisitor() {
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