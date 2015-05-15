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

import melnorme.lang.tooling.ast.ParserError;
import melnorme.lang.tooling.ast.ParserErrorTypes;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.declarations.IncompleteDeclarator;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpMixinString;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.AutoReference;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.BlockStatementUnscoped;
import dtool.ast.statements.CatchClause;
import dtool.ast.statements.CommonStatementList;
import dtool.ast.statements.EmptyStatement;
import dtool.ast.statements.ForeachVariableDef;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.ScopedStatementList;
import dtool.ast.statements.SimpleVariableDef;
import dtool.ast.statements.Statement;
import dtool.ast.statements.StatementAsm;
import dtool.ast.statements.StatementBreak;
import dtool.ast.statements.StatementCase;
import dtool.ast.statements.StatementCaseRange;
import dtool.ast.statements.StatementContinue;
import dtool.ast.statements.StatementDefault;
import dtool.ast.statements.StatementDoWhile;
import dtool.ast.statements.StatementExpression;
import dtool.ast.statements.StatementFor;
import dtool.ast.statements.StatementForeach;
import dtool.ast.statements.StatementGoto;
import dtool.ast.statements.StatementGotoCase;
import dtool.ast.statements.StatementGotoDefault;
import dtool.ast.statements.StatementIf;
import dtool.ast.statements.StatementIfVar;
import dtool.ast.statements.StatementLabel;
import dtool.ast.statements.StatementReturn;
import dtool.ast.statements.StatementScope;
import dtool.ast.statements.StatementSwitch;
import dtool.ast.statements.StatementSynchronized;
import dtool.ast.statements.StatementThrow;
import dtool.ast.statements.StatementTry;
import dtool.ast.statements.StatementWhile;
import dtool.ast.statements.StatementWith;
import dtool.ast.statements.VariableDefWithInit;
import dtool.parser.common.IToken;
import dtool.parser.common.LexElement;


public abstract class DeeParser_Statements extends DeeParser_Definitions {
	
	/* ----------------------------------------------------------------- */
	
	public static final ParseRuleDescription RULE_BLOCK = new ParseRuleDescription("Block", "Block");
	
	protected NodeResult<BlockStatement> parseBlockStatement(boolean createMissing, boolean brokenIfMissing) {
		return parseBlockStatement(createMissing, brokenIfMissing, true).upcastTypeParam();
	}
	
	protected NodeResult<? extends CommonStatementList> parseBlockStatement(
		boolean createMissing, boolean brokenIfMissing, boolean isScoped) {
		if(!tryConsume(DeeTokens.OPEN_BRACE)) {
			if(createMissing) {
				return parseMissingBlock(brokenIfMissing, RULE_BLOCK, isScoped);
			}
			return nullResult(); 
		}
		ParseHelper parse = new ParseHelper();
		
		NodeVector<IStatement> body = parseStatements(DeeTokens.CLOSE_BRACE, true);
		parse.consumeRequired(DeeTokens.CLOSE_BRACE);
		
		return parse.resultConclude(isScoped ? new BlockStatement(body) : new BlockStatementUnscoped(body));
	}
	
	public NodeResult<? extends CommonStatementList> parseMissingBlock(boolean brokenIfMissing,
		ParseRuleDescription expectedRule, boolean isScoped) {
		if(brokenIfMissing) {
			advanceSubChannelTokens();
		}
		int nodeStart = getSourcePosition();
		ParserError error = expectedRule != null ? createErrorExpectedRule(expectedRule) : null;
		return result(brokenIfMissing, conclude(error, srToPosition(nodeStart, 
			isScoped ? new BlockStatement() : new BlockStatementUnscoped())));
	}
	
	protected NodeResult<ScopedStatementList> parseScopedStatementList() {
		ParseHelper parse = new ParseHelper(getSourcePosition());
		
		NodeVector<IStatement> body = parseStatements(null, false);
		
		return parse.resultConclude(new ScopedStatementList(body));
	}
	
