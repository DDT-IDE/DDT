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

import static dtool.util.NewUtils.assertCast;
import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.ArrayList;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.ASTChildrenVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTSemantics;
import dtool.ast.SourceRange;
import dtool.ast.expressions.ExpArrayLength;
import dtool.ast.expressions.ExpAssert;
import dtool.ast.expressions.ExpCall;
import dtool.ast.expressions.ExpCast;
import dtool.ast.expressions.ExpCastQual;
import dtool.ast.expressions.ExpCastQual.CastQualifiers;
import dtool.ast.expressions.ExpConditional;
import dtool.ast.expressions.ExpImportString;
import dtool.ast.expressions.ExpIndex;
import dtool.ast.expressions.ExpInfix;
import dtool.ast.expressions.ExpInfix.InfixOpType;
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
import dtool.ast.expressions.ExpNull;
import dtool.ast.expressions.ExpParentheses;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.ast.expressions.ExpPostfixOperator.PostfixOpType;
import dtool.ast.expressions.ExpPrefix;
import dtool.ast.expressions.ExpPrefix.PrefixOpType;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSlice;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.ExpTypeId;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.MissingParenthesesExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.RefTypeModifier;
import dtool.ast.references.RefTypeModifier.TypeModifierKinds;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.RefTypeof;
import dtool.ast.references.Reference;
import dtool.parser.ParserError.ParserErrorTypes;


public abstract class DeeParser_RefOrExp extends AbstractParser {
	
	/* ----------------------------------------------------------------- */
	
	public DeeTokens lookAheadGrouped() {
		return lookAheadToken().type.getGroupingToken();
	}
	
	public String idTokenToString(LexElement id) {
		return id.isMissingElement() ? null : id.token.source;
	}
	
	/* --------------------  reference parsing  --------------------- */
	
	public static final ParseRuleDescription RULE_REFERENCE = new ParseRuleDescription("Reference");
	public static final ParseRuleDescription RULE_TPL_SINGLE_ARG = new ParseRuleDescription("TemplateSingleArgument");
	
	public NodeResult<Reference> parseTypeReference() {
		return parseTypeReference_do(false);
	}
	
	public NodeResult<Reference> parseTypeReference_ToMissing(boolean reportMissingError) {
		return nullRefToMissing(reportMissingError, parseTypeReference());
	}
	public NodeResult<Reference> parseTypeReference_ToMissing() {
		return nullRefToMissing(true, parseTypeReference());
	}
	
	public NodeResult<Reference> nullRefToMissing(boolean reportMissingError, NodeResult<Reference> refResult) {
		return refResult != null && refResult.node != null ? 
			refResult : 
			nodeResult(false, createMissingTypeReference(reportMissingError));
	}
	
	public Reference createMissingTypeReference(boolean reportMissingError) {
		if(reportMissingError) {
			reportErrorExpectedRule(RULE_REFERENCE);
		}
		LexElement id = createExpectedToken(DeeTokens.IDENTIFIER);
		return connect(new RefIdentifier(idTokenToString(id), sr(id.token)));
	}
	
	protected NodeResult<Reference> parseTypeReference_do(boolean parsingExp) {
		NodeResult<? extends Reference> refParseResult;
		
		if(lookAheadGrouped() == DeeTokens.PRIMITIVE_KW) {
			return parseReference_ReferenceStart(matchRefPrimitive(lookAhead()), parsingExp);
		}
		switch (lookAhead()) {
		case DOT: refParseResult = parseRefModuleQualified_do(); break;
		case IDENTIFIER: return parseReference_ReferenceStart(parseRefIdentifier(), parsingExp);
		
		case KW_TYPEOF: refParseResult = parseRefTypeof_do(); break;
		
		case KW_CONST: refParseResult = matchRefTypeModifier_do(TypeModifierKinds.CONST); break;
		case KW_IMMUTABLE: refParseResult = matchRefTypeModifier_do(TypeModifierKinds.IMMUTABLE); break;
		case KW_SHARED: refParseResult = matchRefTypeModifier_do(TypeModifierKinds.SHARED); break;
		case KW_INOUT: refParseResult = matchRefTypeModifier_do(TypeModifierKinds.INOUT); break;
		default:
			return nullResult();
		}
		
		if(refParseResult.ruleBroken) 
			return refParseResult.<Reference>upcastParam();
		return parseReference_ReferenceStart(refParseResult.node, parsingExp);
	}
	
	protected boolean isTypeModifier(DeeTokens lookAhead) {
		switch (lookAhead) {
		case KW_CONST: case KW_IMMUTABLE: case KW_SHARED: case KW_INOUT: return true;
		default: return false;
		}
	}
	
	protected RefIdentifier parseRefIdentifier() {
		LexElement id = consumeExpectedToken(DeeTokens.IDENTIFIER);
		return connect(
			id != null ? new RefIdentifier(idTokenToString(id), sr(id.token)) : createMissingRefIdentifier());
	}
	
	protected RefIdentifier createMissingRefIdentifier() {
		int nodeStart = lastLexElement().getEndPos();
		LexElement id = createExpectedToken(DeeTokens.IDENTIFIER);
		return new RefIdentifier(idTokenToString(id), srToCursor(nodeStart));
	}
	
	protected RefPrimitive matchRefPrimitive(DeeTokens primitiveType) {
		Token token = consumeLookAhead(primitiveType).token;
		return connect(new RefPrimitive(token, sr(token)));
	}
	
	public RefModuleQualified parseRefModuleQualified() {
		return (RefModuleQualified) parseRefModuleQualified_do().node;
	}
	
	protected NodeResult<RefModuleQualified> parseRefModuleQualified_do() {
		if(!tryConsume(DeeTokens.DOT))
			return nullResult();
		int nodeStart = lastLexElement().getStartPos();
		
		boolean parseBroken = lookAhead() != DeeTokens.IDENTIFIER;
		RefIdentifier id = parseRefIdentifier();
		return connectResult(parseBroken, new RefModuleQualified(id, srToCursor(nodeStart)));
	}
	
