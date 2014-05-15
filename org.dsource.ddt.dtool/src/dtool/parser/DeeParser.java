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
import java.util.Collections;
import java.util.List;

import dtool.ast.ASTNode;
import dtool.ast.ASTVisitor;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.parser.ParserError.ErrorSourceRangeComparator;

/**
 * Concrete D Parser class
 */
public class DeeParser 
//It's not very elegant, but inheritance is used here just for the purpose of namespace importing:
	extends DeeParser_Statements 
{
	
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
	
	public static ParsedModule parseSource(String source, String defaultModuleName) {
		return new DeeParser(source).parseModuleSource(defaultModuleName);
	}
	
	public ParsedModule parseModuleSource(String defaultModuleName) {
		return (ParsedModule) parseUsingRule(null, defaultModuleName);
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
		
		List<ParserError> errors = initErrors(lexerErrors, nodeResult.node);
		if(parseRule == null) {
			Module module = (Module) nodeResult.node;
			return new ParsedModule(getSource(), lexSource.lexElementList, module, 
				nodeResult.ruleBroken, errors);
		} else {
			return new DeeParserResult(getSource(), lexSource.lexElementList, nodeResult.node, 
				nodeResult.ruleBroken, errors);
		}
	}
	
	public static List<ParserError> initErrors(ArrayList<ParserError> lexerErrors, ASTNode resultNode) {
		return lexerErrors == null ? null : collectErrors(lexerErrors, resultNode);
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