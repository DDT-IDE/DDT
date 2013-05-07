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
import dtool.ast.ASTDirectChildrenVisitor;
import dtool.ast.ASTNode;
import dtool.ast.DeclList;
import dtool.ast.NodeData.PreParseNodeData;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.definitions.FunctionAttributes;
import dtool.ast.definitions.IFunctionParameter;
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
import dtool.ast.expressions.ExpLambda;
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
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.RefTypeFunction;
import dtool.ast.references.RefTypeModifier;
import dtool.ast.references.RefTypeModifier.TypeModifierKinds;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.RefTypeof;
import dtool.ast.references.Reference;
import dtool.ast.statements.IFunctionBody;
import dtool.parser.DeeParser_Decls.ElementListParseHelper;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;


public abstract class DeeParser_RefOrExp extends AbstractParser {
	
	/* ----------------------------------------------------------------- */
	
	public DeeTokens lookAheadGrouped() {
		return lookAheadToken().type.getGroupingToken();
	}
	
	public String idTokenToString(BaseLexElement id) {
		return id.isMissingElement() ? null : id.getSourceValue();
	}
	
	/* --------------------  reference parsing  --------------------- */
	
	public static final ParseRuleDescription RULE_REFERENCE = new ParseRuleDescription("Reference");
	public static final ParseRuleDescription RULE_TPL_SINGLE_ARG = new ParseRuleDescription("TemplateSingleArgument");
	
	public NodeResult<Reference> parseTypeReference() {
		return parseTypeReference_do(false);
	}
	
	public NodeResult<Reference> parseTypeReference(boolean createMissing, boolean reportMissingError) {
		NodeResult<Reference> typeRef = parseTypeReference();
		if((typeRef == null || typeRef.node == null) && createMissing) {
			return result(false, createMissingTypeReference(reportMissingError));
		}
		return typeRef;
	}
	
	public NodeResult<Reference> parseTypeReference_ToMissing() {
		return parseTypeReference_ToMissing(true);
	}
	public NodeResult<Reference> parseTypeReference_ToMissing(boolean reportMissingError) {
		return parseTypeReference(true, reportMissingError);
	}
	
	public Reference createMissingTypeReference(boolean reportMissingError) {
		SourceRange sourceRange = createExpectedToken(DeeTokens.IDENTIFIER).getSourceRange();
		return createMissingTypeReferenceNode(sourceRange, reportMissingError);
	}
	
