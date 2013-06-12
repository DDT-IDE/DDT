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

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.NodeListView;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.declarations.StaticIfExpIs;
import dtool.ast.declarations.StaticIfExpIs.StaticIfExpIsDefUnit;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.definitions.DefinitionVariable.CStyleRootRef;
import dtool.ast.definitions.FunctionAttributes;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Symbol;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.expressions.ExpArrayLength;
import dtool.ast.expressions.ExpAssert;
import dtool.ast.expressions.ExpCall;
import dtool.ast.expressions.ExpCast;
import dtool.ast.expressions.ExpCastQual;
import dtool.ast.expressions.ExpCastQual.CastQualifiers;
import dtool.ast.expressions.ExpConditional;
import dtool.ast.expressions.ExpFunctionLiteral;
import dtool.ast.expressions.ExpImportString;
import dtool.ast.expressions.ExpIndex;
import dtool.ast.expressions.ExpInfix;
import dtool.ast.expressions.ExpInfix.InfixOpType;
import dtool.ast.expressions.ExpIs;
import dtool.ast.expressions.ExpIs.ExpIsSpecialization;
import dtool.ast.expressions.ExpLiteralArray;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralChar;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralMapArray;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpMixinString;
import dtool.ast.expressions.ExpNew;
import dtool.ast.expressions.ExpNewAnonClass;
import dtool.ast.expressions.ExpNull;
import dtool.ast.expressions.ExpParentheses;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.ast.expressions.ExpPostfixOperator.PostfixOpType;
import dtool.ast.expressions.ExpPrefix;
import dtool.ast.expressions.ExpPrefix.PrefixOpType;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSimpleLambda;
import dtool.ast.expressions.ExpSimpleLambda.SimpleLambdaDefUnit;
import dtool.ast.expressions.ExpSlice;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.ExpTraits;
import dtool.ast.expressions.ExpTypeId;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.MissingParenthesesExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.RefSlice;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.RefTypeModifier;
import dtool.ast.references.RefTypeModifier.TypeModifierKinds;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.RefTypeof;
import dtool.ast.references.Reference;
import dtool.ast.statements.IFunctionBody;
import dtool.parser.DeeParser.DeeParserState;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;


public abstract class DeeParser_RefOrExp extends DeeParser_Common {
	
	/* --------------------  reference parsing  --------------------- */
	
	public static final ParseRuleDescription RULE_REFERENCE = new ParseRuleDescription("Reference");
	public static final ParseRuleDescription RULE_TPL_SINGLE_ARG = new ParseRuleDescription("TemplateSingleArgument");
	
	public NodeResult<Reference> parseTypeReference() {
		return parseTypeReference_start(false);
	}
	
	public NodeResult<Reference> parseTypeReference(boolean createMissing, boolean reportMissingError) {
		NodeResult<Reference> typeRef = parseTypeReference();
		if((typeRef == null || typeRef.node == null) && createMissing) {
			return result(false, parseMissingTypeReference(reportMissingError));
		}
		return typeRef;
	}
	
	public NodeResult<Reference> parseTypeReference_ToMissing() {
		return parseTypeReference_ToMissing(true);
	}
	public NodeResult<Reference> parseTypeReference_ToMissing(boolean reportMissingError) {
		return parseTypeReference(true, reportMissingError);
	}
	
	public Reference parseMissingTypeReference(boolean reportMissingError) {
		ParseRuleDescription expectedRule = reportMissingError ? RULE_REFERENCE : null;
		return parseMissingTypeReference(expectedRule);
	}
	
	public Reference parseMissingTypeReference(ParseRuleDescription expectedRule) {
		SourceRange sourceRange = createExpectedToken(DeeTokens.IDENTIFIER).getSourceRange();
		ParserError error = expectedRule != null ? createErrorExpectedRule(expectedRule) : null;
		return createMissingTypeReferenceNode(sourceRange, error);
	}
	
	public Reference createMissingTypeReferenceNode(SourceRange sourceRange, ParserError error) {
		RefIdentifier refMissing = new RefIdentifier(null);
		refMissing.setSourceRange(sourceRange);
		assertTrue(isMissing(refMissing));
		return conclude(error, refMissing);
	}
	
	public static boolean isMissing(Reference ref) {
		return ((ref instanceof RefIdentifier) && ((RefIdentifier) ref).name == null)
			|| ((ref instanceof RefImportSelection) && ((RefImportSelection) ref).name == null);
	}
	
	public NodeResult<Reference> parseTypeReference_start(boolean parsingExp) {
		DeeTokens lookAhead = lookAhead();
		NodeResult<Reference> result = parseTypeReference_start_do(parsingExp);
		assertTrue(canParseTypeReferenceStart(lookAhead) == (result.node != null));
		return result;
	}
	
	protected NodeResult<Reference> parseTypeReference_start_do(boolean parsingExp) {
		NodeResult<? extends Reference> refParseResult;
		
		TypeModifierKinds typeModifier = determineTypeModifier(lookAhead());
		if(typeModifier != null) {
			refParseResult = parseRefTypeModifier_start(typeModifier);
		} else {
			switch (lookAhead().getGroupingToken()) {
			case IDENTIFIER: 
				return parseReference_referenceStart(parseRefIdentifier(), parsingExp);
			case PRIMITIVE_KW: 
				return parseReference_referenceStart(parseRefPrimitive_start(lookAhead()), parsingExp); 
			case DOT: 
				refParseResult = parseRefModuleQualified(); break;
			case KW_TYPEOF: 
				refParseResult = parseRefTypeof(); break;
			
			default:
				return nullResult();
			}
		}
		
		if(refParseResult.ruleBroken) 
			return refParseResult.<Reference>upcastTypeParam();
		return parseReference_referenceStart(refParseResult.node, parsingExp);
	}
	
	protected static boolean canParseTypeReferenceStart(DeeTokens lookAhead) {
		TypeModifierKinds typeModifier = determineTypeModifier(lookAhead);
		if(typeModifier != null) {
			return true;
		} else {
			switch (lookAhead.getGroupingToken()) {
			case IDENTIFIER: 
			case PRIMITIVE_KW: 
			case DOT: 
			case KW_TYPEOF: 
				return true;
			
			default:
				return false;
			}
		}
	}
	
	public static TypeModifierKinds determineTypeModifier(DeeTokens tokenType) {
		switch (tokenType) {
		case KW_CONST: return TypeModifierKinds.CONST;
		case KW_IMMUTABLE: return TypeModifierKinds.IMMUTABLE;
		case KW_SHARED: return TypeModifierKinds.SHARED;
		case KW_INOUT: return TypeModifierKinds.INOUT;
		default:
			return null;
		}
	}
	
	protected static boolean isTypeModifier(DeeTokens tokenType) {
		return determineTypeModifier(tokenType) != null;
	}
	
	public RefIdentifier parseRefIdentifier() {
		BaseLexElement id = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
		return conclude(id.getError(), srEffective(id, new RefIdentifier(idTokenToString(id))));
	}
	