	// Note these two rules are equivalent, but last one indicated a preference for blocks vs. single statements. 
	public static final ParseRuleDescription RULE_STATEMENT = 
		new ParseRuleDescription("Statement", "Statement");
	public static final ParseRuleDescription RULE_ST_OR_BLOCK = 
		new ParseRuleDescription("StOrBlock", "Statement or Block");
	
	protected NodeVector<IStatement> parseStatements(DeeTokens nodeListTerminator, boolean parseCaseDefault) {
		ArrayList<IStatement> nodeList = new ArrayList<>();
		while(true) {
			if(lookAhead() == nodeListTerminator) {
				break;
			}
			IStatement st = parseStatement(parseCaseDefault, true).node;
			if(st == null) {
				if(lookAhead() == DeeTokens.EOF 
					|| lookAhead() == DeeTokens.KW_CASE || lookAhead() == DeeTokens.KW_DEFAULT 
					|| isCloseBracketChar(lookAhead())) {
					break;
				}
				st = parseInvalidElement(RULE_STATEMENT, true);
			}
			nodeList.add(st);
		}
		
		return arrayView(nodeList);
	}
	
	protected NodeResult<? extends IStatement> parseStatement_toMissing() {
		return parseStatement_toMissing(RULE_ST_OR_BLOCK);
	}
	
	protected NodeResult<? extends IStatement> parseStatement_toMissing(ParseRuleDescription expectedRule) {
		NodeResult<? extends IStatement> stResult = parseStatement();
		if(stResult.node == null) {
			return parseMissingBlock(false, expectedRule, true);
		}
		return stResult;
	}
	
	protected NodeResult<? extends IStatement> parseUnscopedStatement_toMissing() {
		NodeResult<? extends IStatement> stResult = parseStatement(true, false);
		if(stResult.node == null) {
			return parseMissingBlock(false, RULE_ST_OR_BLOCK, false);
		}
		return stResult;
	}
	
	protected NodeResult<? extends IStatement> parseStatement() {
		return parseStatement(true, true);
	}
	protected NodeResult<? extends IStatement> parseStatement(boolean parseCaseDefault, boolean isScoped) {
		switch (lookAhead()) {
		case SEMICOLON: 
			consumeLookAhead();
			return resultConclude(false, srOf(lastLexElement(), new EmptyStatement()));
		
		case OPEN_BRACE:return parseBlockStatement(true, true, isScoped);
		
		case KW_IF: return parseStatement_ifStart();
		case KW_WHILE: return parseStatementWhile();
		case KW_DO: return parseStatementDoWhile();
		case KW_FOR: return parseStatementFor();
		
		case KW_FOREACH :return parseStatementForeach();
		case KW_FOREACH_REVERSE: return parseStatementForeach();
		case KW_SWITCH: return parseStatementSwitch();
		case KW_FINAL:
			if(lookAhead(1) == DeeTokens.KW_SWITCH)
				return parseStatementSwitch();
			break;
		case KW_CASE:
			if(!parseCaseDefault)
				break;
			return parseStatement_caseStart();
		case KW_DEFAULT:
			if(!parseCaseDefault)
				break;
			return parseStatementDefault();
			
		case KW_CONTINUE: return parseStatementContinue();
		case KW_BREAK: return parseStatementBreak();
		case KW_RETURN: return parseStatementReturn();
		case KW_GOTO: return parseStatement_gotoStart();
		case KW_THROW: return parseStatementThrow();
		case KW_SYNCHRONIZED: return parseStatementSynchronized();
		case KW_WITH: return parseStatementWith();
		case KW_ASM: return parseStatementAsm();
		case KW_TRY: return parseStatementTry();
		case KW_SCOPE:
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return parseStatementScope();
			}
			break;
		case IDENTIFIER:
			if(lookAhead(1) == DeeTokens.COLON)
				return parseStatementLabel_start();
			break;
		default:
			break;
		}
		
		ParserState originalState = saveParserState();
		