	protected NodeResult<RefTypeof> parseRefTypeof_do() {
		if(!tryConsume(DeeTokens.KW_TYPEOF))
			return null;
		int nodeStart = lastLexElement().getStartPos();
		
		Expression exp = null;
		boolean parseBroken = true;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			if(tryConsume(DeeTokens.KW_RETURN)) {
				exp = new RefTypeof.ExpRefReturn(lastLexElement().getSourceRange());
			} else {
				exp = parseExpression_toMissing();
			}
			parseBroken = consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null;
		}
		return connectResult(parseBroken, new RefTypeof(exp, srToCursor(nodeStart)));
	}
	
	protected NodeResult<RefTypeModifier> matchRefTypeModifier_do(TypeModifierKinds modKind) {
		assertTrue(lookAhead().sourceValue.equals(modKind.sourceValue));
		consumeInput();
		int nodeStart = lastLexElement().getStartPos();
		
		Reference ref = null;
		boolean parseBroken = true;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			ref = parseTypeReference_ToMissing(true).getNode_NoBrokenCheck(); 
			parseBroken = consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null;
		}
		return connectResult(parseBroken, new RefTypeModifier(modKind, ref, srToCursor(nodeStart)));
	}
	
	protected NodeResult<Reference> parseReference_ReferenceStart(Reference leftRef, boolean parsingExp) {
		assertNotNull(leftRef);
		return parseReference_ReferenceStart_do(leftRef, parsingExp);
	}
	protected NodeResult<Reference> parseReference_ReferenceStart_do(Reference leftRef, boolean parsingExp) {
		boolean parseBroken = false;
		
		// Star is multiply infix operator, dont parse as pointer ref
		if(lookAhead() == DeeTokens.DOT) {
			if(leftRef instanceof IQualifierNode == false) {
				return nodeResult(leftRef);
			}
			IQualifierNode qualifier = (IQualifierNode) leftRef;
			assertTrue(!RefQualified.isExpressionQualifier(qualifier));
			consumeLookAhead();
			parseBroken = lookAhead() != DeeTokens.IDENTIFIER;
			RefIdentifier qualifiedId = parseRefIdentifier();
			leftRef = connect(new RefQualified(qualifier, qualifiedId, srToCursor(leftRef.getStartPos())));
			
		} else if(lookAhead() == DeeTokens.NOT && isValidTemplateReferenceSyntax(leftRef)){ // template instance
			consumeLookAhead();
			
			ITemplateRefNode tplRef = (ITemplateRefNode) leftRef;
			ArrayList<Resolvable> tplArgs = null;
			Resolvable singleArg = null;
			
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				NodeListParseResult<Resolvable> argList = 
					parseArgumentList(true, DeeTokens.COMMA, DeeTokens.CLOSE_PARENS);
				tplArgs = argList.result;
				parseBroken = argList.ruleBroken;
			} else {
				if(leftRef instanceof RefTemplateInstance) {
					RefTemplateInstance refTplInstance = (RefTemplateInstance) leftRef;
					if(refTplInstance.isSingleArgSyntax()) {
						addError(ParserErrorTypes.NO_CHAINED_TPL_SINGLE_ARG, refTplInstance.getSourceRange(), null);
					}
				}
				
				if(lookAheadGrouped() == DeeTokens.PRIMITIVE_KW) {
					singleArg = matchRefPrimitive(lookAhead());	
				} else if(lookAheadGrouped() == DeeTokens.IDENTIFIER) { 
					singleArg = parseRefIdentifier();
				} else {
					singleArg = parseSimpleLiteral();
					if(singleArg == null) {
						singleArg = createMissingExpression(RULE_TPL_SINGLE_ARG, true); 
					}
				}
			}
			leftRef = connect(new RefTemplateInstance(tplRef, singleArg, arrayView(tplArgs), srToCursor(leftRef)));
			
		} else if(!parsingExp && tryConsume(DeeTokens.STAR)) {
			leftRef = connect(new RefTypePointer(leftRef, srToCursor(leftRef.getStartPos())));
			
		} else if(!parsingExp && tryConsume(DeeTokens.OPEN_BRACKET)) {
			Resolvable resolvable = parseTypeOrExpression(true).node;
			parseBroken = consumeExpectedToken(DeeTokens.CLOSE_BRACKET) == null;
			
			if(resolvable == null) {
				leftRef = connect(new RefTypeDynArray(leftRef, srToCursor(leftRef.getStartPos())));
			} else {
				leftRef = connect(new RefIndexing(leftRef, resolvable, srToCursor(leftRef.getStartPos())));
			}
			
		} else {
			return nodeResult(leftRef);
		}
		if(parseBroken)
			return nodeResult(true, leftRef);
		return parseReference_ReferenceStart(leftRef, parsingExp);
	}
	
	public boolean isValidTemplateReferenceSyntax(Reference leftRef) {
		return leftRef instanceof ITemplateRefNode;
	}
	
	/* --------------------- EXPRESSIONS --------------------- */
	
	public static final ParseRuleDescription RULE_EXPRESSION = new ParseRuleDescription("Expression");
	public static final ParseRuleDescription RULE_TYPE_OR_EXP = new ParseRuleDescription("Reference or Expression");
	
	public static final InfixOpType ANY_OPERATOR = InfixOpType.COMMA;
	
	public NodeResult<Expression> parseExpression() {
		return parseExpression(ANY_OPERATOR);
	}
	
	protected NodeResult<Expression> parseExpression(InfixOpType precedenceLimit) {
		return new ParseRule_TypeOrExp().parse(precedenceLimit).toExpression();
	}
	
	public Expression parseExpression_toMissing() {
		return nullExpToMissing(parseExpression().node);
	}
	public Expression parseExpression_toMissing(InfixOpType precedenceLimit) {
		return nullExpToMissing(parseExpression(precedenceLimit).node);
	}
	
	public NodeResult<Expression> parseAssignExpression() {
		return parseExpression(InfixOpType.ASSIGN);
	}
	
	public Expression parseAssignExpression_toMissing() {
		return nullExpToMissing(parseAssignExpression().node);
	}
	
	public NodeResult<Resolvable> parseExpressionOrType() {
		return parseTypeOrExpression(ANY_OPERATOR, false);
	}
	
	public NodeResult<Resolvable> parseTypeOrExpression(boolean ambiguousToRef) {
		return parseTypeOrExpression(ANY_OPERATOR, ambiguousToRef);
	}
	
	public NodeResult<Resolvable> parseTypeOrAssignExpression(boolean ambiguousToRef) {
		return parseTypeOrExpression(InfixOpType.ASSIGN, ambiguousToRef);
	}
	
	public NodeResult<Resolvable> parseTypeOrExpression(InfixOpType precedenceLimit, boolean ambiguousToRef) {
		return parseTypeOrExpression(precedenceLimit).toFinalResult(ambiguousToRef);
	}
	
	protected TypeOrExpResult parseTypeOrExpression(InfixOpType precedenceLimit) {
		return new ParseRule_TypeOrExp().parse(precedenceLimit);
	}
	
	protected Expression nullExpToMissing(Expression exp) {
		return exp != null ? exp : createMissingExpression(RULE_EXPRESSION, true);
	}
	protected Resolvable nullTypeOrExpToMissing(Resolvable exp) {
		return exp != null ? exp : createMissingExpression(RULE_TYPE_OR_EXP, true);
	}
	
	protected Expression createMissingExpression(ParseRuleDescription expectedRule, boolean consumeIgnoreTokens) {
		return createTypeOrExpMissingExp(TypeOrExpStatus.EXP, expectedRule, consumeIgnoreTokens);
	}
	
	protected MissingExpression createTypeOrExpMissingExp(TypeOrExpStatus mode, boolean consumeIgnoreTokens) {
		return createTypeOrExpMissingExp(mode, RULE_EXPRESSION, consumeIgnoreTokens);
	}
	
	protected MissingExpression createTypeOrExpMissingExp(TypeOrExpStatus mode, ParseRuleDescription expectedRule) {
		return createTypeOrExpMissingExp(mode, expectedRule, false);
	}
	
	public MissingExpression createTypeOrExpMissingExp(TypeOrExpStatus mode, ParseRuleDescription expectedRule,
		boolean consumeIgnoreTokens) {
		int nodeStart = lastLexElement().getEndPos();
		if(consumeIgnoreTokens) {
			consumeIgnoreTokens(null);
		}
		MissingExpression missingExp = new MissingExpression(srToCursor(nodeStart));
		if(expectedRule != null) {
			ParserError error = createErrorOnLastToken(ParserErrorTypes.EXPECTED_RULE, expectedRule.name);
			assertTrue(error.sourceRange.equals(lastNonMissingLexElement().getSourceRange()));
			if(mode == TypeOrExpStatus.EXP) {
				addError(error);
			}
		}
		if(mode == TypeOrExpStatus.EXP) {
			return connect(missingExp);
		} else {
			return connectRefData(mode, lastNonMissingLexElement(), missingExp);
		}
	}
	
	protected void addErrorTypeAsExpValue(Reference reference) {
		addError(ParserErrorTypes.TYPE_USED_AS_EXP_VALUE, reference.getSourceRange(), null);
	}
	
	public boolean isMissing(Expression exp) {
		return exp == null || exp instanceof MissingExpression;
	}
	
	public static enum TypeOrExpStatus { 
		TYPE, 
		TYPE_OR_EXP, 
		TYPE_OR_EXP_WITH_MISSING_RIGHT,
		EXP,
		EXP_WITH_PENDING_TYPE;
		public boolean canBeType() {
			assertTrue(this != EXP_WITH_PENDING_TYPE);
			return this != EXP;
		}
		public boolean canBeExp() {
			assertTrue(this != EXP_WITH_PENDING_TYPE);
			return this != TYPE;
		}
		
		public boolean isExpMode() {
			return this == EXP || this == TypeOrExpStatus.EXP_WITH_PENDING_TYPE;
		}
		
		/** Returns this if it can be an exp without any structural convertions */
		public boolean canBeExpClean() {
			return this != TYPE && this != EXP_WITH_PENDING_TYPE;
		}
	}
	
	protected static class TypeOrExpData {
		public TypeOrExpStatus mode;
		public final LexElement tokenInfo;
		public TypeOrExpData(TypeOrExpStatus mode, LexElement tokenInfo) {
			this.mode = mode;
			this.tokenInfo = tokenInfo;
		}
		public TypeOrExpData(TypeOrExpStatus mode) {
			this(mode, null);
		}
		
		@Override
		public String toString() {
			return mode + (tokenInfo == null ? "" : "[*]");
		}
	}
	
	/* ============================ TypeOrExp ============================ */
	
	// This approach to parsing Type references or Expressions is quite complicated and messy,
	// and probably not worth it (even thought it parses linearly)
	// Better would be an approach based on rule deciders and backtracking. 
	
	public static final Expression DUMMY_EXP = new MissingExpression(null) {{ setData(ASTSemantics.PARSED_STATUS);}};
	