	protected RefPrimitive parseRefPrimitive_start(DeeTokens primitiveType) {
		LexElement primitive = consumeLookAhead(primitiveType);
		return conclude(srOf(primitive, new RefPrimitive(primitive.token)));
	}
	
	public NodeResult<RefModuleQualified> parseRefModuleQualified() {
		if(!tryConsume(DeeTokens.DOT))
			return nullResult();
		int nodeStart = lastLexElement().getStartPos();
		
		RefIdentifier id = parseRefIdentifier();
		return resultConclude(isMissing(id), srToPosition(nodeStart, new RefModuleQualified(id)));
	}
	
	public NodeResult<RefTypeof> parseRefTypeof() {
		if(!tryConsume(DeeTokens.KW_TYPEOF))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Expression exp = null;
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.KW_RETURN)) {
				exp = conclude(srOf(lastLexElement(), new RefTypeof.ExpRefReturn()));
			} else {
				exp = parseExpression_toMissing();
			}
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		}
		return parse.resultConclude(new RefTypeof(exp));
	}
	
	protected NodeResult<RefTypeModifier> parseRefTypeModifier_start(TypeModifierKinds modKind) {
		assertTrue(lookAhead().sourceValue.equals(modKind.sourceValue));
		consumeLookAhead();
		ParseHelper parse = new ParseHelper();
		
		Reference ref = null;
		boolean hasParens = false;
		if(parse.consumeOptional(DeeTokens.OPEN_PARENS)) {
			ref = parseTypeReference_ToMissing(true).node; 
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
			hasParens = true;
		} else {
			ref = parse.checkResult(parseTypeReference());
			if(ref == null) {
				ref = parseMissingTypeReference(RULE_REFERENCE);
				parse.setRuleBroken(true);
			}
		}
		return parse.resultConclude(new RefTypeModifier(modKind, ref, hasParens));
	}
	
	protected NodeResult<Reference> parseReference_referenceStart(Reference leftRef, boolean parsingExp) {
		return parseReference_referenceStart_do(leftRef, parsingExp, false);
	}
	protected NodeResult<Reference> parseReference_referenceStart_do(Reference leftRef, boolean parsingExp
		, boolean templateOnly) {
		assertNotNull(leftRef);
		ParseHelper parse = new ParseHelper(leftRef == null ? -1 : leftRef.getStartPos());
		
		if(isTemplateInstanceLookahead() && isValidTemplateReferenceSyntax(leftRef)){ // template instance
			consumeLookAhead();
			
			ITemplateRefNode tplRef = (ITemplateRefNode) leftRef;
			NodeListView<Resolvable> tplArgs = null;
			Resolvable singleArg = null;
			
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				tplArgs = parseTypeOrExpArgumentList(parse, DeeTokens.COMMA, DeeTokens.CLOSE_PARENS);
			} else {
				if(leftRef instanceof RefTemplateInstance) {
					RefTemplateInstance refTplInstance = (RefTemplateInstance) leftRef;
					if(refTplInstance.isSingleArgSyntax()) {
						parse.store(createError(ParserErrorTypes.NO_CHAINED_TPL_SINGLE_ARG, 
							refTplInstance.getSourceRange(), null));
					}
				}
				
				if(lookAheadGrouped() == DeeTokens.PRIMITIVE_KW) {
					singleArg = parseRefPrimitive_start(lookAhead());	
				} else if(lookAheadGrouped() == DeeTokens.IDENTIFIER) { 
					singleArg = parseRefIdentifier();
				} else {
					singleArg = nullExpToParseMissing(parseSimpleLiteral(), RULE_TPL_SINGLE_ARG);
				}
			}
			leftRef = parse.conclude(new RefTemplateInstance(tplRef, singleArg, tplArgs));
			
		} else if(templateOnly) {
			return result(false, leftRef);
		} else if(lookAhead() == DeeTokens.DOT && leftRef instanceof IQualifierNode) {
			IQualifierNode qualifier = (IQualifierNode) leftRef;
			assertTrue(!RefQualified.isExpressionQualifier(qualifier));
			consumeLookAhead();
			RefIdentifier qualifiedId = parseRefIdentifier();
			parse.setRuleBroken(isMissing(qualifiedId));
			leftRef = parse.conclude(new RefQualified(qualifier, qualifiedId));
			
		} else if(!parsingExp && tryConsume(DeeTokens.STAR)) {
			leftRef = conclude(srToPosition(leftRef, new RefTypePointer(leftRef)));
		} else if(!parsingExp && lookAhead() == DeeTokens.OPEN_BRACKET) {
			leftRef = parseBracketReference(leftRef, parse);
		} else if(!parsingExp && (tryConsume(DeeTokens.KW_FUNCTION) || tryConsume(DeeTokens.KW_DELEGATE))) {
			leftRef = parse.checkResult(thisParser().parseRefTypeFunction_afterReturnType(leftRef));
		} else {
			return result(false, leftRef);
		}
		if(parse.ruleBroken)
			return result(true, leftRef);
		return parseReference_referenceStart_do(leftRef, parsingExp, templateOnly);
	}
	
	public Reference parseBracketReference(Reference leftRef, ParseHelper parse) {
		consumeLookAhead(DeeTokens.OPEN_BRACKET);
		
		TypeOrExpResult argTypeOrExp = parseTypeOrExpression(InfixOpType.ASSIGN); 
		
		if(lookAhead() == DeeTokens.DOUBLE_DOT) {
			Expression startIndex = nullExpToParseMissing(argTypeOrExp.toExpression().node);
			consumeLookAhead(DeeTokens.DOUBLE_DOT);
			Expression endIndex = parseAssignExpression_toMissing();
			parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
			return parse.conclude(new RefSlice(leftRef, startIndex, endIndex));
		}
		parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
		
		Resolvable resolvable = argTypeOrExp.toFinalResult(true).node;
		if(resolvable == null) {
			return parse.conclude(new RefTypeDynArray(leftRef));
		} else {
			return parse.conclude(new RefIndexing(leftRef, resolvable));
		}
	}
	
	public boolean isTemplateInstanceLookahead() {
		return lookAhead() == DeeTokens.NOT && !(lookAhead(1) == DeeTokens.KW_IN || lookAhead(1) == DeeTokens.KW_IS);
	}
	
	public boolean isValidTemplateReferenceSyntax(Reference leftRef) {
		return leftRef instanceof ITemplateRefNode;
	}
	
	public Reference parseCStyleSuffix(ParseHelper parse) {
		if(lookAhead() != DeeTokens.OPEN_BRACKET) {
			parse.requireBrokenCheck();
			return null;
		}
		CStyleRootRef cstyleRootRef = conclude(srAt(getSourcePosition()), new CStyleRootRef());
		NodeResult<Reference> cstyleDeclaratorSuffix = parseCStyleDeclaratorSuffix(cstyleRootRef);
		parse.requireBrokenCheck();
		return parse.checkResult(cstyleDeclaratorSuffix);
	}
	
	protected NodeResult<Reference> parseCStyleDeclaratorSuffix(Reference leftRef) {
		if(lookAhead() != DeeTokens.OPEN_BRACKET) {
			return result(false, leftRef);
		}
		ParseHelper parse = new ParseHelper(leftRef.getStartPos());
		leftRef = parseBracketReference(leftRef, parse);
		if(parse.ruleBroken)
			return result(true, leftRef);
		return parseCStyleDeclaratorSuffix(leftRef);
	}
	
	/* --------------------- EXPRESSIONS --------------------- */
	
	public static final ParseRuleDescription RULE_EXPRESSION = new ParseRuleDescription("Expression");
	public static final ParseRuleDescription RULE_TYPE_OR_EXP = new ParseRuleDescription("Reference or Expression");
	
	public static final InfixOpType ANY_OPERATOR = InfixOpType.COMMA;
	
	
	public final NodeResult<Expression> parseExpression() {
		return parseExpression(ANY_OPERATOR);
	}
	public final NodeResult<Expression> parseExpression_toMissing(boolean breakOnMissing, 
		ParseRuleDescription expectedRule) {
		return nullExpToParseMissing(parseExpression(), breakOnMissing, expectedRule);
	}
	public final Expression parseExpression_toMissing() {
		return nullExpToParseMissing(parseExpression().node);
	}
	
	
	public final NodeResult<Expression> parseAssignExpression() {
		return parseExpression(InfixOpType.ASSIGN);
	}
	public final NodeResult<Expression> parseAssignExpression_toMissing(boolean breakOnMissing, 
		ParseRuleDescription expectedRule) {
		return nullExpToParseMissing(parseAssignExpression(), breakOnMissing, expectedRule);
	}
	public final Expression parseAssignExpression_toMissing() {
		return nullExpToParseMissing(parseAssignExpression().node);
	}
	
	
	protected NodeResult<Expression> parseExpression(InfixOpType precedenceLimit) {
		return new ParseRule_Expression().parseExpressionDo(precedenceLimit);
	}
	protected Expression parseExpression_toMissing(InfixOpType precedenceLimit) {
		return nullExpToParseMissing(parseExpression(precedenceLimit).node);
	}

	
	/* ---------------- Missing stuff ---------------- */
	
	protected Expression nullExpToParseMissing(Expression exp) {
		return nullExpToParseMissing(exp, RULE_EXPRESSION);
	}
	protected Expression nullExpToParseMissing(Expression exp, ParseRuleDescription expectedRule) {
		return exp != null ? exp : parseMissingExpression(expectedRule);
	}
	
	public final NodeResult<Expression> nullExpToParseMissing(NodeResult<Expression> expResult, 
		boolean breakOnMissing, ParseRuleDescription expectedRule) {
		return expResult.node != null ? expResult :
			result(expResult.ruleBroken || breakOnMissing, parseMissingExpression(expectedRule));
	}
	
	protected Expression parseMissingExpression(ParseRuleDescription expectedRule) {
		return parseMissingExpression(expectedRule, true);
	}
	
	protected Expression parseMissingExpression(ParseRuleDescription expectedRule, boolean consumeIgnoreTokens) {
		int nodeStart = getSourcePosition();
		if(consumeIgnoreTokens) {
			consumeSubChannelTokens();
		}
		int nodeEnd = getSourcePosition();
		return createMissingExpression(expectedRule, lastLexElement(), nodeStart, nodeEnd);
	}
	
	protected Expression createMissingExpression(ParseRuleDescription expectedRule, LexElement previousToken,
		int nodeStart, int nodeEnd) {
		
		MissingExpression missingExp = new MissingExpression();
		missingExp.setSourceRange(nodeStart, nodeEnd - nodeStart);
		ParserError error = null;
		if(expectedRule != null) {
			error = createError(ParserErrorTypes.EXPECTED_RULE, previousToken.getSourceRange(), expectedRule.name);
		}
		return conclude(error, missingExp);
	}
	
	public boolean isMissing(Expression exp) {
		return exp == null || exp instanceof MissingExpression;
	}
	
	public Expression createExpReference(Reference reference, boolean reportError) {
		ExpReference expReference = createExpReference(reference);
		return conclude(reportError ? createErrorTypeAsExpValue(reference) : null, expReference);
	}
	
	protected ExpReference createExpReference(Reference ref) {
		ExpReference node = new ExpReference(ref);
		node.setSourceRange(ref.getSourceRange());
		return node;
	}
	
	protected ParserError createErrorTypeAsExpValue(Reference reference) {
		return createError(ParserErrorTypes.TYPE_USED_AS_EXP_VALUE, reference.getSourceRange(), null);
	}
	
	/* ============================ TypeOrExp ============================ */
	