	public Reference createMissingTypeReferenceNode(SourceRange sourceRange, boolean createMissingError) {
		ParserError error = createMissingError ? createErrorExpectedRule(RULE_REFERENCE) : null;
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
	
	protected NodeResult<Reference> parseTypeReference_do(boolean parsingExp) {
		NodeResult<? extends Reference> refParseResult;
		
		if(lookAheadGrouped() == DeeTokens.PRIMITIVE_KW) {
			return parseReference_referenceStart(parseRefPrimitive_start(lookAhead()), parsingExp);
		}
		switch (lookAhead()) {
		case DOT: refParseResult = parseRefModuleQualified(); break;
		case IDENTIFIER: return parseReference_referenceStart(parseRefIdentifier(), parsingExp);
		
		case KW_TYPEOF: refParseResult = parseRefTypeof(); break;
		
		case KW_CONST: refParseResult = parseRefTypeModifier_start(TypeModifierKinds.CONST); break;
		case KW_IMMUTABLE: refParseResult = parseRefTypeModifier_start(TypeModifierKinds.IMMUTABLE); break;
		case KW_SHARED: refParseResult = parseRefTypeModifier_start(TypeModifierKinds.SHARED); break;
		case KW_INOUT: refParseResult = parseRefTypeModifier_start(TypeModifierKinds.INOUT); break;
		default:
			return nullResult();
		}
		
		if(refParseResult.ruleBroken) 
			return refParseResult.<Reference>upcastTypeParam();
		return parseReference_referenceStart(refParseResult.node, parsingExp);
	}
	
	protected boolean isTypeModifier(DeeTokens lookAhead) {
		switch (lookAhead) {
		case KW_CONST: case KW_IMMUTABLE: case KW_SHARED: case KW_INOUT: return true;
		default: return false;
		}
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
		if(parse.consumeRequired(DeeTokens.OPEN_PARENS)) {
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
		if(parse.consumeRequired(DeeTokens.OPEN_PARENS)) {
			ref = parseTypeReference_ToMissing(true).node; 
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		}
		return parse.resultConclude(new RefTypeModifier(modKind, ref));
	}
	
	protected NodeResult<Reference> parseReference_referenceStart(Reference leftRef, boolean parsingExp) {
		assertNotNull(leftRef);
		return parseReference_referenceStart_do(leftRef, parsingExp);
	}
	protected NodeResult<Reference> parseReference_referenceStart_do(Reference leftRef, boolean parsingExp) {
		ParseHelper parse = new ParseHelper(leftRef == null ? -1 : leftRef.getStartPos());
		
		// Star is multiply infix operator, dont parse as pointer ref
		if(lookAhead() == DeeTokens.DOT && leftRef instanceof IQualifierNode) {
			IQualifierNode qualifier = (IQualifierNode) leftRef;
			assertTrue(!RefQualified.isExpressionQualifier(qualifier));
			consumeLookAhead();
			RefIdentifier qualifiedId = parseRefIdentifier();
			parse.ruleBroken = isMissing(qualifiedId);
			leftRef = parse.conclude(new RefQualified(qualifier, qualifiedId));
			
		} else if(lookAhead() == DeeTokens.NOT && isValidTemplateReferenceSyntax(leftRef)){ // template instance
			consumeLookAhead();
			
			ITemplateRefNode tplRef = (ITemplateRefNode) leftRef;
			ArrayList<Resolvable> tplArgs = null;
			Resolvable singleArg = null;
			
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				tplArgs = parseArgumentList(parse, true, DeeTokens.COMMA, DeeTokens.CLOSE_PARENS);
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
					singleArg = parseSimpleLiteral();
					if(singleArg == null) {
						singleArg = createMissingExpression(RULE_TPL_SINGLE_ARG); 
					}
				}
			}
			leftRef = parse.conclude(new RefTemplateInstance(tplRef, singleArg, arrayView(tplArgs)));
			
		} else if(!parsingExp && tryConsume(DeeTokens.STAR)) {
			leftRef = conclude(srToPosition(leftRef, new RefTypePointer(leftRef)));
			
		} else if(!parsingExp && tryConsume(DeeTokens.OPEN_BRACKET)) {
			
			Resolvable resolvable = parseTypeOrExpression(true).node;
			parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
			
			if(resolvable == null) {
				leftRef = parse.conclude(new RefTypeDynArray(leftRef));
			} else {
				leftRef = parse.conclude(new RefIndexing(leftRef, resolvable));
			}
			
		} else if(tryConsume(DeeTokens.KW_FUNCTION) || tryConsume(DeeTokens.KW_DELEGATE)) {
			leftRef = parse.checkResult(parseRefTypeFunction_afterReturnType(leftRef));
		} else {
			return result(false, leftRef);
		}
		if(parse.ruleBroken)
			return result(true, leftRef);
		return parseReference_referenceStart(leftRef, parsingExp);
	}
	
	public abstract NodeResult<RefTypeFunction> parseRefTypeFunction_afterReturnType(Reference retType);
	
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
	
	public NodeResult<Expression> parseAssignExpression_Rule(boolean breakOnMissing, 
		ParseRuleDescription expectedRule) {
		NodeResult<Expression> expResult = parseAssignExpression();
		if(expResult.node != null) {
			return expResult;
		}
		Expression missingNode = expectedRule != null ? createMissingExpression(expectedRule) : null;
		return result(expResult.ruleBroken || breakOnMissing, missingNode);
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
		return exp != null ? exp : createMissingExpression(RULE_EXPRESSION);
	}
	protected Resolvable nullTypeOrExpToMissing(Resolvable exp) {
		return exp != null ? exp : createMissingExpression(RULE_TYPE_OR_EXP);
	}
	
	protected Expression createMissingExpression(ParseRuleDescription expectedRule) {
		return createTypeOrExpMissingExp(TypeOrExpStatus.EXP, expectedRule, true);
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
		int nodeStart = getLexPosition();
		if(consumeIgnoreTokens) {
			consumeSubChannelTokens();
		}
		MissingExpression missingExp = srToPosition(nodeStart, new MissingExpression());
		ParserError error = null;
		if(expectedRule != null) {
			error = createErrorOnLastToken(ParserErrorTypes.EXPECTED_RULE, expectedRule.name);
			assertTrue(error.sourceRange.equals(lastLexElement().getSourceRange()));
		}
		if(mode == TypeOrExpStatus.EXP) {
			return conclude(error, missingExp);
		} else {
			missingExp.setData(new TypeOrExpData(mode, lastLexElement(), error, null));
			return missingExp;
		}
	}
	
	public boolean isMissing(Expression exp) {
		return exp == null || exp instanceof MissingExpression;
	}
	
	protected ParserError createErrorTypeAsExpValue(Reference reference) {
		return createError(ParserErrorTypes.TYPE_USED_AS_EXP_VALUE, reference.getSourceRange(), null);
	}
	