protected class ParseRule_TypeOrExp {
	
	public boolean breakRule;
	public TypeOrExpStatus mode;
	public boolean isRightExpMissing;
	public boolean convertPendingNodeThenContinueExpParse;
	
	public ParseRule_TypeOrExp() {
		breakRule = false;
		mode = null;
		isRightExpMissing = false;
		convertPendingNodeThenContinueExpParse = false;
	}
	
	public void updateTypeOrExpMode(TypeOrExpStatus newMode) {
		assertNotNull(newMode);
		assertTrue(newMode != TypeOrExpStatus.TYPE_OR_EXP_WITH_MISSING_RIGHT); // There is specific function for this
		if(newMode == TypeOrExpStatus.TYPE_OR_EXP) {
			assertTrue(mode == null);
		} else if(mode == TypeOrExpStatus.TYPE_OR_EXP_WITH_MISSING_RIGHT) {
			assertTrue(newMode == TypeOrExpStatus.TYPE);
		} else if(mode == TypeOrExpStatus.EXP_WITH_PENDING_TYPE) {
			assertFail();
		} 
		
		mode = newMode;
	}
	
	public boolean shouldReturnToParseRuleTopLevel(Expression expSoFar) {
		boolean result = breakRule || convertPendingNodeThenContinueExpParse;
		assertTrue(isEnabled() == !result);
		assertTrue((mode != null) || (expSoFar == null));
		return result || expSoFar == null;
	}
	
	public void setParseBroken(boolean parseBroken) {
		this.breakRule = parseBroken;
		if(breakRule) {
			setEnabled(false);
		}
	}
	
	public Expression enterTypeOrExpMissingRightExpMode() {
		setParseBroken(true);
		isRightExpMissing = true;
		if(mode == TypeOrExpStatus.TYPE_OR_EXP) {
			mode = TypeOrExpStatus.TYPE_OR_EXP_WITH_MISSING_RIGHT;
		}
		
		return createTypeOrExpMissingExp(mode, RULE_EXPRESSION);
	}
	
	public void setConvertPendingNodeThenContinueExpMode() {
		convertPendingNodeThenContinueExpParse = true;
		setEnabled(false);
	}
	
	protected Expression expConnect(Expression exp) {
		updateTypeOrExpMode(TypeOrExpStatus.EXP);
		return typeOrExpConnect(exp);
	}
	protected Expression expConnect(boolean parseBroken, Expression exp) {
		setParseBroken(parseBroken);
		return expConnect(exp);
	}
	protected Expression expConnect(NodeResult<? extends Expression> result) {
		return expConnect(result.ruleBroken, result.node);
	}
	
	protected Expression typeConnect(Expression exp) {
		updateTypeOrExpMode(TypeOrExpStatus.TYPE);
		return typeOrExpConnect(exp);
	}
	
	protected Expression typeOrExpConnect(Expression exp, boolean parseBroken) {
		setParseBroken(parseBroken);
		return typeOrExpConnect(exp);
	}
	
	protected Expression typeOrExpConnect(Expression exp) {
		return typeOrExpConnect(null, exp);
	}
	protected Expression typeOrExpConnect(LexElement afterStarOp, Expression exp) {
		if(mode == TypeOrExpStatus.EXP) {
			if(!exp.isParsedStatus()) {
				exp = connect(exp);
			}
		} else {
			// This means the node must go through conversion process
			exp.setData(new TypeOrExpData(mode, afterStarOp));  
		}
		return exp;
	}
	
	public Expression resultConvertTypeThenContinueExpParse(Expression exp) {
		setConvertPendingNodeThenContinueExpMode();
		updateTypeOrExpMode(TypeOrExpStatus.TYPE);
		return exp;
	}
	
	public TypeOrExpResult parse(InfixOpType precedenceLimit) {
		Expression exp = parseTypeOrExpression_start(precedenceLimit, true);
		
		if(!breakRule && convertPendingNodeThenContinueExpParse) {
			setEnabled(true);
			convertPendingNodeThenContinueExpParse = false;
			exp = convertTypeOrExpToExpression(exp);
			mode = TypeOrExpStatus.EXP;
			exp = parseTypeOrExpression_fromUnary(precedenceLimit, exp);
			// Note we dont need to check convertPendingRefThenContinueExpParse again as it is false,
			// because if a new type ref comes up, it will be under a new ParseRule_TypeOrExp instance
		}
		if(breakRule) {
			// breakRule overrides convertPendingRefThenContinueExpParse
			setEnabled(true);
		} 
		assertTrue(mode != TypeOrExpStatus.EXP_WITH_PENDING_TYPE);
		
		if(!breakRule && mode != null) {
			// Check we actually parsed everything we should have
			if(mode.canBeType()) {
				boolean needsExpContext = precedenceLimit.precedence > InfixOpType.MUL.precedence;
				assertTrue(parseReference_ReferenceStart_do(null, needsExpContext).getNode_NoBrokenCheck() == null);
			}
			if(mode.canBeExp()) {
				assertTrue(parsePostfixExpression(DUMMY_EXP) == DUMMY_EXP);
				assertTrue(parseInfixOperators(precedenceLimit, DUMMY_EXP) == DUMMY_EXP);
			}
		}
		if(isRightExpMissing && mode != TypeOrExpStatus.EXP) {
			breakRule = false;
		}
		return new TypeOrExpResult(mode, exp, breakRule);
	}
	
	protected Expression parseTypeOrExpression_start(InfixOpType precedenceLimit, boolean isStart) {
		Expression prefixExp = parsePrefixExpression(isStart);
		if(shouldReturnToParseRuleTopLevel(prefixExp)) {
			return prefixExp;
		}
		
		return parseTypeOrExpression_fromUnary(precedenceLimit, prefixExp);
	}
	
	public Expression parseTypeOrExpression_fromUnary(InfixOpType precedenceLimit, Expression prefixExp) {
		Expression unaryExp = parsePostfixExpression(prefixExp);
		if(shouldReturnToParseRuleTopLevel(unaryExp)) {
			return unaryExp;
		}
		
		return parseInfixOperators(precedenceLimit, unaryExp);
	}
	