protected class ParseRule_Expression {
	
	public boolean breakRule; // TODO : remove this class
	
	public ParseRule_Expression() {
		breakRule = false;
	}
	
	public boolean shouldReturnToParseRuleTopLevel(Expression expSoFar) {
		boolean result = breakRule;
		assertTrue(isEnabled() == !result);
		return result || expSoFar == null;
	}
	
	public void setToEParseBroken(boolean parseBroken) {
		this.breakRule = parseBroken;
		if(breakRule) {
			setEnabled(false);
		}
	}
	
	protected Expression expConclude(NodeResult<? extends Expression> result) {
		setToEParseBroken(result.ruleBroken);
		return result.node;
	}
	
	public NodeResult<Expression> parseExpressionDo(InfixOpType precedenceLimit) {
		Expression exp = parseTypeOrExpression_start(precedenceLimit);
		if(breakRule) {
			setEnabled(true);
		} 
		assertTrue(thisParser().isEnabled());
		return result(breakRule, exp);
	}
	
	protected Expression parseTypeOrExpression_start(InfixOpType precedenceLimit) {
		Expression prefixExp = parsePrimaryExpression();
		if(shouldReturnToParseRuleTopLevel(prefixExp)) {
			return prefixExp;
		}
		
		return parseTypeOrExpression_fromUnary(precedenceLimit, prefixExp);
	}
	
	public Expression parseTypeOrExpression_fromUnary(InfixOpType precedenceLimit, Expression unaryExp) {
		unaryExp = parsePostfixExpression(unaryExp);
		if(shouldReturnToParseRuleTopLevel(unaryExp)) {
			return unaryExp;
		}
		
		return parseInfixOperators(precedenceLimit, unaryExp);
	}
	
	protected Expression parseUnaryExpression() {
		return parseTypeOrExpression_start(InfixOpType.NULL);
	}
	
