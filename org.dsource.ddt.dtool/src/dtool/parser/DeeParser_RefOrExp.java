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

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.ASTDefaultVisitor;
import dtool.ast.ASTNeoNode;
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
import dtool.ast.expressions.ExpPostfix;
import dtool.ast.expressions.ExpPostfix.PostfixOpType;
import dtool.ast.expressions.ExpPrefix;
import dtool.ast.expressions.ExpPrefix.PrefixOpType;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSlice;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.ExpTypeId;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingExpression;
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


public class DeeParser_RefOrExp extends AbstractParser {
	
	public DeeParser_RefOrExp(String source) {
		super(new DeeLexer(source));
	}
	
	public DeeParser_RefOrExp(DeeLexer deeLexer) {
		super(deeLexer);
	}
	
	/* ----------------------------------------------------------------- */
	
	public DeeTokens lookAheadGrouped() {
		return lookAheadToken().type.getGroupingToken();
	}
	
	public static class RuleParseResult<T>  {
		public final boolean parseBroken;
		public final T result;
		
		public RuleParseResult(boolean parseBroken, T result) {
			this.parseBroken = parseBroken;
			this.result = result;
		}
	}
	
	public static <T> RuleParseResult<T> parseResult(boolean parseBroken, T result) {
		return new RuleParseResult<T>(parseBroken, result);
	}
	
	public <T extends ASTNeoNode> RuleParseResult<T> connectResult(boolean parseBroken, T result) {
		return new RuleParseResult<T>(parseBroken, connect(result));
	}
	
	public <T> RuleParseResult<T> nullResult() {
		return new RuleParseResult<T>(false, null);
	}
	
	public String idTokenToString(LexElement id) {
		return id.isMissingElement() ? null : id.token.source;
	}
	
	/* --------------------  reference parsing  --------------------- */
	
	public static ParseRuleDescription RULE_REFERENCE = new ParseRuleDescription("Reference");
	
	public Reference parseReference() {
		return parseReference_begin(false).ref;
	}
	
	public Reference parseReference_ToMissing(boolean reportMissingError) {
		Reference ref = parseReference();
		return ref != null ? ref : createMissingReference(reportMissingError);
	}
	
	public Reference createMissingReference(boolean reportMissingError) {
		if(reportMissingError) {
			reportErrorExpectedRule(RULE_REFERENCE);
		}
		LexElement id = createExpectedToken(DeeTokens.IDENTIFIER);
		return connect(new RefIdentifier(idTokenToString(id), sr(id.token)));
	}
	
	public Reference parseReference(boolean expressionContext) {
		return parseReference_begin(expressionContext).ref;
	}
	
	protected static class RefParseResult { 
		public final Reference ref;
		public final boolean parseBroken;
		
		public RefParseResult(boolean parseBroken, Reference ref) {
			this.ref = ref;
			this.parseBroken = parseBroken;
			assertTrue(!(parseBroken && ref == null));
		}
		public RefParseResult(Reference ref) {
			this(false, ref);
		}
	}
	
	protected RefParseResult parseReference_begin() {
		return parseReference_begin(false);
	}
	
	protected RefParseResult parseReference_begin(boolean parsingExp) {
		RefParseResult refParseResult;
		
		if(lookAheadGrouped() == DeeTokens.PRIMITIVE_KW) {
			return parseReference_ReferenceStart(parseRefPrimitive(lookAhead()), parsingExp);
		}
		switch (lookAhead()) {
		case DOT: refParseResult = parseRefModuleQualified_do(); break;
		case IDENTIFIER: return parseReference_ReferenceStart(parseRefIdentifier(), parsingExp);
		
		case KW_TYPEOF: refParseResult = parseRefTypeof_do(); break;
		
		case KW_CONST: refParseResult = parseRefTypeModifier_do(TypeModifierKinds.CONST); break;
		case KW_IMMUTABLE: refParseResult = parseRefTypeModifier_do(TypeModifierKinds.IMMUTABLE); break;
		case KW_SHARED: refParseResult = parseRefTypeModifier_do(TypeModifierKinds.SHARED); break;
		case KW_INOUT: refParseResult = parseRefTypeModifier_do(TypeModifierKinds.INOUT); break;
		default:
			return new RefParseResult(null);
		}
		
		if(refParseResult.parseBroken) 
			return refParseResult;
		return parseReference_ReferenceStart(refParseResult.ref, parsingExp);
	}
	
	protected boolean isTypeModifier(DeeTokens lookAhead) {
		switch (lookAhead) {
		case KW_CONST: case KW_IMMUTABLE: case KW_SHARED: case KW_INOUT: return true;
		default: return false;
		}
	}
	
	protected RefIdentifier parseRefIdentifier() {
		LexElement id = consumeExpectedIdentifier();
		return connect(new RefIdentifier(idTokenToString(id), sr(id.token)));
	}
	
	protected RefIdentifier createMissingRefIdentifier() {
		LexElement id = createExpectedToken(DeeTokens.IDENTIFIER);
		return connect(new RefIdentifier(idTokenToString(id), sr(id.token)));
	}
	
	protected RefPrimitive parseRefPrimitive(DeeTokens primitiveType) {
		Token token = consumeLookAhead(primitiveType);
		return connect(new RefPrimitive(token, sr(token)));
	}
	
	public RefModuleQualified parseRefModuleQualified() {
		return (RefModuleQualified) parseRefModuleQualified_do().ref;
	}
	