		if(lookAhead() == DeeTokens.KW_IMPORT && lookAhead(1) == DeeTokens.OPEN_PARENS) {
			// Disambiguate against DeclarationImport
			return parseStatementExpression();
		}
		
		NodeResult<? extends IDeclaration> declResult = parseDeclaration(true);
		IDeclaration decl = declResult.node;
		
		if(decl instanceof DeclarationMixinString) {
			DeclarationMixinString declarationMixinString = (DeclarationMixinString) decl;
			// Check if ";" was consumed
			if(declResult.ruleBroken) {
				// If not, then this could have been parsed as an expression, retry with expression rule.
				
				ExpMixinString expMixinString = declarationMixinString.exp;
				expMixinString.detachFromParent_disposeParent();
				
				// TODO: perhaps we could add a precise check for whether expMixinString
				// was consumed sucessfully or had rule broken
				
				ParseHelper parse = new ParseHelper(expMixinString.getStartPos());
				
				Expression exp = new ParseRule_Expression().
						parseTypeOrExpression_fromUnary(ANY_OPERATOR, expMixinString);
				
				parse.consumeRequired(DeeTokens.SEMICOLON);
				return parse.resultConclude(new StatementExpression(exp));
			}
		}
		
		if(decl instanceof IncompleteDeclarator || decl == null) {
			restoreOriginalState(originalState);
			NodeResult<StatementExpression> expResult = parseStatementExpression();
			assertTrue(expResult.node != null || decl == null); // any IncompleteDeclaration must be parsable as exp 
			return expResult;
		} else if(declResult.ruleBroken && decl instanceof DefinitionFunction) {
			
			DefinitionFunction defFunction = (DefinitionFunction) decl;
			if(defFunction.fnBody == null && defFunction.tplConstraint == null && defFunction.fnAttributes == null) {
				ParserState defFunctionState = saveParserState();
				
				restoreOriginalState(originalState);
				NodeResult<StatementExpression> stExpResult = parseStatementExpression();
				
				int expLexElementPos = getEnabledLexSource().getLexElementPosition();
				if(expLexElementPos > defFunctionState.lexSource.getLexElementPosition()) {
					return stExpResult;
				} else {
					restoreOriginalState(defFunctionState);
					// break to return declResult
				}
			}
		}
		assertTrue(decl instanceof IStatement);
		return CoreUtil.blindCast(declResult);
	}
	
	public NodeResult<StatementExpression> parseStatementExpression() {
		ParseHelper parse = new ParseHelper(-1);
		Expression exp = parseExpression().node;
		if(exp == null) {
			return nullResult();
		}
		parse.nodeStart = exp.getStartPos();
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new StatementExpression(exp));
	}
	
	/* ----------------------------------------------------------------- */
	
	protected NodeResult<StatementLabel> parseStatementLabel_start() {
		LexElement labelId = consumeLookAhead(DeeTokens.IDENTIFIER);
		consumeLookAhead(DeeTokens.COLON);
		
		Symbol label = createIdSymbol(labelId);
		return resultConclude(false, srBounds(labelId.getStartPos(), getSourcePosition(), new StatementLabel(label)));
	}
	
	public NodeResult<? extends IStatement> parseStatement_ifStart() {
		if(!tryConsume(DeeTokens.KW_IF))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Expression condition = null;
		VariableDefWithInit conditionVar = null;
		IStatement thenBody = null;
		IStatement elseBody = null;
		
		parsing: { 
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			conditionVar = attemptParseVariableDefWithInit(true);
			if(conditionVar == null) {
				condition = parseExpression_toMissing();
			}
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			
			thenBody = parse.checkResult(parseStatement_toMissing());
			if(parse.ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.KW_ELSE)) {
				elseBody = parse.checkResult(parseStatement_toMissing());
			}
		}
		
		if(conditionVar != null) {
			return parse.resultConclude(new StatementIfVar(conditionVar, thenBody, elseBody));
		} else {
			return parse.resultConclude(new StatementIf(condition, thenBody, elseBody));
		}
	}
	
	protected VariableDefWithInit attemptParseVariableDefWithInit(boolean revertIfInvalid) {
		ParserState savedState = saveParserState();
		
		successfulParsing: {
			ParseHelper parse = new ParseHelper(lookAheadElement());
			
			Reference type;
			ProtoDefSymbol defId;
			
			if(lookAhead() == DeeTokens.KW_AUTO) {
				type = parseAutoReference();
				defId = parseDefId(); // Parse a SimpleVariableDef even if id is missing
			} else {
				NodeResult<Reference> typeResult = parseTypeReference();
				type = typeResult.node;
				if(typeResult.ruleBroken) {
					if(revertIfInvalid) break successfulParsing;
					defId = parseMissingDefIdNoError();
				} else {
					defId = parseDefId();
					if(revertIfInvalid && defId.isMissing()) break successfulParsing;
				}
			}
			
			Expression defaultValue = null;
			parse.consumeRequired(DeeTokens.ASSIGN);
			if(parse.ruleBroken) {
				if(type instanceof RefTypePointer) {
					break successfulParsing; // Parse as exp instead
				}
			} else {
				defaultValue = parseExpression_toMissing();
			}
			return parse.conclude(new VariableDefWithInit(type, defId.createDefId(), defaultValue));
		}
		restoreOriginalState(savedState);
		return null;  // An exp will be parsed instead 
	}
	
	public AutoReference parseAutoReference() {
		LexElement autoToken = consumeLookAhead(DeeTokens.KW_AUTO);
		return conclude(srOf(autoToken, new AutoReference()));
	}
	
	public NodeResult<StatementWhile> parseStatementWhile() {
		if(!tryConsume(DeeTokens.KW_WHILE))
			return nullResult();
		ParseParensExpBodyNode parse = new ParseParensExpBodyNode().doParse(true);
		return parse.resultConclude(new StatementWhile(parse.exp, parse.body));
	}
	
	public class ParseParensExpBodyNode extends ParseHelper {
		
		public Expression exp = null;
		public IStatement body = null;
		
		public ParseParensExpBodyNode doParse(boolean isRequired) {
			ParseHelper parse = this;
			
			parsing: {
				exp = parseExpressionAroundParentheses(parse, isRequired, false);
				if(parse.ruleBroken) break parsing;
				
				body = parse.checkResult(parseStatement_toMissing(RULE_ST_OR_BLOCK));
			}
			return this;
		}
		
	}
	
	public NodeResult<StatementDoWhile> parseStatementDoWhile() {
		if(!tryConsume(DeeTokens.KW_DO))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		IStatement body = null;
		Expression condition = null;
		parsing: { 
			body = parse.checkResult(parseStatement_toMissing(RULE_ST_OR_BLOCK));
			if(parse.ruleBroken) break parsing;
			
			if(parse.consumeRequired(DeeTokens.KW_WHILE).ruleBroken) break parsing;
			
			condition = parseExpressionAroundParentheses(parse, true, false);
			if(parse.ruleBroken) break parsing;
			
			parse.consumeRequired(DeeTokens.SEMICOLON);
		}
		return parse.resultConclude(new StatementDoWhile(body, condition));
	}
	
	protected NodeResult<StatementFor> parseStatementFor() {
		if(!tryConsume(DeeTokens.KW_FOR))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		IStatement init = null;
		Expression condition = null;
		Expression increment = null;
		IStatement body = null;
		
		parsing: { 
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			
			init = parse.checkResult(parseStatement_toMissing(RULE_STATEMENT));
			if(parse.ruleBroken) break parsing;
			
			condition = parseExpression().node;
			
			if(parse.consumeExpected(DeeTokens.SEMICOLON)) {
				increment = parseExpression().node;
			}
			
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
			if(parse.ruleBroken) break parsing;
			
			body = parse.checkResult(parseStatement_toMissing());
		}
		
		return parse.resultConclude(new StatementFor(init, condition, increment, body));
	}
	
	protected NodeResult<StatementForeach> parseStatementForeach() {
		if(!(tryConsume(DeeTokens.KW_FOREACH) || tryConsume(DeeTokens.KW_FOREACH_REVERSE)))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		boolean isForeachReverse = lastLexElement().type == DeeTokens.KW_FOREACH_REVERSE;
		NodeVector<ForeachVariableDef> varParams = null;
		Expression iterable = null;
		IStatement body = null;
		
		parsing: { 
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
		
			ArrayList<ForeachVariableDef> varParamsList = new ArrayList<>(2);
			do {
				ForeachVariableDef varDef = parseForeachVariableDef();
				varParamsList.add(varDef);
			} while(tryConsume(DeeTokens.COMMA));
			varParams = arrayView(varParamsList);
			
			if(parse.consumeExpected(DeeTokens.SEMICOLON)) {
				iterable = parseForeachIterableExpression();
			}
			
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			
			body = parse.checkResult(parseStatement_toMissing());
		}
		
		return parse.resultConclude(new StatementForeach(isForeachReverse, varParams, iterable, body));
	}
	
	public ForeachVariableDef parseForeachVariableDef() {
		ParseHelper parse = new ParseHelper(lookAheadElement());
		boolean isRef = false;
		TypeId_or_Id_RuleFragment typeRef_defId = new TypeId_or_Id_RuleFragment();
		
		if(tryConsume(DeeTokens.KW_REF)) {
			isRef = true;
		}
		
		LexElement typeMod = null;
		if(isImmutabilitySpecifier(lookAhead())) {
			typeMod = consumeLookAhead();
		}
		
		typeRef_defId.parseRuleFragment(parse, true);
		
		return parse.conclude(new ForeachVariableDef(
			isRef, typeMod, typeRef_defId.type, typeRef_defId.defId.createDefId()));
	}
	
	public Expression parseForeachIterableExpression() {
		return parseExpression_toMissing();
	}
	
	public NodeResult<StatementSwitch> parseStatementSwitch() {
		ParseHelper parse = new ParseHelper(lookAheadElement());
		boolean isFinal;
		if(tryConsume(DeeTokens.KW_SWITCH)) {
			isFinal = false;
		} else if(tryConsume(DeeTokens.KW_FINAL, DeeTokens.KW_SWITCH)) {
			isFinal = true;
		} else {
			return nullResult();
		}
		
		Expression exp;
		IStatement body = null;
		parsing: { 
			exp = parseExpressionAroundParentheses(parse, true, false);
			if(parse.ruleBroken) break parsing;
			
			body = parse.checkResult(parseStatement_toMissing());
		}
		
		return parse.resultConclude(new StatementSwitch(isFinal, exp, body));
	}
	
	protected NodeResult<? extends Statement> parseStatement_caseStart() {
		if(!tryConsume(DeeTokens.KW_CASE))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		NodeVector<Expression> caseValues;
		ScopedStatementList body = null;
		parsing: {
			ArrayList<Expression> caseValuesList = new ArrayList<>(2);
			do {
				Expression varDef = parseAssignExpression_toMissing();
				caseValuesList.add(varDef);
			} while(tryConsume(DeeTokens.COMMA));
			caseValues = arrayView(caseValuesList);
			
			parse.consumeRequired(DeeTokens.COLON);
			if(parse.ruleBroken) break parsing;
			
			if(caseValues.size() == 1 && lookAhead() == DeeTokens.DOUBLE_DOT) {
				return parseStatementCaseRange_atDoubleDot(parse, caseValues.get(0));
			}
			
			body = parse.checkResult(parseScopedStatementList());
		}
		
		return parse.resultConclude(new StatementCase(caseValues, body));
	}
	
	public NodeResult<StatementCaseRange> parseStatementCaseRange_atDoubleDot(ParseHelper parse, Expression expFirst) {
		consumeLookAhead(DeeTokens.DOUBLE_DOT);
		
		Expression expLast = null;
		ScopedStatementList body = null;
		parsing: {
			if(parse.consumeRequired(DeeTokens.KW_CASE).ruleBroken) break parsing;
			
			expLast = parseAssignExpression_toMissing();
			
			parse.consumeRequired(DeeTokens.COLON);
			if(parse.ruleBroken) break parsing;
			
			body = parse.checkResult(parseScopedStatementList());
		}
		return parse.resultConclude(new StatementCaseRange(expFirst, expLast, body));
	}
	
	public NodeResult<StatementDefault> parseStatementDefault() {
		if(!tryConsume(DeeTokens.KW_DEFAULT))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		ScopedStatementList body = null;
		parsing: { 
			parse.consumeRequired(DeeTokens.COLON);
			if(parse.ruleBroken) break parsing;
			
			body = parse.checkResult(parseScopedStatementList());
		}
		
		return parse.resultConclude(new StatementDefault(body));
	}
	
	
	public NodeResult<StatementContinue> parseStatementContinue() {
		if(!tryConsume(DeeTokens.KW_CONTINUE))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		Symbol id = lookAhead() == DeeTokens.IDENTIFIER ? parseIdSymbol() : null;
		parse.consumeRequired(DeeTokens.SEMICOLON);
		
		return parse.resultConclude(new StatementContinue(id));
	}
	
	public NodeResult<StatementBreak> parseStatementBreak() {
		if(!tryConsume(DeeTokens.KW_BREAK))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		Symbol id = lookAhead() == DeeTokens.IDENTIFIER ? parseIdSymbol() : null;
		parse.consumeRequired(DeeTokens.SEMICOLON);
		
		return parse.resultConclude(new StatementBreak(id));
	}
	
	public NodeResult<StatementReturn> parseStatementReturn() {
		if(!tryConsume(DeeTokens.KW_RETURN))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		Expression exp = parseExpression().node;
		parse.consumeRequired(DeeTokens.SEMICOLON);
		
		return parse.resultConclude(new StatementReturn(exp));
	}
	
	protected NodeResult<? extends Statement> parseStatement_gotoStart() {
		if(!tryConsume(DeeTokens.KW_GOTO))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		if(tryConsume(DeeTokens.KW_CASE)) {
			Resolvable exp = parseExpression().node;
			parse.consumeRequired(DeeTokens.SEMICOLON);
			return parse.resultConclude(new StatementGotoCase(exp));
		}
		if(tryConsume(DeeTokens.KW_DEFAULT)) {
			parse.consumeRequired(DeeTokens.SEMICOLON);
			return parse.resultConclude(new StatementGotoDefault());
		}
		
		Symbol label = parseIdSymbol();
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new StatementGoto(label));
	}
	
	public NodeResult<StatementThrow> parseStatementThrow() {
		if(!tryConsume(DeeTokens.KW_THROW))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		Expression exp = parseExpression_toMissing();
		parse.consumeRequired(DeeTokens.SEMICOLON);
		
		return parse.resultConclude(new StatementThrow(exp));
	}
	
	public NodeResult<StatementSynchronized> parseStatementSynchronized() {
		if(!tryConsume(DeeTokens.KW_SYNCHRONIZED))
			return nullResult();
		ParseParensExpBodyNode parse = new ParseParensExpBodyNode().doParse(false);
		return parse.resultConclude(new StatementSynchronized(parse.exp, parse.body));
	}
	
	public NodeResult<StatementWith> parseStatementWith() {
		if(!tryConsume(DeeTokens.KW_WITH))
			return nullResult();
		ParseParensExpBodyNode parse = new ParseParensExpBodyNode().doParse(true);
		return parse.resultConclude(new StatementWith(parse.exp, parse.body));
	}
	
	public NodeResult<StatementAsm> parseStatementAsm() {
		if(!tryConsume(DeeTokens.KW_ASM))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		ArrayList<IToken> tokenList = null;
		parsing: { 
			if(parse.consumeExpected(DeeTokens.OPEN_BRACE) == false) break parsing;
			tokenList = new ArrayList<>();
			
			for(int braceDepth = 1; true; ) {
				if(lookAhead() == DeeTokens.EOF) {
					parse.consumeRequired(DeeTokens.CLOSE_BRACE);
					break;
				}
				LexElement token = consumeLookAhead();
				if(token.type == DeeTokens.OPEN_BRACE) {
					braceDepth++;
				}
				if(token.type == DeeTokens.CLOSE_BRACE) {
					braceDepth--;
					if(braceDepth == 0)
						break;
				}
				tokenList.add(token);
			} 
			
		}
		
		return parse.resultConclude(new StatementAsm(arrayViewG(tokenList)));
	}
	
	public NodeResult<StatementScope> parseStatementScope() {
		if(!tryConsume(DeeTokens.KW_SCOPE))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		Symbol scopeTypeId = null;
		IStatement body = null;
		parsing: { 
			if(parse.consumeExpected(DeeTokens.OPEN_PARENS)) {
				
				boolean idIsMissing = lookAhead() != DeeTokens.IDENTIFIER;
				scopeTypeId = parseIdSymbol();
				if(!idIsMissing && StatementScope.ScopeTypes.fromIdentifier(scopeTypeId.name) == null) {
					parse.storeError(createErrorOnLastToken(ParserErrorTypes.INVALID_SCOPE_ID, null));
				}
				
				if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			}
			
			body = parse.checkResult(parseStatement_toMissing());
		}
		
		return parse.resultConclude(new StatementScope(scopeTypeId, body));
	}
	
	public static final ParseRuleDescription RULE_CATCH_OR_FINALLY = 
		new ParseRuleDescription("CatchOrFinally", "Catch or Finally");
	
	public NodeResult<StatementTry> parseStatementTry() {
		if(!tryConsume(DeeTokens.KW_TRY))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		IStatement body;
		ArrayList<CatchClause> catches = null;
		IStatement finallyBody = null;
		
		parsing: { 
			body = parse.checkResult(parseStatement_toMissing());
			if(parse.ruleBroken) break parsing;
			
			catches = new ArrayList<>();
			while(true) {
				CatchClause catchClause = parse.checkResult(parseCatchClause());
				if(catchClause == null) {
					break;
				}
				catches.add(catchClause);
				if(parse.ruleBroken) break parsing;
			}
			
			if(tryConsume(DeeTokens.KW_FINALLY)) {
				finallyBody = parse.checkResult(parseStatement_toMissing());
			}
			if(catches.size() == 0 && finallyBody == null) {
				parse.storeError(createErrorExpectedRule(RULE_CATCH_OR_FINALLY));
			}
		}
		
		return parse.resultConclude(new StatementTry(body, arrayView(catches), finallyBody));
	}
	
	protected NodeResult<CatchClause> parseCatchClause() {
		if(!tryConsume(DeeTokens.KW_CATCH))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		LexElement catchKeyword = lastLexElement();
		
		SimpleVariableDef catchParam = null; 
		IStatement body = null;
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				catchParam = parseSimpleVariableDef_DefIdOptional();
				
				if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			}
			
			body = parse.checkResult(parseStatement_toMissing());
		}
		
		if(parse.ruleBroken == false && catchParam == null && lookAhead() == DeeTokens.KW_CATCH) {
			parse.storeError(createError(ParserErrorTypes.LAST_CATCH, catchKeyword, null));
		}
		
		return parse.resultConclude(new CatchClause(catchParam, body));
	}
	
	public SimpleVariableDef parseSimpleVariableDef_DefIdOptional() {
		ParseHelper parse = new ParseHelper(-1);
		TypeId_or_Id_RuleFragment typeRef_defId = new TypeId_or_Type_RuleFragment();
		typeRef_defId.parseRuleFragment(parse, true);
		return parse.conclude(new SimpleVariableDef(typeRef_defId.type, typeRef_defId.defId.createDefId()));
	}
	
}