	protected Expression parsePrimaryExpression() {
		Expression simpleLiteral = parseSimpleLiteral();
		if(simpleLiteral != null) {
			return simpleLiteral;
		}
		
		switch (lookAheadGrouped()) {
		case KW_ASSERT:
			return expConclude(parseAssertExpression());
		case KW_MIXIN:
			return expConclude(parseMixinExpression());
		case KW_IMPORT:
			return expConclude(parseImportExpression());
		case KW_TYPEID:
			return expConclude(parseTypeIdExpression());
		case KW_NEW:
			return expConclude(parseNewExpression());
		case KW_CAST:
			return expConclude(parseCastExpression());
		case KW_IS:
			return expConclude(parseIsExpression());
		case KW___TRAITS:
			return expConclude(parseTraitsExpression());
		case AND:
		case INCREMENT:
		case DECREMENT:
		case STAR:
		case MINUS:
		case PLUS:
		case NOT:
		case CONCAT:
		case KW_DELETE: {
			LexElement prefixExpOpToken = consumeLookAhead();
			PrefixOpType prefixOpType = PrefixOpType.tokenToPrefixOpType(prefixExpOpToken.token.type);
			
			Expression exp = parseUnaryExpression();
			if(exp == null) {
				exp = parseMissingExpression(RULE_EXPRESSION);
				setToEParseBroken(true);
			}
			
			return conclude(srToPosition(prefixExpOpToken, new ExpPrefix(prefixOpType, exp)));
		}
		case OPEN_PARENS:
			return expConclude(matchParenthesesStart());
			
		case OPEN_BRACE: {
			int startPos = lookAheadElement().getStartPos();
			return expConclude(parseFunctionLiteral_atFunctionBody(startPos, null, null, null, null));
		}
		case KW_FUNCTION:
		case KW_DELEGATE:
			return expConclude(parseFunctionLiteral_start());
			
		case OPEN_BRACKET:
			return parseBracketList(null);
		case IDENTIFIER:
			if(lookAhead(1) == DeeTokens.LAMBDA) {
				return expConclude(parseSimpleLambdaLiteral_start());
			} // else fallthrough to TypeReference:
		default:
			NodeResult<Reference> typeRefResult = parseTypeReference_start(true);
			Reference ref = typeRefResult.node;
			if(ref == null) {
				return null;
			}
			
			boolean isTypeAsExpError = refIsErrorToUseInExp(ref);
			if(!(ref instanceof RefQualified || ref instanceof RefModuleQualified)) {
				setToEParseBroken(typeRefResult.ruleBroken);
			}
			return createExpReference(ref, isTypeAsExpError);
		}
	}
	
	protected Expression parsePostfixExpression(Expression exp) {
		
		switch (lookAheadGrouped()) {
		case DECREMENT:
		case INCREMENT: {
			exp = parsePostfixOpExpression_atOperator(exp);
			return parsePostfixExpression(exp);
		}
		case POW: {
			return parseInfixOperator(exp, InfixOpType.POW);
		}
		case OPEN_PARENS: {
			exp = expConclude(parseCallExpression_atParenthesis(exp));
			if(shouldReturnToParseRuleTopLevel(exp))
				return exp;
			return parsePostfixExpression(exp);
		}
		case OPEN_BRACKET: {
			exp = parseBracketList(exp);
			if(shouldReturnToParseRuleTopLevel(exp)) {
				return exp;
			}
			return parsePostfixExpression(exp);
		}
		case DOT: {
			ParseHelper parse = new ParseHelper(exp);
			IQualifierNode qualifier = exp;
			exp = null;
			if(qualifier instanceof ExpReference) {
				ExpReference expReference = (ExpReference) qualifier;
				if(expReference.ref instanceof RefQualified) {
					assertTrue(((RefQualified) expReference.ref).isExpressionQualifier);
				} else if(expReference.ref instanceof RefTemplateInstance) {
				} else {
					assertFail(); // ...otherwise refqualified would have been parsed already
				}
			}
			consumeLookAhead();
			RefIdentifier qualifiedId = parseRefIdentifier();
			Reference ref = parse.conclude(new RefQualified(qualifier, qualifiedId));
			ref = parseReference_referenceStart_do(ref, true, true).node; // TODO check break...
			return parsePostfixExpression(conclude(createExpReference(ref)));
		}
		default:
			return exp;
		}
	}
	
	protected Expression parseInfixOperators(InfixOpType precedenceLimit, final Expression leftExp) {
		DeeTokens gla = lookAheadGrouped();
		
		InfixOpType infixOpAhead = InfixOpType.tokenToInfixOpType(gla);
		if(lookAhead() == DeeTokens.NOT) {
			if(lookAhead(1) == DeeTokens.KW_IS) {
				infixOpAhead = InfixOpType.NOT_IS;
			} else if(lookAhead(1) == DeeTokens.KW_IN) {
				infixOpAhead = InfixOpType.NOT_IN;
			}
		}
		
		if(infixOpAhead == null) {
			return leftExp;
		}
		
		// If lower precedence it can't be parsed to right expression, 
		// instead this expression must become left children of new parent
		if(infixOpAhead.precedence < precedenceLimit.precedence)
			return leftExp;
		
		Expression exp = parseInfixOperator(leftExp, infixOpAhead);
		if(shouldReturnToParseRuleTopLevel(exp)) {
			return exp;
		}
		
		return parseInfixOperators(precedenceLimit, exp);
	}
	
	public InfixOpType getPrecedenceForInfixOpRightExp(InfixOpType infixOpLA) {
		switch (infixOpLA.category) {
		case COMMA: return InfixOpType.COMMA;
		case ASSIGN: return InfixOpType.ASSIGN;
		case CONDITIONAL: return InfixOpType.CONDITIONAL;
		case LOGICAL_OR: return InfixOpType.LOGICAL_AND;
		case LOGICAL_AND: return InfixOpType.OR;
		case OR: return InfixOpType.XOR;
		case XOR: return InfixOpType.AND;
		case AND: return InfixOpType.EQUALS;
		case EQUALS: return InfixOpType.SHIFT;
		case SHIFT: return InfixOpType.ADD;
		case ADD: return InfixOpType.MUL;
		case MUL: return InfixOpType.NULL;
		case POW: return InfixOpType.NULL;
		default:
			throw assertUnreachable();
		}
	}
	
		public Expression parseInfixOperator(final Expression leftExp, final InfixOpType opType) {
			ParseHelper parse = new ParseHelper(assertNotNull_(leftExp));
			
			Expression rightExp = null;
			
			consumeLookAhead();
			if(opType == InfixOpType.NOT_IS || opType == InfixOpType.NOT_IN) {
				consumeLookAhead(); // consume second token
			}
			
			if(opType != InfixOpType.MUL) {
				parse.store(checkValidAssociativityN(leftExp, opType));
			} else {
				assertTrue(lastLexElement().token.type == DeeTokens.STAR);
			}
			
			Expression middleExp = null;
			
			parsing: {
				if(opType == InfixOpType.CONDITIONAL) {
					middleExp = nullExpToParseMissing(parseExpression().node);
					
					if(parse.consumeRequired(DeeTokens.COLON).ruleBroken) {
						setToEParseBroken(true);
						break parsing;
					}
				}
				
				InfixOpType rightExpPrecedence = getPrecedenceForInfixOpRightExp(opType);
				
				NodeResult<Expression> expResult = parseExpression(rightExpPrecedence);
				setToEParseBroken(expResult.ruleBroken);
				rightExp = expResult.node;
				
				if(isMissing(rightExp)) {
					rightExp = parseMissingExpression(RULE_EXPRESSION);
					setToEParseBroken(true);
				} else {
					parse.store(checkValidAssociativityN(rightExp, opType));
				}
			}
			
			if(opType == InfixOpType.CONDITIONAL) {
				return parse.conclude(new ExpConditional(leftExp, middleExp, rightExp));
			}
			
			return parse.conclude(new ExpInfix(leftExp, opType, rightExp));
		}
		
	
	protected ParserError checkValidAssociativityN(Expression exp, InfixOpType op) {
		// Check for some syntax situations which are technically not allowed by the grammar:
		switch (op.category) {
		case OR: case XOR: case AND: case EQUALS:
			if(exp instanceof ExpInfix && ((ExpInfix) exp).kind.category == InfixOpType.EQUALS) {
				return createError(ParserErrorTypes.EXP_MUST_HAVE_PARENTHESES, exp.getSourceRange(), op.sourceValue);
			}
		default: return null;
		}
	}
	