	protected Expression parseUnaryExpression(boolean isTypeOrExpStart) {
		Expression prefixExp = parsePrefixExpression(isTypeOrExpStart);
		if(shouldReturnToParseRuleTopLevel(prefixExp)) {
			return prefixExp;
		}
		
		return parsePostfixExpression(prefixExp);
	}
	
	protected Expression parsePrefixExpression(boolean isTypeOrExpStart) {
		if(isTypeOrExpStart) {
			assertTrue(mode == null);
		}
		Expression simpleLiteral = parseSimpleLiteral();
		if(simpleLiteral != null) {
			return expConnect(simpleLiteral);
		}
		
		switch (lookAheadGrouped()) {
		default:
			NodeResult<Reference> typeRefResult = parseTypeReference_do(true);
			boolean ruleBroken = typeRefResult.ruleBroken;
			Reference ref = typeRefResult.getNode();
			if(ref == null) {
				return null; // TODO: option to return missing?
			}
			
			// Initialize mode
			if(isTypeOrExpStart) { 
				updateTypeOrExpMode(TypeOrExpStatus.TYPE_OR_EXP);
			} else {
				updateTypeOrExpMode(TypeOrExpStatus.EXP);
			}
			if(parsesAsTypeRef(ref)) {
				if(mode.canBeType()) {
					updateTypeOrExpMode(TypeOrExpStatus.TYPE); // Begginning of Type ref
				} else {
					addErrorTypeAsExpValue(ref);
				}
			}
			return typeOrExpConnect(new ExpReference(ref), ruleBroken);
		case AND:
		case INCREMENT:
		case DECREMENT:
		case STAR:
		case MINUS:
		case PLUS:
		case NOT:
		case CONCAT:
		case KW_DELETE: {
			Token prefixExpToken = consumeLookAhead();
			PrefixOpType prefixOpType = PrefixOpType.tokenToPrefixOpType(prefixExpToken.type);
			LexElement opAheadInfo = lookAheadElement();
			
			if(prefixExpToken.type != DeeTokens.STAR || isTypeOrExpStart || !mode.canBeType()) {
				updateTypeOrExpMode(TypeOrExpStatus.EXP);
			}
			Expression exp = parseUnaryExpression(false);
			if(exp == null) {
				exp = enterTypeOrExpMissingRightExpMode();
			}
			
			return typeOrExpConnect(opAheadInfo, new ExpPrefix(prefixOpType, exp, srToCursor(prefixExpToken)));
		}
		case OPEN_PARENS:
			return expConnect(parseParenthesesExp());
		case OPEN_BRACKET:
			if(isTypeOrExpStart) {
				updateTypeOrExpMode(TypeOrExpStatus.EXP);
			}
			return parseBracketList(null);
			//TODO test for brokenness for rest of rules
		case KW_ASSERT:
			return expConnect(parseAssertExpression());
		case KW_MIXIN:
			return expConnect(parseMixinExpression());
		case KW_IMPORT:
			return expConnect(parseImportExpression());
		case KW_TYPEID:
			return expConnect(parseTypeIdExpression());
		case KW_NEW:
			return expConnect(parseNewExpression());
		case KW_CAST:
			return expConnect(parseCastExpression());
		}
	}
	