	protected RefParseResult parseRefModuleQualified_do() {
		if(!tryConsume(DeeTokens.DOT))
			return new RefParseResult(null);
		int nodeStart = lastLexElement.getStartPos();
		
		boolean parseBroken = lookAhead() != DeeTokens.IDENTIFIER;
		RefIdentifier id = parseRefIdentifier();
		return new RefParseResult(parseBroken, connect(new RefModuleQualified(id, srToCursor(nodeStart))));
	}
	
	protected RefParseResult parseRefTypeof_do() {
		if(!tryConsume(DeeTokens.KW_TYPEOF))
			return null;
		int nodeStart = lastLexElement.getStartPos();
		
		Expression exp = null;
		boolean parseBroken = true;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			if(tryConsume(DeeTokens.KW_RETURN)) {
				exp = new RefTypeof.ExpRefReturn(lastLexElement.getSourceRange());
			} else {
				exp = parseExpression_ToMissing(true);
			}
			parseBroken = consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null;
		}
		return new RefParseResult(parseBroken, connect(new RefTypeof(exp, srToCursor(nodeStart))));
	}
	
	protected RefParseResult parseRefTypeModifier_do(TypeModifierKinds modKind) {
		assertTrue(lookAhead().sourceValue.equals(modKind.sourceValue));
		consumeInput();
		int nodeStart = lastLexElement.getStartPos();
		
		Reference ref = null;
		boolean parseBroken = true;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			ref = parseReference_ToMissing(true); 
			parseBroken = consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null;
		}
		return new RefParseResult(parseBroken, connect(new RefTypeModifier(modKind, ref, srToCursor(nodeStart))));
	}
	
	protected RefParseResult parseReference_ReferenceStart(Reference leftRef, boolean parsingExp) {
		assertNotNull(leftRef);
		return parseReference_ReferenceStart_do(leftRef, parsingExp);
	}
	protected RefParseResult parseReference_ReferenceStart_do(Reference leftRef, boolean parsingExp) {
		boolean parseBroken = false;
		
		// Star is multiply infix operator, dont parse as pointer ref
		if(lookAhead() == DeeTokens.DOT) {
			if(leftRef instanceof IQualifierNode == false) {
				addError(ParserErrorTypes.INVALID_QUALIFIER, leftRef.getSourceRange(), null);
				return new RefParseResult(leftRef);
			}
			IQualifierNode qualifier = (IQualifierNode) leftRef;
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
				ArgumentListParseResult<Resolvable> argList = 
					parseArgumentList(true, DeeTokens.COMMA, DeeTokens.CLOSE_PARENS);
				tplArgs = argList.list;
				parseBroken = argList.parseBroken;
			} else {
				if(leftRef instanceof RefTemplateInstance) {
					RefTemplateInstance refTplInstance = (RefTemplateInstance) leftRef;
					if(refTplInstance.isSingleArgSyntax()) {
						addError(ParserErrorTypes.NO_CHAINED_TPL_SINGLE_ARG, refTplInstance.getSourceRange(), null);
					}
				}
				
				if(lookAheadGrouped() == DeeTokens.PRIMITIVE_KW) {
					singleArg = parseRefPrimitive(lookAhead());	
				} else if(lookAheadGrouped() == DeeTokens.IDENTIFIER) { 
					singleArg = parseRefIdentifier();
				} else {
					singleArg = parseSimpleLiteral();
					if(singleArg == null) {
						singleArg = createMissingExpression(true, RULE_TPL_SINGLE_ARG); 
					}
				}
			}
			leftRef = connect(new RefTemplateInstance(tplRef, singleArg, arrayView(tplArgs), srToCursor(leftRef)));
			
		} else if(!parsingExp && tryConsume(DeeTokens.STAR)) {
			leftRef = connect(new RefTypePointer(leftRef, srToCursor(leftRef.getStartPos())));
			
		} else if(!parsingExp && tryConsume(DeeTokens.OPEN_BRACKET)) {
			Resolvable resolvable = parseReferenceOrExpression(true);
			parseBroken = consumeExpectedToken(DeeTokens.CLOSE_BRACKET) == null;
			
			if(resolvable == null) {
				leftRef = connect(new RefTypeDynArray(leftRef, srToCursor(leftRef.getStartPos())));
			} else {
				leftRef = connect(new RefIndexing(leftRef, resolvable, srToCursor(leftRef.getStartPos())));
			}
			
		} else {
			return new RefParseResult(leftRef);
		}
		if(parseBroken)
			return new RefParseResult(true, leftRef);
		return parseReference_ReferenceStart(leftRef, parsingExp);
	}
	
	public boolean isValidTemplateReferenceSyntax(Reference leftRef) {
		return leftRef instanceof ITemplateRefNode;
	}
	
	/* ----------------------------------------- */
	
	public static ParseRuleDescription RULE_EXPRESSION = new ParseRuleDescription("Expression");
	public static ParseRuleDescription RULE_REF_OR_EXP = new ParseRuleDescription("Reference or Expression");
	
	public static ParseRuleDescription RULE_TPL_SINGLE_ARG = new ParseRuleDescription("TemplateSingleArgument");
	
	public static int ANY_OPERATOR = 0;

	public Expression parseExpression() {
		return parseExpression(ANY_OPERATOR);
	}
	
	protected Expression parseExpression(int precedenceLimit) {
		return parseReferenceStartOrExpression(precedenceLimit, false, true).getExp_NoRuleContinue();
	}
	
	public Expression parseExpression_ToMissing(boolean reportMissingExpError) {
		return nullExpToMissing(parseExpression(), reportMissingExpError);
	}
	
	public Expression parseAssignExpression() {
		return parseExpression(InfixOpType.ASSIGN.precedence);
	}
	
	public Expression parseAssignExpression_toMissing(boolean reportMissingExpError) {
		return nullExpToMissing(parseAssignExpression(), reportMissingExpError);
	}
	
	public Resolvable parseReferenceOrExpression(boolean ambiguousToRef) {
		return parseReferenceOrExpression(ANY_OPERATOR, ambiguousToRef);
	}
	
	public Resolvable parseReferenceOrAssignExpression(boolean ambiguousToRef) {
		return parseReferenceOrExpression(InfixOpType.ASSIGN.precedence, ambiguousToRef);
	}
	
	public Resolvable parseReferenceOrExpression(int precedenceLimit, boolean ambiguousToRef) {
		RefOrExpFullResult refOrExp = parseReferenceOrExpression_full(precedenceLimit);
		if(refOrExp.mode == RefOrExpMode.REF_OR_EXP) {
			if(ambiguousToRef) {
				return convertRefOrExpToReference(refOrExp.getExpression());
			} else {
				return convertRefOrExpToExpression(refOrExp.getExpression());
			}
		}
		return refOrExp.resolvable;
	}
	
	protected Expression nullExpToMissing(Expression exp, boolean reportMissingExpError) {
		return exp != null ? exp : createMissingExpression(reportMissingExpError, RULE_EXPRESSION);
	}
	protected Resolvable nullRoEToMissing(Resolvable exp, boolean reportMissingExpError) {
		return exp != null ? exp : createMissingExpression(reportMissingExpError, RULE_REF_OR_EXP);
	}
	
	protected Expression createMissingExpression(boolean reportMissingExpError, ParseRuleDescription expectedRule) {
		if(reportMissingExpError) {
			reportErrorExpectedRule(expectedRule);
		}
		int nodeStart = lastLexElement.getEndPos();
		return connect(new MissingExpression(srToCursor(nodeStart)));
	}
	
	protected RefOrExpFullResult parseReferenceOrExpression_full(int precedenceLimit) {
		// canBeRef will indicate whether the expression parsed so far could also have been parsed as a reference.
		// It is essential that every function call checks and updates the value of this variable before
		// consuming additional tokens from the stream.
		
		RefOrExpParse refOrExp = parseReferenceStartOrExpression(precedenceLimit, true, true);
		if(refOrExp.mode == null)
			return new RefOrExpFullResult(null, null);
		
		if(refOrExp.mode == RefOrExpMode.EXP) {
			return new RefOrExpFullResult(RefOrExpMode.EXP, refOrExp.getExp());
		} else if(refOrExp.mode == RefOrExpMode.REF ) {
			// The expression we parse should actually have been parsed as a reference, so convert it:
			Reference startRef = convertRefOrExpToReference(refOrExp.getExp_NoRuleContinue());
			// And resume parsing as ref
			Reference ref = parseReference_ReferenceStart(startRef, false).ref;
			return new RefOrExpFullResult(RefOrExpMode.REF, ref);
		} else {
			// Ambiguous RoE must not leave refs ahead (otherwise it should have been part of ambiguous)
			assertTrue(parseReference_ReferenceStart_do(null, false).ref == null); 
			return new RefOrExpFullResult(RefOrExpMode.REF_OR_EXP, refOrExp.getExp());
		}
	}
	
	public static enum RefOrExpMode { REF, EXP, REF_OR_EXP }
	
	protected static class RefOrExpParse {
		
		public final RefOrExpMode mode;
		public final boolean breakRule;
		private final Expression exp;
		
		public RefOrExpParse(RefOrExpMode mode, boolean breakRule, Expression exp) {
			this.mode = mode;
			this.breakRule = breakRule;
			this.exp = exp;
			assertTrue((mode == null) == (exp == null));
			if(exp != null) {
				assertTrue((exp.getData() == PARSED_STATUS) == (mode == RefOrExpMode.EXP));
			}
		}
		
		public boolean canBeRef() {
			assertTrue(mode != null && mode != RefOrExpMode.REF);
			return mode == RefOrExpMode.REF_OR_EXP;
		}
		
		public boolean shouldStopRule() {
			return mode == null || mode == RefOrExpMode.REF || breakRule;
		}
		
		public Expression getExp() {
			assertTrue(mode != null && mode != RefOrExpMode.REF);
			return exp;
		}
		
		/** Using this method means the RoE parsing will not continue consuming more tokens 
		 * (unless the mode is checked again). */
		public Expression getExp_NoRuleContinue() {
			return exp;
		}
		
	}
	
	protected static class RefOrExpResultHelper {
		public RefOrExpMode mode;
		
		public RefOrExpResultHelper(boolean canBeRef) {
			updateMode(canBeRef);
		}
		
		public boolean canBeRef() {
			assertTrue(mode != RefOrExpMode.REF);
			return mode == RefOrExpMode.REF_OR_EXP;
		}
		
		public void updateRefOrExpToExpression(boolean newCanBeRef, Expression exp1) {
			updateRefOrExpToExpression(newCanBeRef, exp1, null);
		}
		public void updateRefOrExpToExpression(boolean newCanBeRef, Expression exp1, Expression exp2) {
			if(canBeRef() && newCanBeRef == false) {
				convertRefOrExpToExpression(exp1);
				convertRefOrExpToExpression(exp2);
			}
			updateMode(newCanBeRef);
		}
		
		public void updateMode(boolean canBeRef) {
			assertTrue(mode != RefOrExpMode.REF);
			mode = canBeRef ? RefOrExpMode.REF_OR_EXP : RefOrExpMode.EXP;
		}
		
	}
	
	protected RefOrExpParse refOrExp(RefOrExpMode mode, boolean breakRule, Expression exp) {
		return new RefOrExpParse(mode, breakRule, exp);
	}
	
	protected RefOrExpParse refOrExp(boolean canBeRef, boolean breakRule, Expression exp) {
		return refOrExp(canBeRef ? RefOrExpMode.REF_OR_EXP : RefOrExpMode.EXP, breakRule, exp);
	}
	
	protected RefOrExpParse expConnect(Expression exp) {
		return refOrExpConnect(RefOrExpMode.EXP, exp, null);
	}
	
	protected RefOrExpParse refConnect(Expression exp) {
		return refOrExpConnect(RefOrExpMode.REF, exp, null);
	}
	
	protected RefOrExpParse refOrExpConnect(RefOrExpMode mode, Expression exp) {
		return refOrExpConnect(mode, exp, null);
	}
	protected RefOrExpParse refOrExpConnect(RefOrExpMode mode, Expression exp, LexElement afterStarOp) {
		assertNotNull(mode);
		if(mode == RefOrExpMode.EXP) {
			if(exp.getData() != DeeParser_Decls.PARSED_STATUS) {
				exp = connect(exp);
			}
		} else { // This means the node must go through conversion process
			if(afterStarOp != null) {
				exp.setData(afterStarOp);
			} else {
				exp.setData(mode);
			}
		}
		return new RefOrExpParse(mode, false, exp);
	}
	
	protected class RefOrExpFullResult {
		public final RefOrExpMode mode;
		public final Resolvable resolvable;
		
		public RefOrExpFullResult(RefOrExpMode mode, Resolvable resolvable) {
			this.mode = mode;
			this.resolvable = resolvable;
			if(resolvable != null) {
				assertTrue((resolvable.getData() == PARSED_STATUS) == (mode != RefOrExpMode.REF_OR_EXP));
			}
		}
		
		public boolean isReference() {
			return mode == RefOrExpMode.REF;
		}
		
		public Expression getExpression() {
			assertTrue(mode != RefOrExpMode.REF);
			return (Expression) resolvable;
		}
		
		public Reference getReference() {
			assertTrue(isReference());
			return (Reference) resolvable;
		}
	}
	
	protected RefOrExpParse parseReferenceStartOrExpression(int precedenceLimit, boolean canBeRef, boolean isAtStart) {
		RefOrExpParse refOrExp = parseUnaryExpression(canBeRef, isAtStart);
		if(refOrExp.shouldStopRule()) {
			return refOrExp;
		}
		
		return parseReferenceStartOrExpression_RoEStart(precedenceLimit, refOrExp.getExp(), refOrExp.canBeRef());
	}
	
	public Expression parseUnaryExpression() {
		return parseUnaryExpression(false).exp;
	}
	
	protected RefOrExpParse parseUnaryExpression(boolean canBeRef) {
		return parseUnaryExpression(canBeRef, false);
	}
	
	protected RefOrExpParse parseUnaryExpression(boolean canBeRef, boolean isRefOrExpStart) {
		RefOrExpParse prefixExp = parsePrefixExpression(canBeRef, isRefOrExpStart);
		if(prefixExp.shouldStopRule())
			return prefixExp;
		
		return parsePostfixExpression(prefixExp.getExp(), prefixExp.canBeRef());
	}
	
	protected RefOrExpParse parsePostfixExpression(Expression exp, boolean canBeRef) {
		RefOrExpResultHelper roeResult = new RefOrExpResultHelper(canBeRef);
		switch (lookAheadGrouped()) {
		case DECREMENT:
		case INCREMENT: {
			roeResult.updateRefOrExpToExpression(false, exp);
			RefOrExpParse refOrExp = expConnect(parsePostfixExpression(exp));
			return parsePostfixExpression(refOrExp.getExp(), false);
		}
		case OPEN_PARENS: {
			roeResult.updateRefOrExpToExpression(false, exp);
			RefOrExpParse refOrExp = expConnect(parseCallExpression(exp));
			return parsePostfixExpression(refOrExp.getExp(), false);
		} case OPEN_BRACKET: {
			RefOrExpParse refOrExp = parseBracketList(exp, canBeRef);
			if(refOrExp.mode == RefOrExpMode.REF) {
				return refOrExp;
			}
			return parsePostfixExpression(refOrExp.getExp(), refOrExp.canBeRef());
		}
		case POW: {
			roeResult.updateRefOrExpToExpression(false, exp);
			return parseInfixOperator(exp, InfixOpType.POW, false);
		}
		case DOT: {
			assertTrue(canBeRef == false); // Because exp argument should be unambiguously an expression
			consumeLookAhead();
			RefIdentifier qualifiedId = parseRefIdentifier();
			// TODO: remove cast
			Reference ref = connect(new RefQualified((IQualifierNode)exp, qualifiedId, srToCursor(exp.getStartPos())));
			ref = parseReference_ReferenceStart(ref, true).ref; // continue parsing exp even with balance broken
			return parsePostfixExpression(connect(new ExpReference(ref, ref.getSourceRange())), false);
		}
		default:
			return refOrExp(canBeRef, false, exp);
		}
	}
	
	public Expression parseSimpleLiteral() {
		switch (lookAheadGrouped()) {
		case KW_TRUE: case KW_FALSE:
			Token token = consumeLookAhead();
			return connect(new ExpLiteralBool(token.type == DeeTokens.KW_TRUE, srToCursor(lastLexElement)));
		case KW_THIS:
			consumeLookAhead();
			return connect(new ExpThis(srToCursor(lastLexElement)));
		case KW_SUPER:
			consumeLookAhead();
			return connect(new ExpSuper(srToCursor(lastLexElement)));
		case KW_NULL:
			consumeLookAhead();
			return connect(new ExpNull(srToCursor(lastLexElement)));
		case DOLLAR:
			consumeLookAhead();
			return connect(new ExpArrayLength(srToCursor(lastLexElement)));
			
		case KW___LINE__:
			return connect(new ExpLiteralInteger(consumeLookAhead(), srToCursor(lastLexElement)));
		case KW___FILE__:
			return connect(new ExpLiteralString(consumeLookAhead(), srToCursor(lastLexElement)));
		case INTEGER:
			return connect(new ExpLiteralInteger(consumeLookAhead(), srToCursor(lastLexElement)));
		case CHARACTER: 
			return connect(new ExpLiteralChar(consumeLookAhead(), srToCursor(lastLexElement)));
		case FLOAT:
			return connect(new ExpLiteralFloat(consumeLookAhead(), srToCursor(lastLexElement)));
		case STRING:
			return parseStringLiteral();
		default:
			return null;
		}
	}
	
	protected RefOrExpParse parsePrefixExpression(boolean canBeRef, boolean isRefOrExpStart) {
		Expression simpleLiteral = parseSimpleLiteral();
		if(simpleLiteral != null) {
			return expConnect(simpleLiteral);
		}
		
		switch (lookAheadGrouped()) {
		case AND:
		case INCREMENT:
		case DECREMENT:
		case STAR:
		case MINUS:
		case PLUS:
		case NOT:
		case CONCAT:
		case KW_DELETE:
			Token prefixExpToken = consumeLookAhead();
			PrefixOpType prefixOpType = PrefixOpType.tokenToPrefixOpType(prefixExpToken.type);
			
			if(prefixExpToken.type == DeeTokens.STAR && canBeRef && !isRefOrExpStart) {
				
				LexElement data = lookAheadElement();
				RefOrExpParse refOrExp = parseUnaryExpression(canBeRef);
				RefOrExpMode mode = refOrExp.mode;
				
				if(refOrExp.mode == null) {
					mode = RefOrExpMode.REF;
				}
				
				Expression exp = refOrExp.getExp_NoRuleContinue();
				return refOrExpConnect(mode, new ExpPrefix(prefixOpType, exp, srToCursor(prefixExpToken)), data);
				
			} else {
				canBeRef = false;
				RefOrExpParse refOrExp = parseUnaryExpression(canBeRef);
				if(refOrExp.mode == null) {
					reportErrorExpectedRule(RULE_EXPRESSION);
				}
				
				return expConnect(new ExpPrefix(prefixOpType, refOrExp.exp, srToCursor(prefixExpToken)));
			}
			
		case OPEN_PARENS:
			return expConnect(parseParenthesesExp());
		case OPEN_BRACKET:
			return parseArrayLiteral(canBeRef);
		case KW_ASSERT:
			return expConnect(parseAssertExpression());
		case KW_MIXIN:
			return expConnect(parseMixinExpression());
		case KW_IMPORT:
			return expConnect(parseImportExpression());
		case KW_TYPEID:
			return expConnect(parseTypeIdExpression());
		case KW_NEW:
			return parseNewExpression();
		case KW_CAST:
			return parseCastExpression();
		default:
			Reference ref = parseReference(true); /*BUG here breakRule from ref*/
			if(ref == null) {
				return refOrExp(null, false, null);
			}
			
			RefOrExpMode mode = canBeRef && isRefOrExpStart ? RefOrExpMode.REF_OR_EXP : RefOrExpMode.EXP;
			if(parsesAsTypeRef(ref)) {
				if(canBeRef && isRefOrExpStart) {
					mode = RefOrExpMode.REF;
				} else {
					addError(ParserErrorTypes.TYPE_USED_AS_EXP_VALUE, ref.getSourceRange(), null);
				}
			}
			return refOrExpConnect(mode, new ExpReference(ref, ref.getSourceRange()));
		}
	}
	
	/** Returns true if the given ref can only be a reference to a type (due to the parsed grammar rules). */
	protected static boolean parsesAsTypeRef(Reference ref) {
		switch (ref.getNodeType()) {
		case REF_PRIMITIVE:
		case REF_TYPEOF:
			return true;
		case REF_TYPE_DYN_ARRAY:
		case REF_INDEXING:
		case REF_TYPE_POINTER:
			throw assertFail();
		default:
			return false;
		}
	}
	
	public Expression parseArrayLiteral() {
		return parseArrayLiteral(false).getExp();
	}
	protected RefOrExpParse parseArrayLiteral(boolean canBeRef) {
		return parseBracketList(null, canBeRef);
	}
	
	protected RefOrExpParse parseBracketList(Expression calleeExp, final boolean canBeRef) {
		return parseBracketList(calleeExp, new RefOrExpResultHelper(canBeRef));
	}
	protected RefOrExpParse parseBracketList(Expression calleeExp, final RefOrExpResultHelper roeResult) {
		if(tryConsume(DeeTokens.OPEN_BRACKET) == false)
			return refOrExp(null, false, null);
		int nodeStart = lastLexElement.getStartPos();
		
		final boolean isExpIndexing = calleeExp != null;
		final DeeTokens secondLA = isExpIndexing ? DeeTokens.DOUBLE_DOT : DeeTokens.COLON;
		
		ArrayList<Expression> elements = new ArrayList<Expression>();
		ArrayList<MapArrayLiteralKeyValue> mapElements = null;
		
		boolean firstElement = true;
		
		while(true) {
			Expression exp1;
			Expression exp2 = null;
			
			if(firstElement) {
				if(roeResult.canBeRef()) {
					RefOrExpFullResult refOrExp = parseReferenceOrExpression_full(InfixOpType.ASSIGN.precedence);
					if(refOrExp.isReference()) {
						elements.add(refConnect(new ExpReference(refOrExp.getReference(), null)).exp);
						roeResult.mode = RefOrExpMode.REF;
						break;
					} else {
						exp1 = refOrExp.getExpression();
					}
				} else {
					exp1 = parseAssignExpression();
				}
				
				if(exp1 == null && lookAhead() != DeeTokens.COMMA && lookAhead() != secondLA) {
					if(isExpIndexing) {
						consumeExpectedToken(DeeTokens.CLOSE_BRACKET);
						return refOrExpConnect(roeResult.mode, new ExpSlice(calleeExp, srToCursor(calleeExp)));
					}
					
					break; // Empty array literal
				}
				exp1 = nullExpToMissing(exp1, true);
				
				if(lookAhead() == DeeTokens.COMMA) {
					roeResult.updateRefOrExpToExpression(false, calleeExp, exp1);
				} else if(!isExpIndexing && tryConsume(DeeTokens.COLON)) {
					roeResult.updateRefOrExpToExpression(false, exp1);
					exp2 = parseAssignExpression_toMissing(true);
					mapElements = new ArrayList<MapArrayLiteralKeyValue>();
				} else if(isExpIndexing && tryConsume(DeeTokens.DOUBLE_DOT)) {
					roeResult.updateRefOrExpToExpression(false, calleeExp, exp1);
					exp2 = parseAssignExpression_toMissing(true);
					
					consumeExpectedToken(DeeTokens.CLOSE_BRACKET);
					return expConnect(new ExpSlice(calleeExp, exp1, exp2, srToCursor(calleeExp)));
				}
			} else {
				exp1 = parseAssignExpression_toMissing(true);
				
				if(mapElements != null ) {
					assertTrue(roeResult.mode == RefOrExpMode.EXP);
					if(consumeExpectedToken(DeeTokens.COLON) != null) {
						exp2 = parseAssignExpression_toMissing(true);
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
		
		consumeExpectedToken(DeeTokens.CLOSE_BRACKET);

		if(mapElements != null ) {
			return expConnect(new ExpLiteralMapArray(arrayView(mapElements), srToCursor(nodeStart)));
		} else if(calleeExp == null) {
			return refOrExpConnect(roeResult.mode, new ExpLiteralArray(arrayView(elements), srToCursor(nodeStart)));
		}
		return refOrExpConnect(roeResult.mode, new ExpIndex(calleeExp, arrayView(elements), srToCursor(calleeExp)));
	}
	
	public ExpPostfix parsePostfixExpression(Expression exp) {
		Token op = consumeLookAhead();
		return new ExpPostfix(exp, PostfixOpType.tokenToPrefixOpType(op.type), srToCursor(exp));
	}
	
	protected RefOrExpParse parseReferenceStartOrExpression_RoEStart(int precedenceLimit, final Expression leftExp, 
		final boolean canBeRef) {
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
			return refOrExp(canBeRef, false, leftExp);
		}
		
		// If lower precedence it can't be parsed to right expression, 
		// instead this expression must become left children of new parent
		if(infixOpAhead.precedence < precedenceLimit) 
			return refOrExp(canBeRef, false, leftExp);
		
		RefOrExpParse refOrExp = parseInfixOperator(leftExp, infixOpAhead, canBeRef);
		if(refOrExp.shouldStopRule()) {
			return refOrExp;
		}
		
		Expression newLeftExp = refOrExp.getExp();
		return parseReferenceStartOrExpression_RoEStart(precedenceLimit, newLeftExp, refOrExp.canBeRef());
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
	
	public RefOrExpParse parseInfixOperator(final Expression leftExp, InfixOpType opType, final boolean canBeRef) {
		
		consumeLookAhead();
		if(opType == InfixOpType.NOT_IS || opType == InfixOpType.NOT_IN) {
			consumeLookAhead(); // consume second token
		}
		
		RefOrExpResultHelper roeResult = new RefOrExpResultHelper(canBeRef);
		LexElement afterStarOp = null;
		
		if(opType != InfixOpType.MUL) {
			roeResult.updateRefOrExpToExpression(false, leftExp);
			
			checkValidAssociativity(leftExp, opType);
		} else {
			assertTrue(lastLexElement.getType() == DeeTokens.STAR);
			afterStarOp = lookAheadElement();
		}
		
		Expression middleExp = null;
		Expression rightExp = null;
		
		parsing: {
			if(opType == InfixOpType.CONDITIONAL) {
				middleExp = parseExpression();
				if(middleExp == null) {
					reportErrorExpectedRule(RULE_EXPRESSION);
				}
				if(consumeExpectedToken(DeeTokens.COLON) == null) {
					break parsing;
				}
			}
			
			int rightExpPrece = getPrecedenceForInfixOpRightExp(opType).precedence;
			RefOrExpParse rightExpResult = parseReferenceStartOrExpression(rightExpPrece, roeResult.canBeRef(), false);
			
			if(rightExpResult.exp == null) {
				if(roeResult.mode == RefOrExpMode.REF_OR_EXP) {
					roeResult.mode = RefOrExpMode.REF;
				} else {
					reportErrorExpectedRule(RULE_EXPRESSION);
				}
			} else {
				roeResult.updateRefOrExpToExpression(rightExpResult.mode != RefOrExpMode.EXP, leftExp);
				roeResult.mode = rightExpResult.mode;
				
				rightExp = rightExpResult.getExp_NoRuleContinue();
				checkValidAssociativity(rightExp, opType);
			}
		}
		
		if(opType == InfixOpType.CONDITIONAL) {
			return expConnect(new ExpConditional(leftExp, middleExp, rightExp, srToCursor(leftExp)));
		}
		
		ExpInfix expInfix = new ExpInfix(leftExp, opType, rightExp, srToCursor(leftExp));
		return refOrExpConnect(roeResult.mode, expInfix, afterStarOp);
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
	
	protected static Expression convertRefOrExpToExpression(Expression exp) {
		if(exp == null) 
			return null;
		exp.accept(new ASTDefaultVisitor() {
			@Override
			public boolean preVisit(ASTNeoNode node) {
				if(node.getData() == PARSED_STATUS) {
					return false;
				}
				switch (node.getNodeType()) {
				case EXP_INFIX:
				case EXP_PREFIX:
					node.removeData(LexElement.class);
					break;
				case EXP_REFERENCE:
				case EXP_LITERAL_ARRAY:
				case EXP_SLICE:
				case EXP_INDEX:
					node.removeData(RefOrExpMode.class);
					break;
				default:
					throw assertFail();
				}
				node.setData(DeeParser_Decls.PARSED_STATUS);
				return true;
			}
		});
		return exp;
	}
	
	protected Reference convertRefOrExpToReference(Expression exp) {
		return convertRefOrExpToReference(null, exp);
	}
	
	protected Reference convertRefOrExpToReference(Reference leftRef, Expression exp) {
		if(exp == null) {
			return leftRef;
		}
		
		switch (exp.getNodeType()) {
		case EXP_REFERENCE: {
			assertTrue(leftRef == null);
			
			exp.resetData(null);
			Reference ref = ((ExpReference) exp).ref;
			ref.detachFromParent();
			return ref;
		}
		case EXP_INFIX: {
			ExpInfix expInfix = (ExpInfix) exp;
			assertTrue(leftRef == null);
			
			assertTrue(expInfix.kind == InfixOpType.MUL);
			leftRef = convertRefOrExpToReference(expInfix.leftExp);
			
			assertTrue(expInfix.getData() instanceof LexElement);
			LexElement afterStarToken = (LexElement) expInfix.getData();
			SourceRange sr = srNodeStart(leftRef, afterStarToken.getFullRangeStartPos());
			
			leftRef = connect(new RefTypePointer(leftRef, sr));
			
			return convertRefOrExpToReference(leftRef, expInfix.rightExp);
		}
		case EXP_INDEX: {
			ExpIndex expIndex = (ExpIndex) exp;
			assertTrue(expIndex.args.size() == 1);
			
			leftRef = convertRefOrExpToReference(leftRef, expIndex.indexee);
			return convertToRefIndexing(leftRef, expIndex, expIndex.args.get(0));
		}
		case EXP_SLICE: {
			ExpSlice expSlice = (ExpSlice) exp;
			assertTrue(expSlice.from == null && expSlice.to == null);
			
			leftRef = convertRefOrExpToReference(leftRef, expSlice.slicee);
			return connect(new RefTypeDynArray(leftRef, srNodeStart(leftRef, expSlice.getEndPos())));
		}
		case EXP_PREFIX: {
			assertTrue(leftRef != null);
			ExpPrefix expPrefix = (ExpPrefix) exp;
			assertTrue(expPrefix.kind == PrefixOpType.REFERENCE);
			
			LexElement afterStarToken = assertNotNull_((LexElement) expPrefix.getData());
			SourceRange sr = srNodeStart(leftRef, afterStarToken.getFullRangeStartPos());
			
			leftRef = connect(new RefTypePointer(leftRef, sr));
			return convertRefOrExpToReference(leftRef, expPrefix.exp);
		}
		case EXP_LITERAL_ARRAY: {
			assertTrue(leftRef != null);
			ExpLiteralArray expLiteralArray = (ExpLiteralArray) exp;
			assertTrue(expLiteralArray.getData() instanceof RefOrExpMode);
			
			assertTrue(expLiteralArray.elements.size() <= 1);
			if(expLiteralArray.elements.size() == 0) {
				return connect(new RefTypeDynArray(leftRef, srNodeStart(leftRef, exp.getEndPos())));
			} else {
				return convertToRefIndexing(leftRef, expLiteralArray, expLiteralArray.elements.get(0));
			}
		}
		
		default:
			throw assertFail();
		}
	}
	
	public Reference convertToRefIndexing(Reference leftRef, Expression exp, Expression indexArgExp) {
		Resolvable indexArg;
		if(indexArgExp.getData() == RefOrExpMode.REF) {
			// argument can only be interpreted as reference
			indexArg = ((ExpReference) indexArgExp).ref;
			indexArg.detachFromParent();
		} else if(indexArgExp.getData() == DeeParser_Decls.PARSED_STATUS) {
			// argument can only be interpreted as expression
			indexArg = indexArgExp;
			indexArg.detachFromParent();
		} else if(indexArgExp.getData() == RefOrExpMode.REF_OR_EXP || indexArgExp.getData() instanceof LexElement) {
			// argument is ambiguous, so convert it to reference
			indexArg = convertRefOrExpToReference(indexArgExp);
		} else {
			throw assertFail();
		}
		
		return connect(new RefIndexing(leftRef, indexArg, srNodeStart(leftRef, exp.getEndPos())));
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
	
	protected ExpCall parseCallExpression(Expression callee) {
		consumeLookAhead(DeeTokens.OPEN_PARENS);
		
		ArrayList<Expression> args = parseExpArgumentList(DeeTokens.CLOSE_PARENS).list;
		return connect(new ExpCall(callee, arrayView(args), srToCursor(callee)));
	}
	
	public static class ArgumentListParseResult<T> extends RuleParseResult<ArrayList<T>> {
		public final ArrayList<T> list;
		
		public ArgumentListParseResult(boolean properlyTerminated, ArrayList<T> argList) {
			super(!properlyTerminated, argList);
			this.list = argList;
		}
	}
	
	protected ArgumentListParseResult<Expression> parseExpArgumentList(DeeTokens tokenLISTCLOSE) {
		return CoreUtil.blindCast(parseArgumentList(false, DeeTokens.COMMA, tokenLISTCLOSE));
	}
	protected ArgumentListParseResult<Resolvable> parseArgumentList(boolean parseRefOrExp, 
		DeeTokens tokenSEPARATOR, DeeTokens tokenLISTCLOSE) {
		
		ArrayList<Resolvable> args = new ArrayList<Resolvable>();
		
		boolean first = true;
		while(true) {
			Resolvable arg = parseRefOrExp ? parseReferenceOrAssignExpression(true) : parseAssignExpression();
			
			if(first && arg == null && lookAhead() != tokenSEPARATOR) {
				break;
			}
			arg = parseRefOrExp ? nullRoEToMissing(arg, true) : nullExpToMissing((Expression) arg, true);
			args.add(arg);
			first = false;
			
			if(tryConsume(tokenSEPARATOR)) {
				continue;
			}
			break;
		}
		boolean properlyTerminated = consumeExpectedToken(tokenLISTCLOSE) != null;
		return new ArgumentListParseResult<Resolvable>(properlyTerminated, args);
	}
	
	public Expression parseParenthesesExp() {
		if(!tryConsume(DeeTokens.OPEN_PARENS))
			return null;
		int nodeStart = lastLexElement.getStartPos();
		
		Resolvable resolvable = parseReferenceOrExpression(false);
		if(resolvable == null) {
			resolvable = nullExpToMissing((Expression) resolvable, true);
		}
		
		if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
			if(resolvable instanceof Reference && lookAhead() != DeeTokens.DOT) {
				addError(ParserErrorTypes.TYPE_USED_AS_EXP_VALUE, resolvable.getSourceRange(), null);
			}
		}
		
		return connect(new ExpParentheses(resolvable, srToCursor(nodeStart)));
	}
	
	public ExpAssert parseAssertExpression() {
		if(tryConsume(DeeTokens.KW_ASSERT) == false)
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		Expression exp = null;
		Expression msg = null;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseAssignExpression_toMissing(true);
			if(tryConsume(DeeTokens.COMMA)) {
				msg = parseAssignExpression_toMissing(true);
			}
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		
		return connect(new ExpAssert(exp, msg, srToCursor(nodeStart)));
	}
	
	public ExpImportString parseImportExpression() {
		if(tryConsume(DeeTokens.KW_IMPORT) == false)
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		Expression exp = parseAssignExpressionWithParens();
		return connect(new ExpImportString(exp, srToCursor(nodeStart)));
	}
	
	public ExpMixinString parseMixinExpression() {
		if(tryConsume(DeeTokens.KW_MIXIN) == false)
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		Expression exp = parseAssignExpressionWithParens();
		return connect(new ExpMixinString(exp, srToCursor(nodeStart)));
	}
	
	protected Expression parseAssignExpressionWithParens() {
		Expression exp = null;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseAssignExpression_toMissing(true);
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		return exp;
	}
	
	public ExpTypeId parseTypeIdExpression() {
		if(tryConsume(DeeTokens.KW_TYPEID) == false)
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		Reference ref = null;
		Expression exp = null;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			Resolvable resolvable = nullRoEToMissing(parseReferenceOrExpression(true), true);
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
	
	public RefOrExpParse parseNewExpression() {
		if(!tryConsume(DeeTokens.KW_NEW))
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		
		ArrayList<Expression> allocArgs = null;
		Reference type = null;
		ArrayList<Expression> args = null;
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				ArgumentListParseResult<Expression> allocArgsResult = parseExpArgumentList(DeeTokens.CLOSE_PARENS);
				allocArgs = allocArgsResult.list;
				if(allocArgsResult.parseBroken) {
					break parsing;
				}
			}
			type = parseReference_ToMissing(true);
			if(lastLexElement.isMissingElement()) {
				break parsing;
			}
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				args = parseExpArgumentList(DeeTokens.CLOSE_PARENS).list;
			}
		}
		
		return expConnect(new ExpNew(arrayView(allocArgs), type, arrayView(args), srToCursor(nodeStart)));
	}
	
	public RefOrExpParse parseCastExpression() {
		if(!tryConsume(DeeTokens.KW_CAST))
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		
		Reference type = null;
		CastQualifiers qualifier = null;
		Expression exp = null;
		
		parsing: {
			if(consumeExpectedToken(DeeTokens.OPEN_PARENS) == null)
				break parsing;
			
			qualifier = parseCastQualifier();
			if(qualifier == null) {
				type = parseReference_ToMissing(false);
			}
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null)
				break parsing;
			
			exp = nullExpToMissing(parseUnaryExpression(), true);
		}
		
		if(qualifier != null) {
			return expConnect(new ExpCastQual(qualifier, exp, srToCursor(nodeStart)));
		} else {
			return expConnect(new ExpCast(type, exp, srToCursor(nodeStart)));
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