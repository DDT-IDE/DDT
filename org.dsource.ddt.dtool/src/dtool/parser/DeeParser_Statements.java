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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.EmptyStatement;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.SimpleVariableDef;
import dtool.ast.statements.StatementIf;
import dtool.ast.statements.StatementIfVar;
import dtool.ast.statements.StatementLabel;
import dtool.parser.DeeParser.DeeParserState;
import dtool.util.ArrayView;


public abstract class DeeParser_Statements extends DeeParser_Decls {
	
	/* ----------------------------------------------------------------- */
	
	public static final ParseRuleDescription RULE_BLOCK = new ParseRuleDescription("Block");
	
	@Override
	protected NodeResult<BlockStatement> parseBlockStatement(boolean createMissing, boolean brokenIfMissing) {
		if(!tryConsume(DeeTokens.OPEN_BRACE)) {
			if(createMissing) {
				return result(brokenIfMissing, createMissingBlock(RULE_BLOCK));
			}
			return nullResult(); 
		}
		ParseHelper parse = new ParseHelper();
		
		ArrayView<IStatement> body = parseStatements(DeeTokens.CLOSE_BRACE);
		parse.consumeRequired(DeeTokens.CLOSE_BRACE);
		
		return parse.resultConclude(new BlockStatement(body));
	}
	
	protected ArrayView<IStatement> parseStatements(DeeTokens nodeListTerminator) {
		ArrayList<IStatement> nodeList = new ArrayList<>();
		while(true) {
			if(lookAhead() == nodeListTerminator) {
				break;
			}
			IStatement st = parseStatement().node;
			if(st == null) {
				if(lookAhead() == DeeTokens.EOF) {
					break;
				}
				st = parseInvalidElement(RULE_STATEMENT, true);
			}
			nodeList.add(st);
		}
		
		return arrayViewI(nodeList);
	}
	
	// Note these two rules are equivalent, but last one indicated a preference for blocks vs. single statements. 
	public static final ParseRuleDescription RULE_STATEMENT = new ParseRuleDescription("Statement");
	public static final ParseRuleDescription RULE_ST_OR_BLOCK = new ParseRuleDescription("Statement or Block");
	
	protected NodeResult<? extends IStatement> parseStatement(ParseRuleDescription expected) {
		NodeResult<? extends IStatement> stResult = parseStatement();
		if(stResult.node == null) {
			return result(false, createMissingBlock(expected));
		}
		return stResult;
	}
	
	protected BlockStatement createMissingBlock(ParseRuleDescription expectedRule) {
		ParserError error = expectedRule != null ? createErrorExpectedRule(expectedRule) : null;
		int nodeStart = getLexPosition();
		return conclude(error, srToPosition(nodeStart, new BlockStatement()));
	}
	
	protected NodeResult<? extends IStatement> parseStatement() {
		switch (lookAhead()) {
		case SEMICOLON: 
			consumeLookAhead();
			return resultConclude(false, srOf(lastLexElement(), new EmptyStatement()));
		case OPEN_BRACE:
			return parseBlockStatement(true, true);
		case KW_IF:
			return parseStatement_ifStart();
		default:
			break;
		}
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.COLON) {
			return parseStatementLabel_start();
		}
		
		NodeResult<? extends IStatement> decl = parseStatementDeclaration();
		return decl;
	}
	
	protected NodeResult<? extends IStatement> parseStatementDeclaration() {
		NodeResult<? extends IDeclaration> declResult = parseDeclaration(false, false, true);
		assertTrue(declResult.node == null || declResult.node instanceof IStatement);
		return CoreUtil.blindCast(declResult);
	}
	
	/* ----------------------------------------------------------------- */
	
	protected NodeResult<StatementLabel> parseStatementLabel_start() {
		LexElement labelId = consumeLookAhead(DeeTokens.IDENTIFIER);
		consumeLookAhead(DeeTokens.COLON);
		
		Symbol label = createIdSymbol(labelId);
		return resultConclude(false, srBounds(labelId.getStartPos(), getLexPosition(), new StatementLabel(label)));
	}
	
	public NodeResult<? extends IStatement> parseStatement_ifStart() {
		if(!tryConsume(DeeTokens.KW_IF))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Expression condition = null;
		SimpleVariableDef conditionVar = null;
		IStatement thenBody = null;
		IStatement elseBody = null;
		
		parsing: { 
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS) == false) break parsing;
			conditionVar = attemptParseSimpleDefVar();
			if(conditionVar == null) {
				condition = parseExpression_toMissing();
			}
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS) == false) break parsing;
			
			thenBody = parse.checkResult(parseStatement(RULE_ST_OR_BLOCK));
			if(parse.ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.KW_ELSE)) {
				elseBody = parse.checkResult(parseStatement(RULE_ST_OR_BLOCK));
			}
		}
		
		if(conditionVar != null) {
			return parse.resultConclude(new StatementIfVar(conditionVar, thenBody, elseBody));
		} else {
			return parse.resultConclude(new StatementIf(condition, thenBody, elseBody));
		}
	}
	
	public SimpleVariableDef attemptParseSimpleDefVar() {
		DeeParserState savedState = thisParser().enterBacktrackableMode();
		
		successfulParsing: {
			ParseHelper parse = new ParseHelper(lookAheadElement());
			
			Reference type;
			ProtoDefSymbol defId;
			
			if(lookAhead() == DeeTokens.KW_AUTO) {
				type = parseAutoReference();
				defId = parseDefId(); // Parse a SimpleVariableDef if even id is missing
			} else {
				NodeResult<Reference> typeResult = parseTypeReference();
				if(typeResult.ruleBroken) break successfulParsing;
				type = typeResult.node;
				
				defId = parseDefId();
				if(defId.isMissing()) break successfulParsing;
			}
			
			Expression defaultValue = null;
			if(parse.consumeRequired(DeeTokens.ASSIGN)) {
				defaultValue = parseExpression_toMissing();
			}
			return parse.conclude(new SimpleVariableDef(type, defId, defaultValue));
		}
		thisParser().restoreOriginalState(savedState);
		return null;
	}
	
}