	protected Expression parsePostfixExpression(Expression exp) {
		assertTrue(mode != TypeOrExpStatus.TYPE_OR_EXP_WITH_MISSING_RIGHT);
		
		switch (lookAheadGrouped()) {
		case DECREMENT:
		case INCREMENT: {
			if(mode == TypeOrExpStatus.TYPE)
				return resultConvertTypeThenContinueExpParse(exp);
			exp = convertTypeOrExpToExpression(exp);
			exp = expConnect(matchPostfixOpExpression(exp));
			return parsePostfixExpression(exp);
		}
		case OPEN_PARENS: {
			if(mode == TypeOrExpStatus.TYPE)
				return resultConvertTypeThenContinueExpParse(exp);
			exp = convertTypeOrExpToExpression(exp);
			exp = expConnect(matchCallExpression(exp));
			return parsePostfixExpression(exp);
		}
		case POW: {
			if(mode == TypeOrExpStatus.TYPE)
				return resultConvertTypeThenContinueExpParse(exp);
			exp = convertTypeOrExpToExpression(exp);
			updateTypeOrExpMode(TypeOrExpStatus.EXP);
			return new ParseRule_InfixOperatorExp(exp).parseInfixOperator(InfixOpType.POW);
		}
		case OPEN_BRACKET: {
			exp = parseBracketList(exp);
			if(shouldReturnToParseRuleTopLevel(exp)) {
				return exp;
			}
			return parsePostfixExpression(exp);
		}
		case DOT: {
			IQualifierNode qualifier = resolvableToExp(false, convertTypeOrExp(null, exp, false));
			exp = null;
			if(qualifier instanceof ExpReference) {
				ExpReference expReference = (ExpReference) qualifier;
				if(expReference.ref instanceof RefQualified) {
					assertTrue(((RefQualified) expReference.ref).isExpressionQualifier);
				} else {
					assertTrue(!(expReference.ref instanceof IQualifierNode)); 
					// ...otherwise refqualified would have been parsed already
					addError(ParserErrorTypes.INVALID_QUALIFIER, expReference.getSourceRange(), null);
				}
			}
			consumeLookAhead();
			RefIdentifier qualifiedId = parseRefIdentifier();
			Reference ref = connect(new RefQualified(qualifier, qualifiedId, srToCursor(qualifier.getStartPos())));
			updateTypeOrExpMode(TypeOrExpStatus.EXP);
			return parsePostfixExpression(connect(new ExpReference(ref)));
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
		
		Expression exp = new ParseRule_InfixOperatorExp(leftExp).parseInfixOperator(infixOpAhead);
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
	
	protected abstract class TypeOrExpRule_Common {
		
		public boolean setExpMode() {
			if(mode.canBeType()) {
				updateTypeOrExpMode(TypeOrExpStatus.EXP);
				modeUpdatedToExp();
				return true;
			}
			return false;
		}
		
		public abstract void modeUpdatedToExp();
		
	}
	
	protected class ParseRule_InfixOperatorExp extends TypeOrExpRule_Common {
		
		protected Expression leftExp;
		protected Expression rightExp = null;
		
		protected ParseRule_InfixOperatorExp(Expression leftExp) {
			this.leftExp = leftExp;
			assertNotNull(leftExp);
			assertTrue(leftExp.isParsedStatus() || getTypeOrExpStatus(leftExp).canBeType());
		}
		
		public Expression parseInfixOperator(final InfixOpType opType) {
			
			consumeLookAhead();
			if(opType == InfixOpType.NOT_IS || opType == InfixOpType.NOT_IN) {
				consumeLookAhead(); // consume second token
			}
			
			LexElement afterStarOp = null;
			
			if(opType != InfixOpType.MUL) {
				setExpMode();
				
				checkValidAssociativity(leftExp, opType);
			} else {
				assertTrue(lastLexElement().getType() == DeeTokens.STAR);
				afterStarOp = lookAheadElement();
			}
			
			Expression middleExp = null;
			
			parsing: {
				if(opType == InfixOpType.CONDITIONAL) {
					middleExp = nullExpToMissing(parseExpression().node);
					
					if(consumeExpectedToken(DeeTokens.COLON) == null) {
						setParseBroken(true);
						break parsing;
					}
				}
				
				InfixOpType rightExpPrecedence = getPrecedenceForInfixOpRightExp(opType);
				
				if(mode == TypeOrExpStatus.EXP) {
					NodeResult<Expression> expResult = parseExpression(rightExpPrecedence);
					setParseBroken(expResult.ruleBroken);
					rightExp = expResult.getNode();
				} else {
					rightExp = parseTypeOrExpression_start(rightExpPrecedence, false);
				}
				
				if(isMissing(rightExp)) {
					rightExp = enterTypeOrExpMissingRightExpMode();
				} else {
					if(mode == TypeOrExpStatus.EXP) {
						modeUpdatedToExp();
					}
					
					checkValidAssociativity(rightExp, opType);
				}
			}
			
			if(opType == InfixOpType.CONDITIONAL) {
				return expConnect(new ExpConditional(leftExp, middleExp, rightExp, srToCursor(leftExp)));
			}
			
			assertTrue(!(leftExp.isParsedStatus() && mode == TypeOrExpStatus.EXP_WITH_PENDING_TYPE));
			return typeOrExpConnect(afterStarOp, init(srToCursor(leftExp), new ExpInfix(leftExp, opType, rightExp)));
		}
		
		protected void checkValidAssociativity(Expression exp, InfixOpType op) {
			// Check for some syntax situations which are technically not allowed by the grammar:
			switch (op.category) {
			case OR: case XOR: case AND: case EQUALS:
				if(exp instanceof ExpInfix) {
					if(((ExpInfix) exp).kind.category == InfixOpType.EQUALS) {
						addError(ParserErrorTypes.EXP_MUST_HAVE_PARENTHESES, exp.getSourceRange(), op.sourceValue);
					}
				}
			default: break;
			}
		}
		
		@Override
		public void modeUpdatedToExp() {
			if(rightExp == null || rightExp.isParsedStatus()) {
				leftExp = convertTypeOrExpToExpression(leftExp); 
			}
		}
		
	}
	
	protected Expression parseArrayLiteral() {
		return new ParseRule_BracketList(null).doParse();
	}
	
	protected Expression parseBracketList(Expression calleeExp) {
		return new ParseRule_BracketList(calleeExp).doParse();
	}
	
	protected class ParseRule_BracketList extends TypeOrExpRule_Common {
		
		public Expression calleeExp;
		public Expression firstArg;
		
		public ParseRule_BracketList(Expression calleeExp) {
			this.calleeExp = calleeExp;
			assertTrue(calleeExp == null || calleeExp.isParsedStatus() || getTypeOrExpStatus(calleeExp).canBeType());
		}
		
		protected Expression doParse() {
			if(tryConsume(DeeTokens.OPEN_BRACKET) == false)
				return null;
			int nodeStart = lastLexElement().getStartPos();
			
			final boolean isExpIndexing = calleeExp != null;
			final boolean couldBeExpIndexing = mode == TypeOrExpStatus.TYPE && false; // This feature is disabled
			final DeeTokens secondLA = isExpIndexing ? DeeTokens.DOUBLE_DOT : DeeTokens.COLON;
			
			ArrayList<Expression> elements = new ArrayList<Expression>();
			ArrayList<MapArrayLiteralKeyValue> mapElements = null;
			
			boolean firstElement = true;
			
			while(true) {
				Expression exp1;
				Expression exp2 = null;
				
				if(firstElement) {
					if(mode.canBeType()) {
						TypeOrExpResult firstExpToE = new ParseRule_TypeOrExp().parse(InfixOpType.ASSIGN);
						if(firstExpToE.isModePreferablyType()) {
							updateTypeOrExpMode(TypeOrExpStatus.TYPE);
						}
						firstArg = firstExpToE.exp;
					} else {
						firstArg = parseAssignExpression().node;
					}
					
					if(isMissing(firstArg) && lookAhead() != DeeTokens.COMMA && lookAhead() != secondLA) {
						if(isExpIndexing) {
							setParseBroken(consumeExpectedToken(DeeTokens.CLOSE_BRACKET) == null);
							return typeOrExpConnect(new ExpSlice(calleeExp, srToCursor(calleeExp)));
						}
						break; // Empty array literal
					}
					
					firstArg = nullExpToMissing(firstArg);
					
					if(lookAhead() == DeeTokens.COMMA) {
						setExpMode();
					} else if(!isExpIndexing && tryConsume(DeeTokens.COLON)) {
						setExpMode();
						assertTrue(calleeExp == null);
						exp2 = parseAssignExpression_toMissing();
						mapElements = new ArrayList<MapArrayLiteralKeyValue>();
					} else if((isExpIndexing || couldBeExpIndexing)&& tryConsume(DeeTokens.DOUBLE_DOT)) {
						setExpMode();
						exp2 = parseAssignExpression_toMissing();
						
						setParseBroken(consumeExpectedToken(DeeTokens.CLOSE_BRACKET) == null);
						
						if(!isExpIndexing) { // Small trick to improve parsing
							calleeExp = createTypeOrExpMissingExp(TypeOrExpStatus.TYPE, false);
							calleeExp.setSourcePosition(nodeStart, nodeStart); // range won't matter in the end.
						}
						
						return typeOrExpBracketList(
							init(srToCursor(calleeExp), new ExpSlice(calleeExp, firstArg, exp2)));
					}
					exp1 = firstArg;
				} else {
					exp1 = parseAssignExpression_toMissing();
					
					if(mapElements != null ) {
						assertTrue(mode == TypeOrExpStatus.EXP);
						if(consumeExpectedToken(DeeTokens.COLON) != null) {
							exp2 = parseAssignExpression_toMissing();
						}
					}
				}
				firstElement = false;
				
				if(mapElements == null ) {
					elements.add(exp1);
				} else {
					mapElements.add(connect(new MapArrayLiteralKeyValue(exp1, exp2, srToCursor(exp1.getStartPos()))));
				}
				
				if(tryConsume(DeeTokens.COMMA)) {
					continue;
				}
				break;
			}
			
			setParseBroken(consumeExpectedToken(DeeTokens.CLOSE_BRACKET) == null);
			
			if(calleeExp == null) {
				if(mapElements != null ) {
					return expConnect(new ExpLiteralMapArray(arrayView(mapElements), srToCursor(nodeStart)));
				} else {
					return typeOrExpBracketList(new ExpLiteralArray(arrayView(elements), srToCursor(nodeStart)));
				}
			}
			return typeOrExpBracketList(init(srToCursor(calleeExp), new ExpIndex(calleeExp, arrayView(elements))));
		}
		
		@Override
		public void modeUpdatedToExp() {
			if(calleeExp != null && getTypeOrExpStatus(calleeExp) != TypeOrExpStatus.TYPE) {
				calleeExp = convertTypeOrExpToExpression(calleeExp);
			}
			firstArg = convertTypeOrExpToExpression(firstArg);
		}
		
		protected Expression typeOrExpBracketList(Expression exp) {
			if(mode == TypeOrExpStatus.EXP && getTypeOrExpStatus(calleeExp) == TypeOrExpStatus.TYPE) {
				updateTypeOrExpMode(TypeOrExpStatus.EXP_WITH_PENDING_TYPE);
				setConvertPendingNodeThenContinueExpMode();
			}
			return typeOrExpConnect(exp);
		}
		
	}
	
} /* ---------------- ParseRule_TypeOrExp END----------------*/
	
	/* ---------------- ParseRule_TypeOrExp utils: ----------------*/
	
	/** Returns true if the given ref can only be a reference to a type (due to the parsed grammar rules). */
	protected static boolean parsesAsTypeRef(Reference ref) {
		switch (ref.getNodeType()) {
		case REF_IDENTIFIER:
		case REF_QUALIFIED:
		case REF_MODULE_QUALIFIED:
		case REF_TEMPLATE_INSTANCE:
			return false;
		case REF_PRIMITIVE:
		case REF_TYPEOF:
		case REF_MODIFIER:
			return true;
			
		case REF_TYPE_DYN_ARRAY:
		case REF_TYPE_POINTER:
		case REF_INDEXING:
			throw assertFail(); // This method should not be used with these kinds of refs
		default:
			throw assertFail();
		}
	}
	
	public static <T extends ASTNeoNode> T connectRefData(TypeOrExpStatus mode, LexElement lexInfo, T node) {
		node.setData(new TypeOrExpData(mode, lexInfo));
		return node;
	}
	
	public Expression wrapReferenceForTypeOrExpParse(Reference ref) {
		return connectRefData(TypeOrExpStatus.TYPE, null, new ExpReference(ref));
	}
	
	protected static TypeOrExpStatus getTypeOrExpStatus(Expression exp) {
		if(exp == null) {
			return null;
		}
		if(exp.getData() instanceof TypeOrExpData) {
			return ((TypeOrExpData) exp.getData()).mode;
		}
		return TypeOrExpStatus.EXP;
	}
	
	protected class TypeOrExpResult {
		
		public final boolean definiteRuleBroken; 
		public final TypeOrExpStatus mode;
		private final Expression exp;
		
		public TypeOrExpResult(TypeOrExpStatus mode, Expression exp) {
			this(mode, exp, false);
		}
		
		public boolean isModePreferablyType() {
			return mode == TypeOrExpStatus.TYPE || (mode == TypeOrExpStatus.TYPE_OR_EXP_WITH_MISSING_RIGHT);
		}
		
		public TypeOrExpResult(TypeOrExpStatus mode, Expression exp, boolean ruleBroken) {
			this.definiteRuleBroken = ruleBroken;
			this.mode = exp == null ? null : mode;
			assertTrue(mode != TypeOrExpStatus.EXP_WITH_PENDING_TYPE);
			this.exp = exp;
			assertTrue(mode != null || exp == null);
			assertTrue(getTypeOrExpStatus(exp) == mode);
		}
		
		public boolean isNull() {
			return mode == null;
		}
		
		public NodeResult<Reference> toReference() {
			assertTrue(mode != TypeOrExpStatus.EXP);
			return nodeResult(definiteRuleBroken, convertTypeOrExpToReference(exp));
		}
		
		public NodeResult<Expression> toExpression() {
			return expResult(convertTypeOrExpToExpression(exp));
		}

		protected <T extends ASTNeoNode> NodeResult<T> expResult(T result) {
			boolean additionalBreaks = mode == TypeOrExpStatus.TYPE_OR_EXP_WITH_MISSING_RIGHT
				|| (result instanceof ExpReference && ((ExpReference) result).ref instanceof RefTypePointer);
			return nodeResult(definiteRuleBroken || additionalBreaks, result);
		}
		
		public NodeResult<Resolvable> toFinalResult(boolean ambiguousToRef) {
			boolean toRef = mode == TypeOrExpStatus.TYPE || (mode != null && mode.canBeType() && ambiguousToRef);
			Resolvable result = convertTypeOrExp(null, exp, toRef);
			if(result instanceof Expression) {
				return expResult(result);
			}
			return nodeResult(definiteRuleBroken, result);
		}
	}
	
	protected Expression convertTypeOrExpToExpression(Expression exp) {
		return resolvableToExp(true, convertTypeOrExp(null, exp, false));
	}
	
	protected Expression resolvableToExp(boolean reportError, Resolvable resolvable) {
		if(resolvable instanceof Reference) {
			Reference reference = (Reference) resolvable;
			if(reportError) {
				addErrorTypeAsExpValue(reference);
			}
			return connect(new ExpReference(reference));
		}
		return (Expression) resolvable;
	}
	
	protected Reference convertTypeOrExpToReference(Expression exp) {
		assertTrue(exp == null || getTypeOrExpStatus(exp).canBeType());
		return assertCast(convertTypeOrExp(null, exp, true), Reference.class);
	}
	
	protected Reference convertTypeOrExpToReference(Reference refOnTheLeft, Expression exp) {
		assertTrue(!getTypeOrExpStatus(exp).isExpMode());
		return assertCast(convertTypeOrExp(refOnTheLeft, exp, true), Reference.class);
	}
	
	protected Resolvable convertTypeOrExp(Reference refOnTheLeft, Expression exp, final boolean toRef) {
		if(exp == null) {
			return refOnTheLeft;
		}
		if(exp.isParsedStatus()) {
			assertTrue(refOnTheLeft == null);
			return exp;
		}
		
		boolean convertToTypeRef = getTypeOrExpStatus(exp) == TypeOrExpStatus.TYPE
			|| (!getTypeOrExpStatus(exp).isExpMode() && toRef);
		
		boolean isCleanExpConvertion = getTypeOrExpStatus(exp) == TypeOrExpStatus.EXP 
			|| (getTypeOrExpStatus(exp).canBeExpClean() && !toRef);
		
		switch (exp.getNodeType()) {
		case MISSING_EXPRESSION: {
			SourceRange errorSourceRange = exp.getData(TypeOrExpData.class).tokenInfo.getSourceRange();
			if(convertToTypeRef) {
				if(refOnTheLeft != null) {
					return refOnTheLeft;
				} else {
					addError(ParserErrorTypes.EXPECTED_RULE, errorSourceRange, RULE_REFERENCE.name);
					return connect(new RefIdentifier(null, exp.getSourceRange()));  
				}
			}
			
			addError(createError(ParserErrorTypes.EXPECTED_RULE, errorSourceRange, RULE_EXPRESSION.name));
			break;
		}
		case EXP_REFERENCE: {
			assertTrue(refOnTheLeft == null);
			if(!toRef) {
				if(getTypeOrExpStatus(exp) == TypeOrExpStatus.TYPE) {
					addErrorTypeAsExpValue(((ExpReference) exp).ref);
				}
				break;
			}
			Reference ref = ((ExpReference) exp).ref;
			ref.detachFromParent();
			return ref;
		}
		case EXP_INFIX: {
			ExpInfix expInfix = (ExpInfix) exp;
			if(isCleanExpConvertion) {
				convertTypeOrExpToExpression_noChange(expInfix.leftExp);
				convertTypeOrExpToExpression_noChange(expInfix.rightExp);
				break;
			}
			if(!convertToTypeRef) {
				assertTrue(getTypeOrExpStatus(expInfix.leftExp).canBeType());
				assertTrue(getTypeOrExpStatus(expInfix.rightExp) == TypeOrExpStatus.EXP_WITH_PENDING_TYPE);
			}
			assertTrue(expInfix.kind == InfixOpType.MUL);
			
			LexElement sourceRangeInfo = exp.getData(TypeOrExpData.class).tokenInfo;
			refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, detachParent(expInfix.leftExp));
			
			SourceRange sr = srNodeStart(refOnTheLeft, sourceRangeInfo.getFullRangeStartPos());
			refOnTheLeft = connect(new RefTypePointer(refOnTheLeft, sr));
			
			return convertTypeOrExp(refOnTheLeft, expInfix.rightExp, toRef);
		}
		case EXP_PREFIX: {
			ExpPrefix expPrefix = (ExpPrefix) exp;
			assertTrue(expPrefix.kind == PrefixOpType.REFERENCE);
			if(isCleanExpConvertion) {
				convertTypeOrExpToExpression_noChange(expPrefix.exp);
				break;
			}
			assertTrue(refOnTheLeft != null);
			
			if(!convertToTypeRef) {
				assertTrue(getTypeOrExpStatus(expPrefix.exp) == TypeOrExpStatus.EXP_WITH_PENDING_TYPE);
			}
			LexElement afterStarToken = assertNotNull_(expPrefix.getData(TypeOrExpData.class).tokenInfo);
			SourceRange sr = srNodeStart(refOnTheLeft, afterStarToken.getFullRangeStartPos());
			
			refOnTheLeft = connect(sr, new RefTypePointer(refOnTheLeft, null));
			return convertTypeOrExp(refOnTheLeft, expPrefix.exp, toRef);
		}
		case EXP_SLICE: {
			ExpSlice expSlice = (ExpSlice) exp;
			if(isCleanExpConvertion) {
				assertTrue(expSlice.from == null && expSlice.to == null);
				convertTypeOrExpToExpression_noChange(expSlice.slicee);
				break;
			}
			if(convertToTypeRef) {
				assertTrue(expSlice.from == null && expSlice.to == null);
				
				refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, detachParent(expSlice.slicee));
				return connect(new RefTypeDynArray(refOnTheLeft, srNodeStart(refOnTheLeft, expSlice.getEndPos())));
			} else {
				refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, detachParent(expSlice.slicee));
				Expression expSlicee = resolvableToExp(true, refOnTheLeft);
				return connect(srNodeStart(refOnTheLeft, exp.getEndPos()),
					new ExpSlice(expSlicee, expSlice.from, expSlice.to));
			}
		}
		case EXP_INDEX: {
			ExpIndex expIndex = (ExpIndex) exp;
			if(isCleanExpConvertion) {
				assertTrue(expIndex.args.size() == 1);
				convertTypeOrExpToExpression_noChange(expIndex.indexee);
				convertTypeOrExpToExpression_noChange(expIndex.args.get(0));
				break;
			}
			if(convertToTypeRef) {
				assertTrue(expIndex.args.size() == 1);
				
				refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, detachParent(expIndex.indexee));
				return convertToRefIndexing(refOnTheLeft, expIndex, expIndex.args.get(0));
			} else {
				refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, detachParent(expIndex.indexee));
				Expression expIndexee = resolvableToExp(true, refOnTheLeft);
				return connect(srNodeStart(expIndexee, exp.getEndPos()), new ExpIndex(expIndexee, expIndex.args));
			}
		}
		case EXP_LITERAL_ARRAY: {
			ExpLiteralArray expLiteralArray = (ExpLiteralArray) exp;
			assertTrue(expLiteralArray.elements.size() <= 1);
			assertTrue(getTypeOrExpStatus(exp) != TypeOrExpStatus.EXP_WITH_PENDING_TYPE); // Not possible
			
			if(!convertToTypeRef && getTypeOrExpStatus(exp).canBeExpClean()) {
				if(expLiteralArray.elements.size() == 1) {
					convertTypeOrExpToExpression_noChange(expLiteralArray.elements.get(0));
				}
				break;
			}
			assertTrue(convertToTypeRef);
			assertTrue(refOnTheLeft != null);
			
			if(expLiteralArray.elements.size() == 0) {
				return connect(srNodeStart(refOnTheLeft, exp.getEndPos()), new RefTypeDynArray(refOnTheLeft, null));
			} else {
				detachParent(expLiteralArray.elements.get(0));
				return convertToRefIndexing(refOnTheLeft, expLiteralArray, expLiteralArray.elements.get(0));
			}
		}
		
		default:
			throw assertFail();
		}
		
		assertTrue(refOnTheLeft == null);
		exp.removeData(TypeOrExpData.class);
		return connect(exp);
	}
	
	protected Expression convertTypeOrExpToExpression_noChange(Expression exp) {
		assertTrue(exp == null || exp.isParsedStatus() || getTypeOrExpStatus(exp).canBeExp());
		Expression converted = assertCast(convertTypeOrExp(null, exp, false), Expression.class);
		assertTrue(converted == exp);
		return converted;
	}
	
	public Expression detachParent(Expression exp) {
		exp.getParent().accept(new ASTChildrenVisitor() {
			@Override
			protected void geneticChildrenVisit(ASTNeoNode child) {
				child.detachFromParent();
			}
		});
		return exp;
	}
	
	public Reference convertToRefIndexing(Reference leftRef, Expression exp, Expression indexArgExp) {
		Resolvable indexArg;
		if(indexArgExp.isParsedStatus()) {
			// argument can only be interpreted as expression
			indexArg = indexArgExp;
		} else if(getTypeOrExpStatus(indexArgExp).canBeType()) {
			indexArg = convertTypeOrExpToReference(indexArgExp);
		} else {
			throw assertFail();
		}
		
		return connect(new RefIndexing(leftRef, indexArg, srNodeStart(leftRef, exp.getEndPos())));
	}
	
	/* ---------------- ParseRule_TypeOrExp End of utils ----------------*/
	
	public Expression parseUnaryExpression() {
		Expression exp = new ParseRule_TypeOrExp().parseUnaryExpression(true);
		return convertTypeOrExpToExpression(exp); // TODO: Minor BUG here with TYPE occurences
	}
	
	public Expression parseArrayLiteral() {
		return new ParseRule_TypeOrExp().parseArrayLiteral();
	}
	
	public Expression parseSimpleLiteral() {
		switch (lookAheadGrouped()) {
		case KW_TRUE: case KW_FALSE:
			Token token = consumeLookAhead();
			return connect(new ExpLiteralBool(token.type == DeeTokens.KW_TRUE, srToCursor(lastLexElement())));
		case KW_THIS:
			consumeLookAhead();
			return connect(new ExpThis(srToCursor(lastLexElement())));
		case KW_SUPER:
			consumeLookAhead();
			return connect(new ExpSuper(srToCursor(lastLexElement())));
		case KW_NULL:
			consumeLookAhead();
			return connect(new ExpNull(srToCursor(lastLexElement())));
		case DOLLAR:
			consumeLookAhead();
			return connect(new ExpArrayLength(srToCursor(lastLexElement())));
			
		case KW___LINE__:
			return connect(new ExpLiteralInteger(consumeLookAhead(), srToCursor(lastLexElement())));
		case KW___FILE__:
			return connect(new ExpLiteralString(consumeLookAhead(), srToCursor(lastLexElement())));
		case INTEGER:
			return connect(new ExpLiteralInteger(consumeLookAhead(), srToCursor(lastLexElement())));
		case CHARACTER: 
			return connect(new ExpLiteralChar(consumeLookAhead(), srToCursor(lastLexElement())));
		case FLOAT:
			return connect(new ExpLiteralFloat(consumeLookAhead(), srToCursor(lastLexElement())));
		case STRING:
			return parseStringLiteral();
		default:
			return null;
		}
	}
	
	public Expression parseStringLiteral() {
		ArrayList<Token> stringTokens = new ArrayList<Token>();
		
		while(lookAheadGrouped() == DeeTokens.STRING) {
			Token string = consumeLookAhead();
			stringTokens.add(string);
		}
		Token[] tokenStrings = ArrayUtil.createFrom(stringTokens, Token.class);
		return connect(new ExpLiteralString(tokenStrings, srToCursor(tokenStrings[0])));
	}
	
	protected ExpPostfixOperator matchPostfixOpExpression(Expression exp) {
		Token op = consumeLookAhead();
		return new ExpPostfixOperator(exp, PostfixOpType.tokenToPrefixOpType(op.type), srToCursor(exp));
	}
	
	protected ExpCall matchCallExpression(Expression callee) {
		consumeLookAhead(DeeTokens.OPEN_PARENS);
		
		ArrayList<Expression> args = parseExpArgumentList(DeeTokens.CLOSE_PARENS).result;
		return connect(new ExpCall(callee, arrayView(args), srToCursor(callee)));
	}
	
	protected NodeListParseResult<Expression> parseExpArgumentList(DeeTokens tokenLISTCLOSE) {
		return CoreUtil.blindCast(parseArgumentList(false, DeeTokens.COMMA, tokenLISTCLOSE));
	}
	protected NodeListParseResult<Resolvable> parseArgumentList(boolean parseTypeOrExp, 
		DeeTokens tokenSEPARATOR, DeeTokens tokenLISTCLOSE) {
		
		ArrayList<Resolvable> args = new ArrayList<Resolvable>();
		
		boolean first = true;
		while(true) {
			Resolvable arg = (parseTypeOrExp ? parseTypeOrAssignExpression(true) : parseAssignExpression()).node;
			
			if(first && arg == null && lookAhead() != tokenSEPARATOR) {
				break;
			}
			arg = parseTypeOrExp ? nullTypeOrExpToMissing(arg) : nullExpToMissing((Expression) arg);
			args.add(arg);
			first = false;
			
			if(tryConsume(tokenSEPARATOR)) {
				continue;
			}
			break;
		}
		boolean properlyTerminated = consumeExpectedToken(tokenLISTCLOSE) != null;
		return new NodeListParseResult<Resolvable>(properlyTerminated, args);
	}
	
	public NodeResult<ExpParentheses> parseParenthesesExp() {
		if(!tryConsume(DeeTokens.OPEN_PARENS))
			return null;
		int nodeStart = lastLexElement().getStartPos();
		
		TypeOrExpResult arg = parseTypeOrExpression(ANY_OPERATOR);
		Resolvable resolvable;
		
		boolean isDotAfterParensSyntax = lookAhead() == DeeTokens.CLOSE_PARENS && lookAhead(1) == DeeTokens.DOT;
		if(isDotAfterParensSyntax) {
			resolvable = arg.toFinalResult(true).getNode_NoBrokenCheck();
		} else {
			resolvable = arg.toFinalResult(false).getNode_NoBrokenCheck();
			if(resolvable instanceof Reference) {
				addErrorTypeAsExpValue((Reference) resolvable);
			}
		}
		if(resolvable == null) {
			resolvable = nullExpToMissing(null);
		}
		boolean ruleBroken = consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null;
		
		return connectResult(ruleBroken, 
			new ExpParentheses(isDotAfterParensSyntax, resolvable, srToCursor(nodeStart)));
	}
	
	public ExpAssert parseAssertExpression() {
		if(tryConsume(DeeTokens.KW_ASSERT) == false)
			return null;
		
		int nodeStart = lastLexElement().getStartPos();
		Expression exp = null;
		Expression msg = null;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseAssignExpression_toMissing();
			if(tryConsume(DeeTokens.COMMA)) {
				msg = parseAssignExpression_toMissing();
			}
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		
		return connect(new ExpAssert(exp, msg, srToCursor(nodeStart)));
	}
	
	public ExpImportString parseImportExpression() {
		if(tryConsume(DeeTokens.KW_IMPORT) == false)
			return null;
		
		int nodeStart = lastLexElement().getStartPos();
		Expression exp = parseParensWithExpression();
		return connect(new ExpImportString(exp, srToCursor(nodeStart)));
	}
	
	public ExpMixinString parseMixinExpression() {
		if(tryConsume(DeeTokens.KW_MIXIN) == false)
			return null;
		
		int nodeStart = lastLexElement().getStartPos();
		Expression exp = parseParensWithExpression();
		return connect(new ExpMixinString(exp, srToCursor(nodeStart)));
	}
	
	protected Expression parseParensWithExpression() {
		return parseExpressionAroundParentheses(false).node; // TODO: check break
	}
	
	public NodeResult<Expression> parseExpressionAroundParentheses(boolean createMissing) {
		Expression exp = null;
		boolean ruleBroken = false;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseExpression_toMissing();
			ruleBroken = consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null;
		} else if(createMissing) {
			exp = connect(new MissingParenthesesExpression(srToCursor(getParserPosition())));
		}
		return nodeResult(ruleBroken, exp);
	}
	
	public ExpTypeId parseTypeIdExpression() {
		if(tryConsume(DeeTokens.KW_TYPEID) == false)
			return null;
		
		int nodeStart = lastLexElement().getStartPos();
		Reference ref = null;
		Expression exp = null;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			Resolvable resolvable = nullTypeOrExpToMissing(parseTypeOrExpression(true).getNode_NoBrokenCheck());
			if(resolvable instanceof Reference) {
				ref = (Reference) resolvable;
			} else {
				exp = (Expression) resolvable;
			}
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		if(ref != null) {
			return connect(new ExpTypeId(ref, srToCursor(nodeStart)));
		}
		return connect(new ExpTypeId(exp, srToCursor(nodeStart)));
	}
	
	public Expression parseNewExpression() {
		if(!tryConsume(DeeTokens.KW_NEW))
			return null;
		
		int nodeStart = lastLexElement().getStartPos();
		
		ArrayList<Expression> allocArgs = null;
		Reference type = null;
		ArrayList<Expression> args = null;
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				NodeListParseResult<Expression> allocArgsResult = parseExpArgumentList(DeeTokens.CLOSE_PARENS);
				allocArgs = allocArgsResult.result;
				if(allocArgsResult.ruleBroken) {
					break parsing;
				}
			}
			type = parseTypeReference_ToMissing(true).getNode();
			if(lastLexElement().isMissingElement()) {
				break parsing;
			}
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				args = parseExpArgumentList(DeeTokens.CLOSE_PARENS).result;
			}
		}
		
		return connect(new ExpNew(arrayView(allocArgs), type, arrayView(args), srToCursor(nodeStart)));
	}
	
	public Expression parseCastExpression() {
		if(!tryConsume(DeeTokens.KW_CAST))
			return null;
		
		int nodeStart = lastLexElement().getStartPos();
		
		Reference type = null;
		CastQualifiers qualifier = null;
		Expression exp = null;
		
		parsing: {
			if(consumeExpectedToken(DeeTokens.OPEN_PARENS) == null)
				break parsing;
			
			qualifier = parseCastQualifier();
			if(qualifier == null) {
				type = parseTypeReference_ToMissing(false).getNode_NoBrokenCheck();
			}
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null)
				break parsing;
			
			exp = parseUnaryExpression(); // TODO: check break
			exp = exp != null ? exp : createMissingExpression(RULE_EXPRESSION, false); 
		}
		
		if(qualifier != null) {
			return connect(new ExpCastQual(qualifier, exp, srToCursor(nodeStart)));
		} else {
			return connect(new ExpCast(type, exp, srToCursor(nodeStart)));
		}
	}
	
	public CastQualifiers parseCastQualifier() {
		if(tryConsume(DeeTokens.KW_SHARED, DeeTokens.KW_CONST))
			return CastQualifiers.SHARED_CONST;
		switch (lookAhead()) {
		case KW_CONST:
			return parseCastQualifier(DeeTokens.KW_SHARED, CastQualifiers.CONST_SHARED, CastQualifiers.CONST);
		case KW_INOUT:
			return parseCastQualifier(DeeTokens.KW_SHARED, CastQualifiers.INOUT_SHARED, CastQualifiers.INOUT);
		case KW_SHARED:
			return parseCastQualifier(DeeTokens.KW_INOUT, CastQualifiers.SHARED_INOUT, CastQualifiers.SHARED);
		case KW_IMMUTABLE:
			if(lookAhead(1) == DeeTokens.CLOSE_PARENS) {
				consumeInput();
				return CastQualifiers.IMMUTABLE;
			}
		default: return null;
		}
	}
	
	public CastQualifiers parseCastQualifier(DeeTokens token1, CastQualifiers altDouble, CastQualifiers altSingle) {
		if(lookAhead(1) == token1) {
			consumeInput();
			consumeInput();
			return altDouble;
		} else if(lookAhead(1) == DeeTokens.CLOSE_PARENS) {
			consumeInput();
			return altSingle;
		} else {
			return null;
		}
	}
	
}