	protected ExpReference createExpReference(Reference ref) {
		ExpReference node = new ExpReference(ref);
		node.setSourceRange(ref.getSourceRange());
		return node;
	}
	
	protected ExpReference createExpReferenceWithError(Reference ref, boolean addError) {
		ExpReference expReference = createExpReference(ref);
		return conclude(addError ? createErrorTypeAsExpValue(ref) : null, expReference);
	}
	
	public static boolean couldHaveBeenParsedAsId(Reference ref) {
		return ref instanceof RefIdentifier;
	}
	
	public static ProtoDefSymbol convertRefIdToDef(Reference ref) {
		assertTrue(couldHaveBeenParsedAsId(ref));
		RefIdentifier refId = (RefIdentifier) ref;
		ParserError error = refId.name != null ? null : refId.getData().getNodeErrors().iterator().next();
		return new ProtoDefSymbol(refId.name == null ? "" : refId.name, ref.getSourceRange(), error);
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
	
	protected static class TypeOrExpData extends PreParseNodeData {
		public final TypeOrExpStatus mode;
		public final LexElement tokenInfo;
		public final ParserError error1;
		public final ParserError error2;
		
		public TypeOrExpData(TypeOrExpStatus mode, LexElement tokenInfo, ParserError error1, ParserError error2) {
			this.mode = mode;
			this.tokenInfo = tokenInfo;
			this.error1 = error1;
			this.error2 = error2;
		}
		public TypeOrExpData(TypeOrExpStatus mode) {
			this(mode, null, null, null);
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
	
	public static final Expression DUMMY_EXP = new MissingExpression() {{ setParsedStatus();}};
	
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
		return typeOrExpConnect(null, null, null, exp);
	}
	protected Expression typeOrExpConnect(LexElement afterStarOp, Expression exp) {
		return typeOrExpConnect(afterStarOp, null, null, exp);
	}
	protected Expression typeOrExpConnect(ParseHelper helper, Expression exp) {
		return typeOrExpConnect(null, helper, exp);
	}
	
	protected Expression typeOrExpConnect(LexElement afterStarOp, ParseHelper helper, Expression exp) {
		return typeOrExpConnect(afterStarOp, helper.error1, helper.error2, exp);	
	}
	
	protected Expression typeOrExpConnect(LexElement afterStarOp, ParserError error1, ParserError error2, 
		Expression exp) {
		if(mode == TypeOrExpStatus.EXP) {
			if(!exp.isParsedStatus()) {
				exp = concludeDo(error1, error2, exp);
			}
		} else {
			// This means the node must go through conversion process
			assertTrue(error2 == null);
			exp.setData(new TypeOrExpData(mode, afterStarOp, error1, error2));
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
				assertTrue(parseReference_referenceStart_do(null, needsExpContext).node == null);
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
			LexElement opAheadInfo = lookAheadElement();
			
			if(prefixExpOpToken.token.type != DeeTokens.STAR || isTypeOrExpStart || !mode.canBeType()) {
				updateTypeOrExpMode(TypeOrExpStatus.EXP);
			}
			Expression exp = parseUnaryExpression(false);
			if(exp == null) {
				exp = enterTypeOrExpMissingRightExpMode();
			}
			
			return typeOrExpConnect(opAheadInfo, srToPosition(prefixExpOpToken, new ExpPrefix(prefixOpType, exp)));
		}
		case OPEN_PARENS:
			return expConnect(matchParenthesesStart());
			
		case OPEN_BRACE: {
			int startPos = lookAheadElement().getStartPos();
			return expConnect(parseFunctionLiteral_atFunctionBody(startPos, null, null, null, null));
		}
		case KW_FUNCTION:
		case KW_DELEGATE:
			return expConnect(parseFunctionLiteral_start());
			
		case OPEN_BRACKET:
			if(isTypeOrExpStart) {
				updateTypeOrExpMode(TypeOrExpStatus.EXP);
			}
			return parseBracketList(null);
		case IDENTIFIER:
			if(lookAhead(1) == DeeTokens.LAMBDA) {
				return expConnect(parseSimpleLambdaLiteral_start());
			} // else fallthrough to TypeReference:
		default:
			NodeResult<Reference> typeRefResult = parseTypeReference_do(true);
			boolean ruleBroken = typeRefResult.ruleBroken;
			Reference ref = typeRefResult.node;
			if(ref == null) {
				return null; // TODO: option to return missing?
			}
			
			// Initialize mode
			if(isTypeOrExpStart) { 
				updateTypeOrExpMode(TypeOrExpStatus.TYPE_OR_EXP);
			} else {
				updateTypeOrExpMode(TypeOrExpStatus.EXP);
			}
			ParserError error = null;
			if(parsesAsTypeRef(ref)) {
				error = createErrorTypeAsExpValue(ref);
				if(mode.canBeType()) {
					updateTypeOrExpMode(TypeOrExpStatus.TYPE); // Begginning of Type ref
				}
			}
			setParseBroken(ruleBroken);
			return typeOrExpConnect(null, error, null, createExpReference(ref));
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
			exp = expConnect(parsePostfixOpExpression_atOperator(exp));
			return parsePostfixExpression(exp);
		}
		case POW: {
			if(mode == TypeOrExpStatus.TYPE)
				return resultConvertTypeThenContinueExpParse(exp);
			exp = convertTypeOrExpToExpression(exp);
			updateTypeOrExpMode(TypeOrExpStatus.EXP);
			return new ParseRule_InfixOperatorExp(exp).parseInfixOperator(InfixOpType.POW);
		}
		case OPEN_PARENS: {
			if(mode == TypeOrExpStatus.TYPE)
				return resultConvertTypeThenContinueExpParse(exp);
			exp = convertTypeOrExpToExpression(exp);
			exp = expConnect(parseCallExpression_atParenthesis(exp));
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
			IQualifierNode qualifier = resolvableToExp(false, convertTypeOrExp(null, exp, false));
			ParseHelper parse = new ParseHelper(qualifier.asNode());
			exp = null;
			if(qualifier instanceof ExpReference) {
				ExpReference expReference = (ExpReference) qualifier;
				if(expReference.ref instanceof RefQualified) {
					assertTrue(((RefQualified) expReference.ref).isExpressionQualifier);
				} else {
					assertTrue(!(expReference.ref instanceof IQualifierNode)); 
					// ...otherwise refqualified would have been parsed already
					parse.store(createError(ParserErrorTypes.INVALID_QUALIFIER, expReference.getSourceRange(), null));
				}
			}
			consumeLookAhead();
			RefIdentifier qualifiedId = parseRefIdentifier();
			Reference ref = parse.conclude(new RefQualified(qualifier, qualifiedId));
			updateTypeOrExpMode(TypeOrExpStatus.EXP);
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
			
			ParseHelper parse = new ParseHelper(leftExp);
			
			consumeLookAhead();
			if(opType == InfixOpType.NOT_IS || opType == InfixOpType.NOT_IN) {
				consumeLookAhead(); // consume second token
			}
			
			LexElement afterStarOp = null;
			
			if(opType != InfixOpType.MUL) {
				setExpMode();
				
				parse.store(checkValidAssociativityN(leftExp, opType));
			} else {
				assertTrue(lastLexElement().token.type == DeeTokens.STAR);
				afterStarOp = lookAheadElement();
			}
			
			Expression middleExp = null;
			
			parsing: {
				if(opType == InfixOpType.CONDITIONAL) {
					middleExp = nullExpToMissing(parseExpression().node);
					
					if(parse.consumeRequired(DeeTokens.COLON) == false) {
						setParseBroken(true);
						break parsing;
					}
				}
				
				InfixOpType rightExpPrecedence = getPrecedenceForInfixOpRightExp(opType);
				
				if(mode == TypeOrExpStatus.EXP) {
					NodeResult<Expression> expResult = parseExpression(rightExpPrecedence);
					setParseBroken(expResult.ruleBroken);
					rightExp = expResult.node;
				} else {
					rightExp = parseTypeOrExpression_start(rightExpPrecedence, false);
				}
				
				if(isMissing(rightExp)) {
					rightExp = enterTypeOrExpMissingRightExpMode();
				} else {
					if(mode == TypeOrExpStatus.EXP) {
						modeUpdatedToExp();
					}
					
					parse.store(checkValidAssociativityN(rightExp, opType));
				}
			}
			
			if(opType == InfixOpType.CONDITIONAL) {
				return expConnect(parse.conclude(new ExpConditional(leftExp, middleExp, rightExp)));
			}
			
			assertTrue(!(leftExp.isParsedStatus() && mode == TypeOrExpStatus.EXP_WITH_PENDING_TYPE));
			
			return typeOrExpConnect(afterStarOp, parse, parse.initRange(new ExpInfix(leftExp, opType, rightExp)));
		}
		
		@Override
		public void modeUpdatedToExp() {
			if(rightExp == null || rightExp.isParsedStatus()) {
				leftExp = convertTypeOrExpToExpression(leftExp); 
			}
		}
		
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
			final boolean isExpIndexing = calleeExp != null;
			
			ParseHelper parse = isExpIndexing ? new ParseHelper(calleeExp) : new ParseHelper();
			
			final boolean couldBeExpIndexing = mode == TypeOrExpStatus.TYPE && false; // This feature is disabled
			final DeeTokens secondLA = isExpIndexing ? DeeTokens.DOUBLE_DOT : DeeTokens.COLON;
			
			ArrayList<Expression> elements = new ArrayList<Expression>();
			ArrayList<MapArrayLiteralKeyValue> mapElements = null;
			
			boolean firstElement = true;
			
			while(true) {
				Expression exp1;
				Expression exp2 = null;
				ParseHelper exp2parse = null;
				
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
							parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
							setParseBroken(parse.ruleBroken);
							return typeOrExpConnect(parse, parse.initRange(new ExpSlice(calleeExp)));
						}
						break; // Empty array literal
					}
					
					firstArg = nullExpToMissing(firstArg);
					
					if(lookAhead() == DeeTokens.COMMA) {
						setExpMode();
					} else if(!isExpIndexing && tryConsume(DeeTokens.COLON)) {
						setExpMode();
						assertTrue(calleeExp == null);
						exp2parse = new ParseHelper(firstArg);
						exp2 = parseAssignExpression_toMissing();
						mapElements = new ArrayList<MapArrayLiteralKeyValue>();
					} else if((isExpIndexing || couldBeExpIndexing)&& tryConsume(DeeTokens.DOUBLE_DOT)) {
						setExpMode();
						exp2 = parseAssignExpression_toMissing();
						
						parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
						setParseBroken(parse.ruleBroken);
						
						if(!isExpIndexing) { // Small trick to improve parsing
							calleeExp = createTypeOrExpMissingExp(TypeOrExpStatus.TYPE, false);
							int nodeStart = parse.nodeStart;
							calleeExp.setSourcePosition(nodeStart, nodeStart); // range won't matter in the end.
						}
						
						return typeOrExpBracketList(parse, new ExpSlice(calleeExp, firstArg, exp2));
					}
					exp1 = firstArg;
				} else {
					exp1 = parseAssignExpression_toMissing();
					
					if(mapElements != null ) {
						exp2parse = new ParseHelper(exp1);
						assertTrue(mode == TypeOrExpStatus.EXP);
						if(exp2parse.consumeExpected(DeeTokens.COLON)) {
							exp2 = parseAssignExpression_toMissing();
						}
					}
				}
				firstElement = false;
				