	public Expression parseArrayLiteral() {
		return parseBracketList(null);
	}
	
		protected Expression parseBracketList(Expression calleeExp) {
			LexElement tokenBeforeBracket = lastLexElement();
			if(tryConsume(DeeTokens.OPEN_BRACKET) == false)
				return null;
			
			final boolean isExpIndexing = calleeExp != null;
			ParseHelper parse = isExpIndexing ? new ParseHelper(calleeExp) : new ParseHelper();
			
			ArrayList<Expression> elements = new ArrayList<Expression>(4);
			ArrayList<MapArrayLiteralKeyValue> mapElements = null;
			
			boolean firstElement = true;
			
			while(true) {
				Expression exp1;
				Expression exp2 = null;
				ParseHelper exp2parse = null;
				
				exp1 = parseAssignExpression().node;
				if(lookAhead() == DeeTokens.COMMA) {
					exp1 = nullExpToParseMissing(exp1);
				}
				
				if(firstElement) {
					
					if(!isExpIndexing && lookAhead() == DeeTokens.COLON) {
						exp1 = nullExpToParseMissing(exp1);
						consumeLookAhead(DeeTokens.COLON);
						exp2parse = new ParseHelper(exp1);
						exp2 = parseAssignExpression_toMissing();
						mapElements = new ArrayList<MapArrayLiteralKeyValue>();
					} else if(lookAhead() == DeeTokens.DOUBLE_DOT) {
						exp1 = nullExpToParseMissing(exp1);
						consumeLookAhead(DeeTokens.DOUBLE_DOT);
						exp2 = parseAssignExpression_toMissing();
						
						parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
						setToEParseBroken(parse.ruleBroken);
						
						if(calleeExp == null) {
							int nodePos = parse.nodeStart; // This is bracket start
							calleeExp = createMissingExpression(RULE_EXPRESSION, tokenBeforeBracket, nodePos, nodePos);
							parse.nodeStart = calleeExp.getStartPos();
						}
						
						return parse.conclude(new ExpSlice(calleeExp, exp1, exp2));
					} else if(exp1 == null) {
						break;
					}
				} else {
					if(mapElements != null) {
						if(lookAhead() == DeeTokens.COLON) {
							exp1 = nullExpToParseMissing(exp1);
						}
						
						if(exp1 != null) {
							exp2parse = new ParseHelper(exp1);
							if(exp2parse.consumeExpected(DeeTokens.COLON)) {
								exp2 = parseAssignExpression_toMissing();
							}
						}
					}
				}
				firstElement = false;
				
				if(mapElements == null ) {
					elements.add(exp1);
				} else {
					if(exp2parse == null) {
						mapElements.add(null);
					} else {
						mapElements.add(exp2parse.conclude(new MapArrayLiteralKeyValue(exp1, exp2)));
					}
				}
				
				if(tryConsume(DeeTokens.COMMA)) {
					assertTrue(exp1 != null);
					continue;
				}
				break;
			}
			
			parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
			setToEParseBroken(parse.ruleBroken);
			
			if(calleeExp == null) {
				if(mapElements != null ) {
					return parse.conclude(new ExpLiteralMapArray(nodeListView(mapElements)));
				} else {
					return parse.conclude(new ExpLiteralArray(nodeListView(elements)));
				}
			}
			if(elements == null || elements.size() == 0) {
				return parse.conclude(new ExpSlice(calleeExp));
			}
			return parse.conclude(new ExpIndex(calleeExp, nodeListView(elements)));
		}
		
} /* ---------------- ParseRule_TypeOrExp END----------------*/
	
   // typeof and type modifier can appear in exp in a valid way, so no error in that case
	protected static boolean refIsErrorToUseInExp(Reference ref) {
		switch (ref.getNodeType()) {
		case REF_PRIMITIVE:
		case REF_TYPE_FUNCTION:
			return true;
		case REF_TYPE_DYN_ARRAY:
		case REF_TYPE_POINTER:
		case REF_INDEXING:
			return true;
		default:
			return false;
		}
	}
	
	/* ---------------- parse TypeOrExp ----------------*/
	
	public NodeResult<Resolvable> parseTypeOrExpression(boolean ambiguousToRef) {
		return parseTypeOrExpression(ANY_OPERATOR, ambiguousToRef);
	}
	
	public NodeResult<Resolvable> parseTypeOrAssignExpression(boolean ambiguousToRef) {
		return parseTypeOrExpression(InfixOpType.ASSIGN, ambiguousToRef);
	}
	
	public NodeResult<Resolvable> parseTypeOrExpression(InfixOpType precedenceLimit, boolean ambiguousToRef) {
		return parseTypeOrExpression(precedenceLimit).toFinalResult(ambiguousToRef).upcastTypeParam();
	}
	
	protected Resolvable nullTypeOrExpToParseMissing(Resolvable exp) {
		return exp != null ? exp : parseMissingExpression(RULE_TYPE_OR_EXP);
	}
	
	protected TypeOrExpResult parseTypeOrExpression(InfixOpType precedenceLimit) {
		DeeParserState initialState = thisParser().saveParserState();
		
		NodeResult<Reference> refResult = parseTypeReference();
		DeeParserState refResultState = thisParser().saveParserState();
		assertTrue(thisParser().isEnabled());
		thisParser().restoreOriginalState(initialState);
		
		NodeResult<Expression> expResult = parseExpression(precedenceLimit);
		int expResultLexPosition = thisParser().getEnabledLexSource().getLexElementPosition();
		int refResultLexPosition = refResultState.lexSource.getLexElementPosition();
		
		if(expResultLexPosition > refResultLexPosition) {
			return new TypeOrExpResult(null, expResult);
		} else if(refResultLexPosition > expResultLexPosition) {
			thisParser().restoreOriginalState(refResultState);
			return new TypeOrExpResult(refResult, null);
		} else {
			return new TypeOrExpResult(refResult, expResult);
		}
	}
	
	protected final class TypeOrExpResult {
		
		private NodeResult<Reference> refResult;
		private NodeResult<Expression> expResult;
		
		public TypeOrExpResult(NodeResult<Reference> refResult, NodeResult<Expression> expResult) {
			this.refResult = refResult;
			this.expResult = expResult;
		}
		
		public boolean isNull() {
			return (refResult == null && expResult == null) ||
				(refResult != null && expResult != null && refResult.node == null);
		}
		
		public boolean isExpOnly() {
			return !isNull() && refResult == null;
		}
		
		public boolean isRefOnly() {
			return !isNull() && expResult == null;
		}
		
		public NodeResult<Reference> toReference() {
			assertTrue(!isExpOnly());
			if(isNull()) {
				return nullResult();
			}
			return refResult;
		}
		
		public NodeResult<Expression> toExpression() {
			assertTrue(!isRefOnly());
			if(isNull()) {
				return nullResult();
			}
			return assertNotNull_(expResult);
		}
		
		public NodeResult<? extends Resolvable> toFinalResult(boolean ambiguousToRef) {
			if(isRefOnly()) {
				return refResult;
			}
			if(isExpOnly()) {
				return expResult;
			}
			return ambiguousToRef ? refResult : expResult;
		}
	}
	
	protected Expression resolvableToExp(Resolvable resolvable, boolean reportError) {
		if(resolvable instanceof Reference) {
			Reference reference = (Reference) resolvable;
			return createExpReference(reference, reportError);
		}
		return (Expression) resolvable;
	}
	
	public Expression parseUnaryExpression() {
		return new ParseRule_Expression().parseUnaryExpression();
	}
	
	public Expression parseExpression_fromUnary(InfixOpType precedenceLimit, Expression unaryExp) {
		ParseRule_Expression parseRule_TypeOrExp = new ParseRule_Expression();
		return parseRule_TypeOrExp.parseTypeOrExpression_fromUnary(precedenceLimit, unaryExp);
	}
	
	public Expression parseArrayLiteral() {
		return new ParseRule_Expression().parseArrayLiteral();
	}
	
	public Expression parseSimpleLiteral() {
		switch (lookAheadGrouped()) {
		case KW_TRUE: case KW_FALSE:
			Token token = consumeLookAhead().token;
			return conclude(srOf(lastLexElement(), new ExpLiteralBool(token.type == DeeTokens.KW_TRUE)));
		case KW_THIS:
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpThis()));
		case KW_SUPER:
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpSuper()));
		case KW_NULL:
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpNull()));
		case DOLLAR:
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpArrayLength()));
			
		case INTEGER:
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpLiteralInteger(lastLexElement().token)));
		case CHARACTER: 
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpLiteralChar(lastLexElement().token)));
		case FLOAT:
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpLiteralFloat(lastLexElement().token)));
		case STRING:
			return parseStringLiteral();
		default:
			return null;
		}
	}
	
	public Expression parseStringLiteral() {
		ArrayList<Token> stringTokens = new ArrayList<Token>();
		
		while(lookAheadGrouped() == DeeTokens.STRING) {
			Token string = consumeLookAhead().token;
			stringTokens.add(string);
		}
		Token[] tokenStrings = ArrayUtil.createFrom(stringTokens, Token.class);
		return conclude(srToPosition(tokenStrings[0].getStartPos(), new ExpLiteralString(tokenStrings)));
	}
	
	protected ExpPostfixOperator parsePostfixOpExpression_atOperator(Expression exp) {
		Token op = consumeLookAhead().token;
		return conclude(srToPosition(exp, new ExpPostfixOperator(exp, PostfixOpType.tokenToPrefixOpType(op.type))));
	}
	
	protected NodeResult<ExpCall> parseCallExpression_atParenthesis(Expression callee) {
		ParseHelper parse = new ParseHelper(callee);
		consumeLookAhead(DeeTokens.OPEN_PARENS);
		NodeListView<Expression> args = parseExpArgumentList(parse, true, DeeTokens.CLOSE_PARENS);
		return parse.resultConclude(new ExpCall(callee, args));
	}
	
	protected NodeListView<Expression> parseExpArgumentList(ParseHelper parse, boolean canBeEmpty, 
		DeeTokens tokenLISTCLOSE) {
		SimpleListParseHelper<Expression> elementListParse = new SimpleListParseHelper<Expression>() {
			@Override
			protected Expression parseElement(boolean createMissing) {
				Expression arg = parseAssignExpression().node;
				return createMissing ? nullExpToParseMissing(arg) : arg;
			}
		};
		elementListParse.parseSimpleList(DeeTokens.COMMA, canBeEmpty, true);
		
		parse.consumeRequired(tokenLISTCLOSE);
		return elementListParse.members;
	}
	
	protected final class TypeOrExpArgumentListSimpleParse extends SimpleListParseHelper<Resolvable> {
		@Override
		protected Resolvable parseElement(boolean createMissing) {
			Resolvable arg = parseTypeOrAssignExpression(true).node;
			return createMissing ? nullTypeOrExpToParseMissing(arg) : arg;
		}
	}
	
	protected final NodeListView<Resolvable> parseTypeOrExpArgumentList(ParseHelper parse, DeeTokens tkSEP, 
		DeeTokens tkCLOSE) {
		
		SimpleListParseHelper<Resolvable> elementListParse = new TypeOrExpArgumentListSimpleParse();
		elementListParse.parseSimpleList(tkSEP, true, true);
		parse.consumeRequired(tkCLOSE);
		return elementListParse.members;
	}
	
	protected NodeResult<? extends Expression> matchParenthesesStart() {
		assertTrue(lookAhead() == DeeTokens.OPEN_PARENS);
		ParseHelper parse = new ParseHelper(lookAheadElement());
		
		DeeParser_RuleParameters fnParametersRule = thisParser().isFunctionParameters(parse);
		
		if(!parse.ruleBroken) {
			ArrayView<FunctionAttributes> fnAttributes = thisParser().parseFunctionAttributes();
			
			if(lookAhead() == DeeTokens.OPEN_BRACE || lookAhead() == DeeTokens.LAMBDA) {
				fnParametersRule.acceptDeciderResult();
				ArrayView<IFunctionParameter> fnParams = fnParametersRule.getAsFunctionParameters();
				return parseFunctionLiteral_atFunctionBody(parse.nodeStart, null, null, fnParams, fnAttributes);
			}
		}
		
		fnParametersRule.discardDeciderResult();
		return parseParenthesesExp();
	}
	
	protected NodeResult<ExpSimpleLambda> parseSimpleLambdaLiteral_start() {
		ProtoDefSymbol defId = thisParser().parseDefId();
		consumeLookAhead(DeeTokens.LAMBDA);
		
		ParseHelper parse = new ParseHelper(defId.getStartPos());
		Expression bodyExp = parse.checkResult(parseAssignExpression_toMissing(true, RULE_EXPRESSION));
		
		SimpleLambdaDefUnit lambdaDefId = conclude(defId.nameSourceRange, new SimpleLambdaDefUnit(defId));
		return parse.resultConclude(new ExpSimpleLambda(lambdaDefId, bodyExp));
	}
	
	public NodeResult<ExpFunctionLiteral> parseFunctionLiteral_start() {
		assertTrue(lookAhead() == DeeTokens.KW_FUNCTION || lookAhead() == DeeTokens.KW_DELEGATE);
		consumeLookAhead();
		boolean isFunctionKeyword = lastLexElement().token.type == DeeTokens.KW_FUNCTION;
		ParseHelper parse = new ParseHelper();
		
		Reference retType = parseTypeReference().node;
		
		ArrayView<IFunctionParameter> fnParams = null;
		ArrayView<FunctionAttributes> fnAttributes = null;
		
		parsing: {
			fnParams = thisParser().parseFunctionParameters(parse);
			if(parse.ruleBroken) break parsing;
			
			fnAttributes = thisParser().parseFunctionAttributes();
			
			return parseFunctionLiteral_atFunctionBody(parse.nodeStart, isFunctionKeyword, retType, fnParams, 
				fnAttributes);
		}
		
		return parse.resultConclude(
			new ExpFunctionLiteral(isFunctionKeyword, retType, fnParams, fnAttributes, null, null));
	}
	
	protected NodeResult<ExpFunctionLiteral> parseFunctionLiteral_atFunctionBody(int nodeStart,
		Boolean isFunctionKeyword, Reference retType, ArrayView<IFunctionParameter> fnParams,
		ArrayView<FunctionAttributes> fnAttributes) 
	{
		if(tryConsume(DeeTokens.LAMBDA)) {
			assertTrue(fnParams != null);
			NodeResult<Expression> litBody = parseAssignExpression_toMissing(true, RULE_EXPRESSION);
			
			return resultConclude(litBody.ruleBroken, srToPosition(nodeStart, 
				new ExpFunctionLiteral(isFunctionKeyword, retType, fnParams, fnAttributes, null, litBody.node)));
		} else {
			NodeResult<? extends IFunctionBody> litBody = thisParser().parseBlockStatement(true, true);
			
			return resultConclude(litBody.ruleBroken, srToPosition(nodeStart, 
				new ExpFunctionLiteral(isFunctionKeyword, retType, fnParams, fnAttributes, litBody.node, null)));
		}
	}
	
	public NodeResult<ExpParentheses> parseParenthesesExp() {
		if(!tryConsume(DeeTokens.OPEN_PARENS))
			return null;
		ParseHelper parse = new ParseHelper();
		
		TypeOrExpResult arg = parseTypeOrExpression(ANY_OPERATOR);
		Resolvable resolvable;
		
		boolean isDotAfterParensSyntax = lookAhead() == DeeTokens.CLOSE_PARENS && lookAhead(1) == DeeTokens.DOT;
		if(isDotAfterParensSyntax) {
			resolvable = nullTypeOrExpToParseMissing(arg.toFinalResult(true).node);
		} else {
			resolvable = arg.toFinalResult(false).node;
			resolvable = nullExpToParseMissing(resolvableToExp(resolvable, true));
		}
		parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		
		return parse.resultConclude(new ExpParentheses(isDotAfterParensSyntax, resolvable));
	}
	
	public NodeResult<ExpAssert> parseAssertExpression() {
		if(tryConsume(DeeTokens.KW_ASSERT) == false)
			return null;
		ParseHelper parse = new ParseHelper();
		
		Expression exp = null;
		Expression msg = null;
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			exp = parseAssignExpression_toMissing();
			if(tryConsume(DeeTokens.COMMA)) {
				msg = parseAssignExpression_toMissing();
			}
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		}
		
		return parse.resultConclude(new ExpAssert(exp, msg));
	}
	
	public NodeResult<ExpImportString> parseImportExpression() {
		if(tryConsume(DeeTokens.KW_IMPORT) == false)
			return null;
		ParseHelper parse = new ParseHelper();
		
		Expression expParentheses = parseExpressionAroundParentheses(parse, true, true);
		return parse.resultConclude(new ExpImportString(expParentheses));
	}
	
	public NodeResult<ExpMixinString> parseMixinExpression() {
		if(tryConsume(DeeTokens.KW_MIXIN) == false)
			return null;
		ParseHelper parse = new ParseHelper();
		
		Expression expParentheses = parseExpressionAroundParentheses(parse, true, true);
		return parse.resultConclude(new ExpMixinString(expParentheses));
	}

	public Expression parseExpressionAroundParentheses(ParseHelper parse, boolean isRequired, 
		boolean brokenIfMissing) {
		boolean isOptional = !isRequired;
		if(parse.consume(DeeTokens.OPEN_PARENS, isOptional, brokenIfMissing) == false) {
			if(!isOptional) {
				return conclude(srToPosition(getSourcePosition(), new MissingParenthesesExpression()));
			}
			return null;
		} else {
			Expression exp = parseExpression_toMissing();
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
			return exp;
		}
	}
	
	public NodeResult<ExpTypeId> parseTypeIdExpression() {
		if(tryConsume(DeeTokens.KW_TYPEID) == false)
			return null;
		ParseHelper parse = new ParseHelper();
		
		Reference ref = null;
		Expression exp = null;
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			Resolvable resolvable = nullTypeOrExpToParseMissing(parseTypeOrExpression(true).node);
			if(resolvable instanceof Reference) {
				ref = (Reference) resolvable;
			} else {
				exp = (Expression) resolvable;
			}
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		}
		if(ref != null) {
			return parse.resultConclude(new ExpTypeId(ref));
		}
		return parse.resultConclude(new ExpTypeId(exp));
	}
	
	public NodeResult<? extends Expression> parseNewExpression() {
		if(!tryConsume(DeeTokens.KW_NEW))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		NodeListView<Expression> allocArgs = null;
		Reference type = null;
		NodeListView<Expression> args = null;
		
		parsing: {
			if(parse.consumeOptional(DeeTokens.OPEN_PARENS)) {
				allocArgs = parseExpArgumentList(parse, true, DeeTokens.CLOSE_PARENS);
				if(parse.ruleBroken) break parsing;
			}
			
			if(parse.consumeOptional(DeeTokens.KW_CLASS)) {
				return parseNewAnonClassExpression_afterClassKeyword(parse, allocArgs);
			}
			if(parse.ruleBroken) break parsing;
			
			type = parseTypeReference_ToMissing(true).node;
			parse.setRuleBroken(isMissing(type));
			if(parse.ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				args = parseExpArgumentList(parse, true, DeeTokens.CLOSE_PARENS);
			}
		}
		
		return parse.resultConclude(new ExpNew(allocArgs, type, args));
	}
	
	protected NodeResult<ExpNewAnonClass> parseNewAnonClassExpression_afterClassKeyword(ParseHelper parse, 
		ArrayView<Expression> allocArgs) {
		
		ArrayView<Expression> args = null;
		SimpleListParseHelper<Reference> baseClasses = thisParser().new TypeReferenceSimpleListParse();
		DeclBlock declBody = null;
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				args = parseExpArgumentList(parse, true, DeeTokens.CLOSE_PARENS);
				if(parse.ruleBroken) break parsing;
			}
			
			baseClasses.parseSimpleList(DeeTokens.COMMA, true, false);
			
			declBody = parse.requiredResult(thisParser().parseDeclarationBlock(), DeeParser.RULE_DECLARATION_BLOCK);
		}
		
		return parse.resultConclude(new ExpNewAnonClass(allocArgs, args, baseClasses.members, declBody));
	}
	
	public NodeResult<? extends Expression> parseCastExpression() {
		if(!tryConsume(DeeTokens.KW_CAST))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Reference type = null;
		CastQualifiers qualifier = null;
		Expression exp = null;
		
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			
			qualifier = parseCastQualifier();
			if(qualifier == null) {
				type = parseTypeReference_ToMissing(false).node;
			}
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			
			exp = nullExpToParseMissing(parseUnaryExpression()); // TODO: check break
		}
		
		if(qualifier != null) {
			return parse.resultConclude(new ExpCastQual(qualifier, exp));
		} else {
			return parse.resultConclude(new ExpCast(type, exp));
		}
	}
	
	public CastQualifiers parseCastQualifier() {
		switch (lookAhead()) {
		case KW_CONST:
			return parseCastQualifier(DeeTokens.KW_SHARED, CastQualifiers.CONST_SHARED, CastQualifiers.CONST);
		case KW_INOUT:
			return parseCastQualifier(DeeTokens.KW_SHARED, CastQualifiers.INOUT_SHARED, CastQualifiers.INOUT);
		case KW_SHARED:
			if(lookAhead(2) == DeeTokens.CLOSE_PARENS && tryConsume(DeeTokens.KW_SHARED, DeeTokens.KW_CONST))
				return CastQualifiers.SHARED_CONST;
			return parseCastQualifier(DeeTokens.KW_INOUT, CastQualifiers.SHARED_INOUT, CastQualifiers.SHARED);
		case KW_IMMUTABLE:
			if(lookAhead(1) == DeeTokens.CLOSE_PARENS) {
				consumeLookAhead();
				return CastQualifiers.IMMUTABLE;
			}
		default: return null;
		}
	}
	
	public CastQualifiers parseCastQualifier(DeeTokens token1, CastQualifiers altDouble, CastQualifiers altSingle) {
		if(lookAhead(2) == DeeTokens.CLOSE_PARENS && lookAhead(1) == token1) {
			consumeLookAhead();
			consumeLookAhead();
			return altDouble;
		} else if(lookAhead(1) == DeeTokens.CLOSE_PARENS) {
			consumeLookAhead();
			return altSingle;
		} else {
			return null;
		}
	}
	
	public static final ParseRuleDescription RULE_IS_TYPE_SPEC = new ParseRuleDescription("IsTypeSpecialization");
	
	public NodeResult<? extends Expression> parseIsExpression() {
		if(!tryConsume(DeeTokens.KW_IS))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Reference typeRef = null;
		StaticIfExpIsDefUnit isExpDefUnit = null;
		ExpIsSpecialization specKind = null;
		Reference specTypeRef = null;
		ArrayView<TemplateParameter> tplParams = null;
		
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			
			typeRef = parseTypeReference_ToMissing().node;
			
			if(lookAhead() == DeeTokens.IDENTIFIER) {
				ProtoDefSymbol defId = parseDefId();
				isExpDefUnit = concludeNode(srOf(lastLexElement(), new StaticIfExpIsDefUnit(defId)));
			}
			
			if(tryConsume(DeeTokens.COLON)) {
				specKind = ExpIsSpecialization.TYPE_SUBTYPE;
				specTypeRef = parseTypeReference_ToMissing().node;
			} else if(tryConsume(DeeTokens.EQUALS)) {
				specKind = determineIsExpArchetype();
				
				if(specKind != null ) {
					consumeLookAhead();
				} else {
					specKind = ExpIsSpecialization.TYPE_EXACT;
					
					specTypeRef = parseTypeReference().node;
					if(specTypeRef == null) {
						specTypeRef = parseMissingTypeReference(RULE_IS_TYPE_SPEC);						
					}
				}
			}
			
			if((specKind == ExpIsSpecialization.TYPE_SUBTYPE || specKind == ExpIsSpecialization.TYPE_EXACT) 
				&& tryConsume(DeeTokens.COMMA)) {
				tplParams = thisParser().parseTemplateParametersList();
			}
			
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		}
		
		if(isExpDefUnit != null || tplParams != null) {
			return parse.resultConclude(new StaticIfExpIs(typeRef, isExpDefUnit, specKind, specTypeRef, tplParams));
		} else {
			return parse.resultConclude(new ExpIs(typeRef, specKind, specTypeRef));
		}
	}
	
	protected ExpIsSpecialization determineIsExpArchetype() {
		if(isTypeModifier(lookAhead()) && 
			(lookAhead(1) == DeeTokens.OPEN_PARENS || canParseTypeReferenceStart(lookAhead(1))))
			return null;
		
		switch (lookAhead()) {
		case KW_STRUCT: return ExpIsSpecialization.STRUCT;
		case KW_UNION: return ExpIsSpecialization.UNION;
		case KW_CLASS: return ExpIsSpecialization.CLASS;
		case KW_INTERFACE: return ExpIsSpecialization.INTERFACE;
		case KW_ENUM: return ExpIsSpecialization.ENUM;
		case KW_FUNCTION: return ExpIsSpecialization.FUNCTION;
		case KW_TYPEDEF: return ExpIsSpecialization.TYPEDEF;
		case KW_DELEGATE: return ExpIsSpecialization.DELEGATE;
		case KW_SUPER: return ExpIsSpecialization.SUPER;
		case KW_CONST:
			return ExpIsSpecialization.CONST;
		case KW_IMMUTABLE: 
			return ExpIsSpecialization.IMMUTABLE;
		case KW_INOUT: 
			return ExpIsSpecialization.INOUT;
		case KW_SHARED: 
			return ExpIsSpecialization.SHARED;
		
		case KW_RETURN: return ExpIsSpecialization.RETURN;
		case IDENTIFIER: 
			if(lookAheadElement().getSourceValue().equals("__parameters"))
				return ExpIsSpecialization.__PARAMETERS;
		default:
			return null;
		}
	}
	
	public NodeResult<ExpTraits> parseTraitsExpression() {
		if(!tryConsume(DeeTokens.KW___TRAITS))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Symbol traitsId = null;
		NodeListView<Resolvable> args = null;
		
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			
			traitsId = parseTraitsId();
			
			if(parse.consumeExpected(DeeTokens.COMMA)) { 
				SimpleListParseHelper<Resolvable> elementListParse = new TypeOrExpArgumentListSimpleParse();
				elementListParse.parseSimpleList(DeeTokens.COMMA, true, true);
				args = elementListParse.members;
			}
			
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		}
		
		return parse.resultConclude(new ExpTraits(traitsId, args));
	}
	
	public Symbol parseTraitsId() {
		BaseLexElement traitsId = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
		ParserError error = DeeTokenSemantics.checkTraitsId(traitsId);
		return conclude(error, srOf(traitsId, new Symbol(traitsId.getSourceValue())));
	}
	
}