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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.ASTNode;

/**
 * Concrete D Parser class
 */
public class DeeParser 
//It's not very elegant, but inheritance is used here just for the purpose of namespace importing:
	extends DeeParser_Statements 
{
	
	public static DeeParserResult parseSource(String source, String defaultModuleName) {
		return new DeeParser(source).parseModuleSource(defaultModuleName);
	}
	
	public DeeParserResult parseModuleSource(String defaultModuleName) {
		return parseUsingRule(null, defaultModuleName);
	}
	public DeeParserResult parseUsingRule(ParseRuleDescription parseRule, String defaultModuleName) {
		NodeResult<? extends ASTNode> nodeResult;
		if(parseRule == null) {
			nodeResult = parseModule(defaultModuleName);
		} else if(parseRule == DeeParser.RULE_EXPRESSION) {
			nodeResult = parseExpression();
		} else if(parseRule == DeeParser.RULE_REFERENCE) {
			nodeResult = parseTypeReference();
		} else if(parseRule == DeeParser.RULE_DECLARATION) {
			nodeResult = parseDeclaration();
		} else if(parseRule == RULE_TYPE_OR_EXP) {
			nodeResult = parseTypeOrExpression(true);
		} else if(parseRule == DeeParser.RULE_INITIALIZER) {
			nodeResult = parseInitializer();
		} else if(parseRule == DeeParser.RULE_STATEMENT) {
			nodeResult = parseStatement();
		} else if(parseRule == DeeParser.RULE_STRUCT_INITIALIZER) {
			nodeResult = parseStructInitializer();
		} else {
			throw assertFail();
		}
		assertTrue(enabled);
		if(nodeResult.node != null) {
			ASTNode.doSimpleAnalysisOnTree(nodeResult.node);
		}
		return new DeeParserResult(nodeResult, this);
	}
	
	protected ArrayList<ParserError> lexerErrors = new ArrayList<>();
	
	public DeeParser(String source) {
		this(new DeeLexer(source));
	}
	
	protected DeeParser(DeeLexer deeLexer) {
		this.source = deeLexer.getSource();
		DeeLexElementProducer deeLexElementProducer = new DeeLexElementProducer();
		this.lexSource = new LexElementSource(deeLexElementProducer.produceLexTokens(deeLexer));
		this.lexerErrors = deeLexElementProducer.lexerErrors;
	}
	
	public static final class DeeLexElementProducer extends LexElementProducer {
		protected ArrayList<ParserError> lexerErrors = new ArrayList<>();
		
		@Override
		protected void tokenParsed(Token token) {
			DeeTokenSemantics.checkTokenErrors(token, lexerErrors);
		}
	}
	
	@Override
	protected final DeeParser thisParser() {
		return this;
	}
	
}