				if(mapElements == null ) {
					elements.add(exp1);
				} else {
					mapElements.add(exp2parse.conclude(new MapArrayLiteralKeyValue(exp1, exp2)));
				}
				
				if(tryConsume(DeeTokens.COMMA)) {
					continue;
				}
				break;
			}
			
			
			parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
			setParseBroken(parse.ruleBroken);
			
			if(calleeExp == null) {
				if(mapElements != null ) {
					return expConnect(parse.conclude(new ExpLiteralMapArray(arrayView(mapElements))));
				} else {
					return typeOrExpBracketList(parse, new ExpLiteralArray(arrayView(elements)));
				}
			}
			return typeOrExpBracketList(parse, new ExpIndex(calleeExp, arrayView(elements)));
		}
		
		@Override
		public void modeUpdatedToExp() {
			if(calleeExp != null && getTypeOrExpStatus(calleeExp) != TypeOrExpStatus.TYPE) {
				calleeExp = convertTypeOrExpToExpression(calleeExp);
			}
			firstArg = convertTypeOrExpToExpression(firstArg);
		}
		
		protected Expression typeOrExpBracketList(ParseHelper parse, Expression exp) {
			if(mode == TypeOrExpStatus.EXP && getTypeOrExpStatus(calleeExp) == TypeOrExpStatus.TYPE) {
				updateTypeOrExpMode(TypeOrExpStatus.EXP_WITH_PENDING_TYPE);
				setConvertPendingNodeThenContinueExpMode();
			}
			return typeOrExpConnect(parse, parse.initRange(exp));
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
		case REF_TYPE_FUNCTION:
			return true;
			
		case REF_TYPE_DYN_ARRAY:
		case REF_TYPE_POINTER:
		case REF_INDEXING:
			throw assertFail(); // This method should not be used with these kinds of refs
		default:
			throw assertFail();
		}
	}
	
	public Expression wrapReferenceForTypeOrExpParse(Reference ref) {
		ExpReference node = createExpReference(ref);
		node.setData(new TypeOrExpData(TypeOrExpStatus.TYPE, null, createErrorTypeAsExpValue(ref), null));
		return node;
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
			return result(definiteRuleBroken, convertTypeOrExpToReference(exp));
		}
		
		public NodeResult<Expression> toExpression() {
			return expResult(convertTypeOrExpToExpression(exp));
		}

		protected <T extends ASTNode> NodeResult<T> expResult(T result) {
			boolean additionalBreaks = mode == TypeOrExpStatus.TYPE_OR_EXP_WITH_MISSING_RIGHT
				|| (result instanceof ExpReference && ((ExpReference) result).ref instanceof RefTypePointer);
			return result(definiteRuleBroken || additionalBreaks, result);
		}
		
		public NodeResult<Resolvable> toFinalResult(boolean ambiguousToRef) {
			boolean toRef = mode == TypeOrExpStatus.TYPE || (mode != null && mode.canBeType() && ambiguousToRef);
			Resolvable result = convertTypeOrExp(null, exp, toRef);
			if(result instanceof Expression) {
				return expResult(result);
			}
			return result(definiteRuleBroken, result);
		}
	}
	
	protected Expression convertTypeOrExpToExpression(Expression exp) {
		return resolvableToExp(true, convertTypeOrExp(null, exp, false));
	}
	
	protected Expression resolvableToExp(boolean reportError, Resolvable resolvable) {
		if(resolvable instanceof Reference) {
			Reference reference = (Reference) resolvable;
			return createExpReferenceWithError(reference, reportError);
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
			SourceRange errorSR = exp.getData(TypeOrExpData.class).tokenInfo.getSourceRange();
			if(convertToTypeRef) {
				if(refOnTheLeft != null) {
					return refOnTheLeft;
				} else {
					ParserError error = createError(ParserErrorTypes.EXPECTED_RULE, errorSR, RULE_REFERENCE.name);
					return createMissingTypeReferenceNode(exp.getSourceRange(), error);
				}
			}
			
			break;
		}
		case EXP_REFERENCE: {
			assertTrue(refOnTheLeft == null);
			if(!toRef) {
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
			
			TypeOrExpData oldData = detachParent(expInfix);
			int endPos = oldData.tokenInfo.getFullRangeStartPos();
			refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, expInfix.leftExp);
			
			refOnTheLeft = concludeToE(oldData, refOnTheLeft, endPos, new RefTypePointer(refOnTheLeft));
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
			TypeOrExpData oldData = detachParent(expPrefix);
			int endPos = assertNotNull_(oldData.tokenInfo).getFullRangeStartPos();
			refOnTheLeft = concludeToE(oldData, refOnTheLeft, endPos, new RefTypePointer(refOnTheLeft));
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
				
				TypeOrExpData oldData = detachParent(expSlice);
				refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, expSlice.slicee);
				return concludeToE(oldData, refOnTheLeft, expSlice.getEndPos(), new RefTypeDynArray(refOnTheLeft));
			} else {
				TypeOrExpData oldData = detachParent(expSlice);
				refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, expSlice.slicee);
				Expression expSlicee = resolvableToExp(true, refOnTheLeft);
				return concludeToE(oldData, refOnTheLeft, exp.getEndPos(), 
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
				
				TypeOrExpData oldData = detachParent(expIndex);
				refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, expIndex.indexee);
				return convertToRefIndexing(refOnTheLeft, oldData, expIndex, expIndex.args.get(0));
			} else {
				TypeOrExpData oldData = detachParent(expIndex);
				refOnTheLeft = convertTypeOrExpToReference(refOnTheLeft, expIndex.indexee);
				Expression expIndexee = resolvableToExp(true, refOnTheLeft);
				return concludeToE(oldData, expIndexee, exp.getEndPos(), new ExpIndex(expIndexee, expIndex.args));
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
				TypeOrExpData oldData = detachParent(expLiteralArray);
				return concludeToE(oldData, refOnTheLeft, exp.getEndPos(), new RefTypeDynArray(refOnTheLeft));
			} else {
				TypeOrExpData oldData = detachParent(expLiteralArray);
				return convertToRefIndexing(refOnTheLeft, oldData, expLiteralArray, expLiteralArray.elements.get(0));
			}
		}
		
		default:
			throw assertFail();
		}
		
		assertTrue(refOnTheLeft == null);
		TypeOrExpData oldData = exp.removeData(TypeOrExpData.class);
		return concludeDo(oldData.error1, oldData.error2, exp);
	}
	
	public <T extends ASTNode> T concludeToE(TypeOrExpData oldData, Resolvable starNode, int nodeEndPos, T node) {
		return concludeDo(oldData.error1, oldData.error2, srBounds(starNode.getStartPos(), nodeEndPos, node));
	}
	
	protected Expression convertTypeOrExpToExpression_noChange(Expression exp) {
		assertTrue(exp == null || exp.isParsedStatus() || getTypeOrExpStatus(exp).canBeExp());
		Expression converted = assertCast(convertTypeOrExp(null, exp, false), Expression.class);
		assertTrue(converted == exp);
		return converted;
	}
	
	public TypeOrExpData detachParent(Expression exp) {
		TypeOrExpData oldData = exp.removeData(TypeOrExpData.class);
		exp.accept(new ASTDirectChildrenVisitor() {
			@Override
			protected void geneticChildrenVisit(ASTNode child) {
				child.detachFromParent();
			}
		});
		return oldData;
	}
	
	public Reference convertToRefIndexing(Reference leftRef, TypeOrExpData oldData, Expression exp, 
		Expression indexArgExp) {
		Resolvable indexArg;
		if(indexArgExp.isParsedStatus()) {
			// argument can only be interpreted as expression
			indexArg = indexArgExp;
		} else if(getTypeOrExpStatus(indexArgExp).canBeType()) {
			indexArg = convertTypeOrExpToReference(indexArgExp);
		} else {
			throw assertFail();
		}
		
		assertTrue(oldData.error2 == null);
		return concludeDo(oldData.error1, null, srBounds(leftRef, exp, new RefIndexing(leftRef, indexArg)));
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
			
		case KW___LINE__:
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpLiteralInteger(lastLexElement().token)));
		case KW___FILE__:
			consumeLookAhead();
			return conclude(srOf(lastLexElement(), new ExpLiteralString(lastLexElement().token)));
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
		return srToPosition(exp, new ExpPostfixOperator(exp, PostfixOpType.tokenToPrefixOpType(op.type)));
	}
	
	protected NodeResult<ExpCall> parseCallExpression_atParenthesis(Expression callee) {
		ParseHelper parse = new ParseHelper(callee);
		consumeLookAhead(DeeTokens.OPEN_PARENS);
		ArrayList<Expression> args = parseExpArgumentList(parse, DeeTokens.CLOSE_PARENS);
		return parse.resultConclude(new ExpCall(callee, arrayView(args)));
	}
	
	protected ArrayList<Expression> parseExpArgumentList(ParseHelper parse, DeeTokens tokenLISTCLOSE) {
		return CoreUtil.blindCast(parseArgumentList(parse, false, DeeTokens.COMMA, tokenLISTCLOSE));
	}
	protected ArrayList<Resolvable> parseArgumentList(ParseHelper parse, boolean parseTypeOrExp, 
		DeeTokens tokenSEPARATOR, DeeTokens tokenLISTCLOSE) {
		
		ArrayList<Resolvable> args = new ArrayList<Resolvable>();
		
		boolean first = true;
		do {
			Resolvable arg = (parseTypeOrExp ? parseTypeOrAssignExpression(true) : parseAssignExpression()).node;
			
			if(first && arg == null && lookAhead() != tokenSEPARATOR) {
				break;
			}
			arg = parseTypeOrExp ? nullTypeOrExpToMissing(arg) : nullExpToMissing((Expression) arg);
			args.add(arg);
			first = false;
		} while(tryConsume(tokenSEPARATOR));
		
		parse.consumeRequired(tokenLISTCLOSE);
		return new ArrayList<Resolvable>(args);
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
				
				if(tryConsume(DeeTokens.LAMBDA)) {
					
					Expression bodyExp = parse.checkResult(parseAssignExpression_Rule(true, RULE_EXPRESSION));
					return parse.resultConclude(new ExpLambda(fnParams, fnAttributes, bodyExp));
				}
				
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
		Expression bodyExp = parse.checkResult(parseAssignExpression_Rule(true, RULE_EXPRESSION));
		
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
		
		return parse.resultConclude(new ExpFunctionLiteral(isFunctionKeyword, retType, fnParams, fnAttributes, null));
	}
	
	protected NodeResult<ExpFunctionLiteral> parseFunctionLiteral_atFunctionBody(int nodeStart,
		Boolean isFunctionKeyword, Reference retType, ArrayView<IFunctionParameter> fnParams,
		ArrayView<FunctionAttributes> fnAttributes) 
	{
		NodeResult<? extends IFunctionBody> fnBody = thisParser().parseBlockStatement(true, false);
		
		return resultConclude(fnBody.ruleBroken, srToPosition(nodeStart, 
			new ExpFunctionLiteral(isFunctionKeyword, retType, fnParams, fnAttributes, fnBody.node)));
	}
	
	public NodeResult<ExpParentheses> parseParenthesesExp() {
		if(!tryConsume(DeeTokens.OPEN_PARENS))
			return null;
		ParseHelper parse = new ParseHelper();
		
		TypeOrExpResult arg = parseTypeOrExpression(ANY_OPERATOR);
		Resolvable resolvable;
		
		boolean isDotAfterParensSyntax = lookAhead() == DeeTokens.CLOSE_PARENS && lookAhead(1) == DeeTokens.DOT;
		if(isDotAfterParensSyntax) {
			resolvable = arg.toFinalResult(true).node;
		} else {
			resolvable = arg.toFinalResult(false).node;
			if(resolvable instanceof Reference) {
				parse.store(createErrorTypeAsExpValue((Reference) resolvable));
			}
		}
		if(resolvable == null) {
			resolvable = nullExpToMissing(null);
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
		if(parse.consumeRequired(DeeTokens.OPEN_PARENS)) {
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
		
		Expression expParentheses = parseExpressionAroundParentheses(parse, false);
		return parse.resultConclude(new ExpImportString(expParentheses));
	}
	
	public NodeResult<ExpMixinString> parseMixinExpression() {
		if(tryConsume(DeeTokens.KW_MIXIN) == false)
			return null;
		ParseHelper parse = new ParseHelper();
		
		Expression expParentheses = parseExpressionAroundParentheses(parse, false);
		return parse.resultConclude(new ExpMixinString(expParentheses));
	}
	
	public Expression parseExpressionAroundParentheses(ParseHelper parse, boolean createMissing) {
		Expression exp = null;
		
		if(parse.consumeRequired(DeeTokens.OPEN_PARENS) == false) {
			if(createMissing) {
				parse.ruleBroken = false;
				exp = conclude(srToPosition(getLexPosition(), new MissingParenthesesExpression()));
			}
		} else {
			exp = parseExpression_toMissing();
			parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		}
		return exp;
	}
	
	public NodeResult<ExpTypeId> parseTypeIdExpression() {
		if(tryConsume(DeeTokens.KW_TYPEID) == false)
			return null;
		ParseHelper parse = new ParseHelper();
		
		Reference ref = null;
		Expression exp = null;
		
		if(parse.consumeRequired(DeeTokens.OPEN_PARENS)) {
			Resolvable resolvable = nullTypeOrExpToMissing(parseTypeOrExpression(true).node);
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
		
		ArrayList<Expression> allocArgs = null;
		Reference type = null;
		ArrayList<Expression> args = null;
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				allocArgs = parseExpArgumentList(parse, DeeTokens.CLOSE_PARENS);
			}
			if(tryConsume(DeeTokens.KW_CLASS)) {
				return parseNewAnonClassExpression_afterClassKeyword(parse, allocArgs);
			}
			if(parse.ruleBroken) break parsing;
			
			type = parseTypeReference_ToMissing(true).node;
			parse.ruleBroken = isMissing(type);
			if(parse.ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				args = parseExpArgumentList(parse, DeeTokens.CLOSE_PARENS);
			}
		}
		
		return parse.resultConclude(new ExpNew(arrayView(allocArgs), type, arrayView(args)));
	}
	
	protected NodeResult<ExpNewAnonClass> parseNewAnonClassExpression_afterClassKeyword(ParseHelper parse, 
		ArrayList<Expression> allocArgs) {
		parse.ruleBroken = false;
		
		ArrayView<Expression> args = null;
		ElementListParseHelper<Reference> baseClasses = thisParser().new TypeReferenceListParse();
		DeclList declBody = null;
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				args = arrayView(parseExpArgumentList(parse, DeeTokens.CLOSE_PARENS));
				if(parse.ruleBroken) break parsing;
			}
			
			baseClasses.parseSimpleList(true, DeeTokens.COMMA);
			
			declBody = thisParser().parseDeclarationBlock(parse);
		}
		
		return parse.resultConclude(new ExpNewAnonClass(arrayView(allocArgs), args, baseClasses.members, declBody));
	}
	
	public NodeResult<? extends Expression> parseCastExpression() {
		if(!tryConsume(DeeTokens.KW_CAST))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Reference type = null;
		CastQualifiers qualifier = null;
		Expression exp = null;
		
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS) == false)
				break parsing;
			
			qualifier = parseCastQualifier();
			if(qualifier == null) {
				type = parseTypeReference_ToMissing(false).node;
			}
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS) == false)
				break parsing;
			
			exp = parseUnaryExpression(); // TODO: check break
			exp = exp != null ? exp : createMissingExpression(RULE_EXPRESSION, false); 
		}
		
		if(qualifier != null) {
			return parse.resultConclude(new ExpCastQual(qualifier, exp));
		} else {
			return parse.resultConclude(new ExpCast(type, exp));
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
				consumeLookAhead();
				return CastQualifiers.IMMUTABLE;
			}
		default: return null;
		}
	}
	
	public CastQualifiers parseCastQualifier(DeeTokens token1, CastQualifiers altDouble, CastQualifiers altSingle) {
		if(lookAhead(1) == token1) {
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
	
}