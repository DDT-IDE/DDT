package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTDefaultVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationBasicAttrib;
import dtool.ast.declarations.DeclarationBasicAttrib.EDeclarationAttribute;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationLinkage.Linkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationProtection.Protection;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.declarations.InvalidDeclaration;
import dtool.ast.declarations.InvalidSyntaxElement;
import dtool.ast.definitions.DefUnit.DefUnitTuple;
import dtool.ast.definitions.DefinitionVarFragment;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpArrayLength;
import dtool.ast.expressions.ExpAssert;
import dtool.ast.expressions.ExpCall;
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
import dtool.ast.expressions.ExpNull;
import dtool.ast.expressions.ExpParentheses;
import dtool.ast.expressions.ExpPrefix;
import dtool.ast.expressions.ExpPrefix.PrefixOpType;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSlice;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.ExpTypeId;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Initializer;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.Reference;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

public class DeeParser extends AbstractParser {
	
	public DeeParser(String source) {
		super(new DeeLexer(source));
	}
	
	public DeeParser(DeeLexer deeLexer) {
		super(deeLexer);
	}
	
	public static DeeParserResult parse(String source) {
		DeeParser deeParser = new DeeParser(source);
		Module module = deeParser.parseModule();
		return new DeeParserResult(module, deeParser.errors);
	}
	
	public String idTokenToString(LexElement id) {
		return id.isMissingElement() ? null : id.token.source;
	}
	
	// TODO: comments
	public DefUnitTuple defUnitNoComments(LexElement id) {
		return defUnitTuple(id, null);
	}
	
	public DefUnitTuple defUnitTuple(LexElement id, Comment[] comments) {
		return new DefUnitTuple(null, id, comments);
	}
	
	public DeeTokens lookAheadGrouped() {
		return lookAheadToken().type.getGroupingToken();
	}
	
	/* ----------------------------------------------------------------- */
	
	public Module parseModule() {
		DeclarationModule md = parseModuleDeclaration();
		
		ArrayView<ASTNeoNode> members = parseDeclDefs(null);
		assertTrue(lookAhead() == DeeTokens.EOF);
		assertTrue(lookAheadToken().getEndPos() == lexer.source.length());
		
		SourceRange modRange = new SourceRange(0, lexer.source.length());
		
		if(md != null) {
			return connect(new Module(md.getModuleSymbol(), null, md, members, modRange));
		} else {
			return connect(Module.createModuleNoModuleDecl(modRange, "__tests_unnamed" /*BUG here*/, members));
		}
	}
	
	public DeclarationModule parseModuleDeclaration() {
		if(!tryConsume(DeeTokens.KW_MODULE)) {
			return null;
		}
		int nodeStart = lastLexElement.getStartPos();
		
		ArrayList<String> packagesList = new ArrayList<String>(0);
		LexElement moduleId;
		
		while(true) {
			LexElement id = tryConsumeIdentifier();
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packagesList.add(id.token.source);
				id = null;
			} else {
				consumeExpectedToken(DeeTokens.SEMICOLON);
				moduleId = id;
				break;
			}
		}
		assertNotNull(moduleId);
		
		String[] packages = ArrayUtil.createFrom(packagesList, String.class);
		SourceRange modDeclRange = srToCursor(nodeStart);
		return connect(new DeclarationModule(packages, moduleId.token, modDeclRange));
	}
	
	
	public ArrayView<ASTNeoNode> parseDeclDefs(DeeTokens nodeListTerminator) {
		ArrayList<ASTNeoNode> declarations = new ArrayList<ASTNeoNode>();
		while(true) {
			if(lookAhead() == nodeListTerminator) {
				break;
			}
			ASTNeoNode decl = parseDeclaration();
			if(decl == null) { 
				break;
			}
			declarations.add(decl);
		}
		
		return arrayView(declarations);
	}
	
	
	public DeclarationImport parseImportDeclaration() {
		boolean isStatic = false;
		int nodeStart = -1;
		
		if(tryConsume(DeeTokens.KW_STATIC)) { // BUG here
			isStatic = true;
			nodeStart = lastLexElement.getStartPos();
		}
		
		if(!tryConsume(DeeTokens.KW_IMPORT)) {
			return null;
		}
		nodeStart = NewUtils.updateIfNull(nodeStart, lastLexElement.getStartPos());
		
		ArrayList<IImportFragment> fragments = new ArrayList<IImportFragment>();
		do {
			IImportFragment fragment = parseImportFragment();
			assertNotNull(fragment);
			fragments.add(fragment);
		} while(tryConsume(DeeTokens.COMMA));
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		SourceRange sr = srToCursor(nodeStart);
		boolean isTransitive = false;
		
		return connect(
			new DeclarationImport(arrayView(fragments, IImportFragment.class), isStatic, isTransitive, sr));
	}
	
	public IImportFragment parseImportFragment() {
		LexElement aliasId = null;
		ArrayList<String> packages = new ArrayList<String>(0);
		int nodeStartPos = -1;
		
		while(true) {
			LexElement id = tryConsumeIdentifier();
			nodeStartPos = nodeStartPos == -1 ? id.getStartPos() : nodeStartPos;
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packages.add(id.token.source);
			} else if(packages.isEmpty() && tryConsume(DeeTokens.ASSIGN)) { // BUG here
				aliasId = id;
				nodeStartPos = -1;
			} else {
				RefModule refModule = 
					connect(new RefModule(arrayViewS(packages), id.token.source, srToCursor(nodeStartPos))); 
				
				IImportFragment fragment = (aliasId == null) ? 
					connect(new ImportContent(refModule)) : 
					connect(new ImportAlias(defUnitNoComments(aliasId), refModule, srToCursor(aliasId.getStartPos())));
				
				if(tryConsume(DeeTokens.COLON)) {
					return parseSelectiveModuleImport(fragment);
				}
				
				return fragment;
			}
		}
	}
	
	public ImportSelective parseSelectiveModuleImport(IImportFragment fragment) {
		ArrayList<IImportSelectiveSelection> selFragments = new ArrayList<IImportSelectiveSelection>();
		
		do {
			IImportSelectiveSelection importSelSelection = parseImportSelectiveSelection();
			selFragments.add(importSelSelection);
			
		} while(tryConsume(DeeTokens.COMMA));
		
		SourceRange sr = srToCursor(fragment.getStartPos());
		return connect(new ImportSelective(fragment, arrayView(selFragments, IImportSelectiveSelection.class), sr));
	}
	
	public IImportSelectiveSelection parseImportSelectiveSelection() {
		LexElement aliasId = null;
		LexElement id = tryConsumeIdentifier();
		
		if(tryConsume(DeeTokens.ASSIGN)){ // BUG here
			aliasId = id;
			id = tryConsumeIdentifier();
		}
		
		RefImportSelection refImportSelection = connect(new RefImportSelection(idTokenToString(id), sr(id.token)));
		
		if(aliasId == null) {
			return refImportSelection;
		} else {
			return connect(
				new ImportSelectiveAlias(defUnitNoComments(aliasId), refImportSelection, srToCursor(aliasId)));
		}
	}
	
	/* --------------------- DECLARATION --------------------- */
	public static String DECLARATION_RULE = "declaration";
	
	public ASTNeoNode parseDeclaration() {
		return parseDeclaration(true);
	}
	
	/** This rule always returns a node, except only on EOF where it returns null. */
	public ASTNeoNode parseDeclaration(boolean acceptEmptyDecl) {
		while(true) {
			DeeTokens la = assertNotNull_(lookAheadGrouped());
			
			if(la == DeeTokens.EOF) {
				return null;
			}
			
			switch (la) {
			case KW_IMPORT: return parseImportDeclaration();
			
			case KW_MIXIN: return parseDeclarationMixinString();
			
			case KW_EXTERN: return parseDeclarationExternLinkage();
			case KW_ALIGN: return parseDeclarationAlign();
			case KW_PRAGMA: return parseDeclarationPragma();
			case PROTECTION_KW: return parseDeclarationProtection();
			case ATTRIBUTE_KW: 
				if(lookAhead() == DeeTokens.KW_STATIC && lookAhead(/*1 TODO*/) == DeeTokens.KW_IMPORT) { 
					return parseImportDeclaration();
				}
				return parseDeclarationBasicAttrib();
			// @disable keyword?
			case KW_AUTO: // TODO:
				break;
			
			case IDENTIFIER: return parseDeclaration_IdStart();
			case PRIMITIVE_KW: return parseDeclaration_RefPrimitiveStart();
			case DOT: return parseDeclaration_DotStart();
			
			default:
				break;
			}
			
			if(acceptEmptyDecl && tryConsume(DeeTokens.SEMICOLON)) {
				return connect(new DeclarationEmpty(srToCursor(lastLexElement)));
			} else {
				Token badToken = consumeLookAhead();
				reportSyntaxError(DECLARATION_RULE);
				return connect(new InvalidSyntaxElement(badToken));
			}
		}
	}
	
	/* --------------------  reference parsing  --------------------- */
	
	protected RefIdentifier parseRefIdentifier() {
		LexElement id = tryConsumeIdentifier();
		assertTrue(id.token.type == DeeTokens.IDENTIFIER);
		return connect(new RefIdentifier(idTokenToString(id), sr(id.token)));
	}
	
	protected RefPrimitive parseRefPrimitive(DeeTokens primitiveType) {
		Token token = consumeLookAhead(primitiveType);
		return connect(new RefPrimitive(token, sr(token)));
	}
	
	protected RefModuleQualified parseRefModuleQualified() {
		int startPos = consumeLookAhead(DeeTokens.DOT).getStartPos();
		return connect(new RefModuleQualified(parseRefIdentifier(), srToCursor(startPos)));
	}
	
	protected static class RefParseResult { 
		public final Reference ref;
		public final boolean balanceBroken;
		
		public RefParseResult(boolean balanceBroken, Reference ref) {
			this.ref = ref;
			this.balanceBroken = balanceBroken;
		}
		public RefParseResult(Reference ref) {
			this(false, ref);
		}
	}
	
	public Reference parseReference() {
		return parseReference_begin(false).ref;
	}
	
	public Reference parseReference(boolean expressionContext) {
		return parseReference_begin(expressionContext).ref;
	}
	
	protected RefParseResult parseReference_begin(boolean parsingExp) {
		DeeTokens la = lookAheadGrouped();
		
		switch (la) {
		case DOT: return parseReference_ReferenceStart(parseRefModuleQualified(), parsingExp);
		case IDENTIFIER: return parseReference_ReferenceStart(parseRefIdentifier(), parsingExp);
		case PRIMITIVE_KW: return parseReference_ReferenceStart(parseRefPrimitive(lookAhead()), parsingExp);
		
		default:
		return new RefParseResult(null);
		}
	}
	
	protected RefParseResult parseReference_ReferenceStart(Reference leftRef, boolean parsingExp) {
		// Star is multiply infix operator, dont parse as pointer ref
		if(!parsingExp && tryConsume(DeeTokens.STAR)) {
			RefTypePointer pointerRef = connect(new RefTypePointer(leftRef, srToCursor(leftRef.getStartPos())));
			return parseReference_ReferenceStart(pointerRef, parsingExp);
			
		} else if(tryConsume(DeeTokens.DOT)) {
			RefIdentifier qualifiedId = parseRefIdentifier();
			leftRef = connect(new RefQualified(leftRef, qualifiedId, srToCursor(leftRef.getStartPos())));
			if(qualifiedId.name == null) {
				return new RefParseResult(true, leftRef);
			}
			return parseReference_ReferenceStart(leftRef, parsingExp);
			
		} else if(!parsingExp && tryConsume(DeeTokens.OPEN_BRACKET)) {
			if(tryConsume(DeeTokens.CLOSE_BRACKET)) {
				RefTypeDynArray dynArrayRef = connect(new RefTypeDynArray(leftRef, srToCursor(leftRef.getStartPos())));
				return parseReference_ReferenceStart(dynArrayRef, parsingExp);
			} else {
				Resolvable resolvable = parseReferenceOrExpression();
				if(consumeExpectedToken(DeeTokens.CLOSE_BRACKET) == null) {
					// Note: if resolvable == null then this case should always be entered into
					if(resolvable == null) {
						leftRef = new RefTypeDynArray(leftRef, srToCursor(leftRef.getStartPos()));
					} else {
						leftRef = new RefIndexing(leftRef, resolvable, srToCursor(leftRef.getStartPos()));
					}
					return new RefParseResult(true, connect(leftRef));
				}
				assertNotNull(resolvable);
				RefIndexing refIndexing = connect(
					new RefIndexing(leftRef, resolvable, srToCursor(leftRef.getStartPos())));
				return parseReference_ReferenceStart(refIndexing, parsingExp);
			}
		}
		return new RefParseResult(leftRef);
	}
	
	/* ----------------------------------------- */
	
	public static String EXPRESSION_RULE = "expression";
	
	public Expression parseExpression() {
		return parseExpression(0);
	}
	
	protected Expression parseExpression(int precedenceLimit) {
		return parseReferenceOrExpression(precedenceLimit, false, true).getExp_NoRuleContinue();
	}
	
	public Expression parseExpression_ToMissing(boolean reportMissingExpError) {
		return nullExpToMissing(parseExpression(), reportMissingExpError);
	}
	
	protected Expression nullExpToMissing(Expression expAssign, boolean reportMissingExpError) {
		if(expAssign == null) {
			if(reportMissingExpError) {
				reportError(ParserErrorTypes.EXPECTED_RULE, EXPRESSION_RULE, false);
			}
			int nodeStart = lastLexElement.getEndPos();
			expAssign = connect(new MissingExpression(srToCursor(nodeStart)));
		}
		return expAssign;
	}
	
	public Expression parseAssignExpression() {
		return parseExpression(InfixOpType.ASSIGN.precedence);
	}
	
	public Expression parseAssignExpression_toMissing(boolean reportMissingExpError) {
		return nullExpToMissing(parseAssignExpression(), reportMissingExpError);
	}
	
	public Resolvable parseReferenceOrExpression() {
		return parseReferenceOrExpression_full(0, true).resolvable;
	}
	
	public static enum RefOrExpMode { REF, EXP, REF_OR_EXP }
	
	protected static class RefOrExpParse {
		
		public final RefOrExpMode mode;
		private final Expression exp;
		
		public RefOrExpParse(RefOrExpMode mode, Expression exp) {
			this.mode = mode;
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
	
	protected RefOrExpParse refOrExp(RefOrExpMode mode, Expression exp) {
		return new RefOrExpParse(mode, exp);
	}
	
	protected RefOrExpParse refOrExp(boolean canBeRef, Expression exp) {
		return refOrExp(canBeRef ? RefOrExpMode.REF_OR_EXP : RefOrExpMode.EXP, exp);
	}
	
	protected RefOrExpParse expConnect(Expression exp) {
		return refOrExpConnect(RefOrExpMode.EXP, exp, null);
	}
	
	protected RefOrExpParse refConnect(Expression exp) {
		return refOrExpConnect(RefOrExpMode.REF, exp, null);
	}
	
	protected RefOrExpParse refOrExpConnect(boolean canBeRef, Expression exp) {
		return refOrExpConnect(canBeRef ? RefOrExpMode.REF_OR_EXP : RefOrExpMode.EXP, exp, null);
	}
	
	protected RefOrExpParse refOrExpConnect(RefOrExpMode mode, Expression exp, LexElement afterStarOp) {
		assertNotNull(mode);
		if(mode == RefOrExpMode.EXP) {
			if(exp.getData() != DeeParser.PARSED_STATUS) {
				exp = connect(exp);
			}
		} else { // This means the node must go through conversion process
			if(afterStarOp != null) {
				exp.setData(afterStarOp);
			} else {
				exp.setData(mode);
			}
		}
		return new RefOrExpParse(mode, exp);
	}
	
	protected boolean updateRefOrExpToExpression(boolean canBeRef, Expression leftExp, boolean newCanBeRef) {
		if(canBeRef && newCanBeRef == false && leftExp != null) {
			convertRefOrExpToExpression(leftExp);
		}
		return newCanBeRef;
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

	public RefOrExpFullResult parseReferenceOrExpression_full(int precedenceLimit, boolean ambiguousToRef) {
		// canBeRef will indicate whether the expression parsed so far could also have been parsed as a reference.
		// It is essential that every function call checks and updates the value of this variable before
		// consuming additional tokens from the stream.
		
		RefOrExpParse refOrExp = parseReferenceOrExpression(precedenceLimit, true, true);
		if(refOrExp.mode == null)
			return new RefOrExpFullResult(null, null);
		
		if(refOrExp.mode == RefOrExpMode.EXP) {
			return new RefOrExpFullResult(RefOrExpMode.EXP, refOrExp.getExp());
		} else if(refOrExp.mode == RefOrExpMode.REF || ambiguousToRef) {
			// The expression we parse should actually have been parsed as a reference, so convert it:
			Reference startRef = convertRefOrExpToReference(refOrExp.getExp_NoRuleContinue());
			// And resume parsing as ref
			Reference ref = parseReference_ReferenceStart(startRef, false).ref;
			return new RefOrExpFullResult(RefOrExpMode.REF, ref);
		} else {
			return new RefOrExpFullResult(RefOrExpMode.REF_OR_EXP, refOrExp.getExp());
		}
	}
	
	protected RefOrExpParse parseReferenceOrExpression_notStart(int precedenceLimit, boolean canBeRef) {
		return parseReferenceOrExpression(precedenceLimit, canBeRef, false);
	}
	
	protected RefOrExpParse parseReferenceOrExpression(int precedenceLimit, boolean canBeRef, boolean isRoEStart) {
		RefOrExpParse refOrExp = parseUnaryExpression(canBeRef, isRoEStart);
		
		if(refOrExp.mode == null || refOrExp.mode == RefOrExpMode.REF) {
			return refOrExp;
		}
		
		return parseReferenceOrExpression_ExpStart(precedenceLimit, refOrExp.getExp(), refOrExp.canBeRef());
	}
	
	public RefOrExpParse parseUnaryExpression(boolean canBeRef) {
		return parseUnaryExpression(canBeRef, false);
	}
	
	public RefOrExpParse parseUnaryExpression(boolean canBeRef, boolean isRefOrExpStart) {
		RefOrExpParse prefixExp = parsePrefixExpression(canBeRef, isRefOrExpStart);
		if(prefixExp.mode == null || prefixExp.mode == RefOrExpMode.REF)
			return prefixExp;
		
		return parsePostfixExpression(prefixExp.getExp(), prefixExp.canBeRef());
	}
	
	public RefOrExpParse parsePostfixExpression(Expression exp, boolean canBeRef) {
		switch (lookAheadGrouped()) {
		case OPEN_PARENS: {
			updateRefOrExpToExpression(canBeRef, exp, false);
			RefOrExpParse refOrExp = expConnect(parseCallExpression(exp));
			return parsePostfixExpression(refOrExp.getExp(), false);
		} case OPEN_BRACKET: {
			RefOrExpParse refOrExp = parseBracketList(exp, canBeRef);
			if(refOrExp.mode == RefOrExpMode.REF) {
				return refOrExp;
			}
			return parsePostfixExpression(refOrExp.getExp(), refOrExp.canBeRef());
		}
		default:
			return refOrExp(canBeRef, exp);
		}
	}
	
	public RefOrExpParse parsePrefixExpression(boolean canBeRef, boolean isRefOrExpStart) {
		switch (lookAheadGrouped()) {
		case KW_TRUE: case KW_FALSE: {
			Token token = consumeLookAhead();
			return expConnect(new ExpLiteralBool(token.type == DeeTokens.KW_TRUE, srToCursor(lastLexElement)));
		}
		case KW_THIS:
			consumeLookAhead();
			return expConnect(new ExpThis(srToCursor(lastLexElement)));
		case KW_SUPER:
			consumeLookAhead();
			return expConnect(new ExpSuper(srToCursor(lastLexElement)));
		case KW_NULL:
			consumeLookAhead();
			return expConnect(new ExpNull(srToCursor(lastLexElement)));
		case DOLLAR:
			consumeLookAhead();
			return expConnect(new ExpArrayLength(srToCursor(lastLexElement)));
			
		case KW___LINE__:
			return expConnect(new ExpLiteralInteger(consumeLookAhead(), srToCursor(lastLexElement)));
		case KW___FILE__:
			return expConnect(new ExpLiteralString(consumeLookAhead(), srToCursor(lastLexElement)));
		case INTEGER:
			return expConnect(new ExpLiteralInteger(consumeLookAhead(), srToCursor(lastLexElement)));
		case CHARACTER: 
			return expConnect(new ExpLiteralChar(consumeLookAhead(), srToCursor(lastLexElement)));
		case FLOAT:
			return expConnect(new ExpLiteralFloat(consumeLookAhead(), srToCursor(lastLexElement)));
		case STRING:
			return expConnect(parseStringLiteral());
			
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
					reportErrorExpectedRule(EXPRESSION_RULE);
				}
				
				return expConnect(new ExpPrefix(prefixOpType, refOrExp.exp, srToCursor(prefixExpToken)));
			}
			
		case OPEN_PARENS:
			return expConnect(parseParenthesesExp());
		case OPEN_BRACKET:
			return parseArrayLiteral(canBeRef);
		case KW_ASSERT:
			return expConnect(parseAssertExp());
		case KW_MIXIN:
			return expConnect(parseMixinExp());
		case KW_IMPORT:
			return expConnect(parseImportExp());
		case KW_TYPEID:
			return expConnect(parseTypeIdExp());
		default:
			Reference ref = parseReference(true);
			if(ref == null) {
				return refOrExp(null, null);
			}
			
			if(isBuiltinTypeRef(ref)) {
				if(isRefOrExpStart && canBeRef) {
					return refConnect(new ExpReference(ref, ref.getSourceRange()));
				}
				addError(ParserErrorTypes.TYPE_USED_AS_EXP_VALUE, ref.getSourceRange(), null);
			}
			return refOrExpConnect(canBeRef && isRefOrExpStart, new ExpReference(ref, ref.getSourceRange()));
		}
	}
	
	protected static boolean isBuiltinTypeRef(Reference ref) {
		switch (ref.getNodeType()) {
		case REF_PRIMITIVE:
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
	
	protected RefOrExpParse parseBracketList(Expression calleeExp, boolean canBeRef) {
		if(tryConsume(DeeTokens.OPEN_BRACKET) == false)
			return refOrExp(null, null);
		int nodeStart = lastLexElement.getStartPos();
		
		final boolean isExpIndexing = calleeExp != null;
		final DeeTokens secondLA = isExpIndexing ? DeeTokens.DOUBLE_DOT : DeeTokens.COLON;
		
		ArrayList<Expression> elements = new ArrayList<Expression>();
		ArrayList<MapArrayLiteralKeyValue> mapElements = null;
		
		boolean firstElement = true;
		Expression firstExp = null;
		
		while(true) {
			Expression exp1;
			Expression exp2 = null;
			
			if(firstElement) {
				if(canBeRef) {
					RefOrExpFullResult refOrExp = parseReferenceOrExpression_full(InfixOpType.ASSIGN.precedence, false);
					if(refOrExp.isReference()) {
						elements.add(refConnect(new ExpReference(refOrExp.getReference(), null)).exp);
						consumeExpectedToken(DeeTokens.CLOSE_BRACKET);
						
						return refConnect(createBracketListNode(calleeExp, elements, nodeStart));
					} else {
						firstExp = exp1 = nullExpToMissing(refOrExp.getExpression(), false);
					}
				} else {
					exp1 = parseAssignExpression_toMissing(false);
				}
				
				if(exp1.getNodeType() == ASTNodeTypes.MISSING_EXPRESSION) {
					if(lookAhead() == DeeTokens.COMMA || lookAhead() == secondLA) {
						reportError(ParserErrorTypes.EXPECTED_RULE, EXPRESSION_RULE, false);
					} else {
						consumeExpectedToken(DeeTokens.CLOSE_BRACKET);
						if(isExpIndexing) {
							return refOrExpConnect(canBeRef, new ExpSlice(calleeExp, srToCursor(calleeExp)));
						}
						break;
					}
				}
				if(!isExpIndexing && tryConsume(DeeTokens.COLON)) {
					canBeRef = updateRefOrExpToExpression(canBeRef, firstExp, false);
					exp2 = parseAssignExpression_toMissing(true);
					mapElements = new ArrayList<MapArrayLiteralKeyValue>();
				} else if(isExpIndexing && tryConsume(DeeTokens.DOUBLE_DOT)) {
					updateRefOrExpToExpression(canBeRef, calleeExp, false);
					canBeRef = updateRefOrExpToExpression(canBeRef, firstExp, false);
					exp2 = parseAssignExpression_toMissing(true);
					
					consumeExpectedToken(DeeTokens.CLOSE_BRACKET);
					return refOrExpConnect(false, new ExpSlice(calleeExp, exp1, exp2, srToCursor(calleeExp)));
				}
			} else {
				exp1 = parseAssignExpression_toMissing(true);
				
				if(mapElements != null ) {
					assertTrue(canBeRef == false);
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
				updateRefOrExpToExpression(canBeRef, calleeExp, false);
				canBeRef = updateRefOrExpToExpression(canBeRef, firstExp, false);
				continue;
			}
			consumeExpectedToken(DeeTokens.CLOSE_BRACKET);
			break;
		}
		
		if(mapElements != null ) {
			return expConnect(new ExpLiteralMapArray(arrayView(mapElements), srToCursor(nodeStart)));
		}
		return refOrExpConnect(canBeRef, createBracketListNode(calleeExp, elements, nodeStart));
	}
	
	public Expression createBracketListNode(Expression calleeExp, ArrayList<Expression> elements, int nodeStart) {
		return calleeExp != null ?
			new ExpIndex(calleeExp, arrayView(elements), srToCursor(calleeExp)) :
			new ExpLiteralArray(arrayView(elements), srToCursor(nodeStart));
	}
	
	protected RefOrExpParse parseReferenceOrExpression_ExpStart(int precedenceLimit, final Expression leftExp, 
		boolean canBeRef) {
		DeeTokens gla = lookAheadGrouped();
		
		InfixOpType infixOp = InfixOpType.tokenToInfixOpType(gla);
		if(lookAhead() == DeeTokens.NOT) {
			if(lookAheadElement(1).getType() == DeeTokens.KW_IS) {
				infixOp = InfixOpType.NOT_IS;
			} else if(lookAheadElement(1).getType() == DeeTokens.KW_IN) {
				infixOp = InfixOpType.NOT_IN;
			}
		}
		
		if(infixOp == null) {
			return refOrExp(canBeRef, leftExp);
		}
		
		// If lower precedence it can't be parsed to right expression, 
		// instead this expression must become left children of new parent
		if(infixOp.precedence < precedenceLimit) 
			return refOrExp(canBeRef, leftExp);
		
		if(infixOp != InfixOpType.MUL) {
			canBeRef = updateRefOrExpToExpression(canBeRef, leftExp, false);
		}
		
		Expression newLeftExp = null;
		switch (infixOp.category) {
		case COMMA:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.COMMA);
			break;
		case ASSIGN:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.ASSIGN);
			break;
		case CONDITIONAL:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.CONDITIONAL);
			break;
		case LOGICAL_OR:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.LOGICAL_AND);
			break;
		case LOGICAL_AND: 
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.OR);
			break;
		case OR:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.XOR);
			break;
		case XOR:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.AND);
			break;
		case AND:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.EQUALS);
			break;
		case EQUALS:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.SHIFT);
			break;
		case SHIFT:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.ADD);
			break;
		case ADD:
			newLeftExp = parseInfixOperator(leftExp, infixOp, InfixOpType.MUL);
			break;
		case MUL:
			RefOrExpParse refOrExp = parseInfixOperator(leftExp, infixOp, InfixOpType.NULL, canBeRef);
			if(refOrExp.mode == RefOrExpMode.REF) {
				return refOrExp;
			}
			canBeRef = refOrExp.canBeRef();
			
			newLeftExp = refOrExp.getExp();
			break;
		default:
			assertUnreachable();
		}
		assertTrue(newLeftExp != null);
		return parseReferenceOrExpression_ExpStart(precedenceLimit, newLeftExp, canBeRef);
	}
	
	public Expression parseInfixOperator(Expression leftExp, InfixOpType op, InfixOpType rightExpLimitToken) {
		return parseInfixOperator(leftExp, op, rightExpLimitToken, false).getExp();
	}
	
	public RefOrExpParse parseInfixOperator(Expression leftExp, InfixOpType op, InfixOpType rightExpLimit,
		final boolean canBeRef) {
		
		consumeLookAhead();
		
		LexElement afterStarOp = null;
		if(canBeRef) {
			assertTrue(lastLexElement.getType() == DeeTokens.STAR);
			afterStarOp = lookAheadElement();
		}
		
		if(op == InfixOpType.NOT_IS || op == InfixOpType.NOT_IN) {
			consumeLookAhead(); // consume second infix token
		}
		checkValidAssociativity(leftExp, op, canBeRef);
		
		Expression middleExp = null;
		Expression rightExp = null;
		RefOrExpMode mode = null;
		
		parsing: {
			if(op == InfixOpType.CONDITIONAL) {
				middleExp = parseExpression();
				if(middleExp == null) {
					reportErrorExpectedRule(EXPRESSION_RULE);
				}
				if(consumeExpectedToken(DeeTokens.COLON) == null) {
					break parsing;
				}
			}
			
			RefOrExpParse rightExpResult = parseReferenceOrExpression_notStart(rightExpLimit.precedence, canBeRef);
			mode = rightExpResult.mode;
			
			if(rightExpResult.mode == null) {
				if(canBeRef) {
					mode = RefOrExpMode.REF;
				} else {
					mode = RefOrExpMode.EXP;
					reportErrorExpectedRule(EXPRESSION_RULE);
				}
				break parsing;
			}
			updateRefOrExpToExpression(canBeRef, leftExp, mode != RefOrExpMode.EXP);
			
			rightExp = rightExpResult.getExp_NoRuleContinue();
			checkValidAssociativity(rightExp, op, canBeRef);
		}
		
		if(op == InfixOpType.CONDITIONAL) {
			return expConnect(new ExpConditional(leftExp, middleExp, rightExp, srToCursor(leftExp)));
		}
		
		return refOrExpConnect(mode, new ExpInfix(leftExp, op, rightExp, srToCursor(leftExp)), afterStarOp);
	}
	
	protected void checkValidAssociativity(Expression exp, InfixOpType op, boolean canBeRef) {
		// Check for some syntax situations which are technically not allowed by the grammar:
		switch (op.category) {
		case OR: case XOR: case AND: case EQUALS:
			if(exp instanceof ExpInfix) {
				assertTrue(canBeRef == false);
				if(((ExpInfix) exp).kind.category == InfixOpType.EQUALS) {
					addError(ParserErrorTypes.EXP_MUST_HAVE_PARENTHESES, exp.getSourceRange(), op.sourceValue);
				}
			}
		default: break;
		}
	}
	
	protected Expression convertRefOrExpToExpression(Expression exp) {
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
				node.setData(DeeParser.PARSED_STATUS);
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
		} else if(indexArgExp.getData() == DeeParser.PARSED_STATUS) {
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
		
		ArrayList<Expression> args = parseArgumentList(DeeTokens.COMMA, DeeTokens.CLOSE_PARENS);
		return connect(new ExpCall(callee, arrayView(args), srToCursor(callee)));
	}
	
	protected ArrayList<Expression> parseArgumentList(DeeTokens tokenSEPARATOR, DeeTokens tokenLISTCLOSE) {
		ArrayList<Expression> args = new ArrayList<Expression>();
		boolean first = true;
		while(true) {
			Expression arg = parseAssignExpression_toMissing(!first);
			
			if(first && arg.getNodeType() == ASTNodeTypes.MISSING_EXPRESSION) {
				if(lookAhead() == tokenSEPARATOR) {
					reportError(ParserErrorTypes.EXPECTED_RULE, EXPRESSION_RULE, false);
				} else {
					consumeExpectedToken(tokenLISTCLOSE);
					break;
				}
			}
			
			args.add(arg);
			first = false;
			
			if(tryConsume(tokenSEPARATOR)) {
				continue;
			}
			consumeExpectedToken(tokenLISTCLOSE);
			break;
		}
		return args;
	}
	
	public Expression parseParenthesesExp() {
		if(!tryConsume(DeeTokens.OPEN_PARENS))
			return null;
		int nodeStart = lastLexElement.getStartPos();
		
		Expression exp = parseExpression_ToMissing(true);
		consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		return connect(new ExpParentheses(exp, srToCursor(nodeStart)));
	}
	
	public ExpAssert parseAssertExp() {
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
	
	public ExpImportString parseImportExp() {
		if(tryConsume(DeeTokens.KW_IMPORT) == false)
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		Expression exp = parseExpWithParens();
		return connect(new ExpImportString(exp, srToCursor(nodeStart)));
	}
	
	public ExpMixinString parseMixinExp() {
		if(tryConsume(DeeTokens.KW_MIXIN) == false)
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		Expression exp = parseExpWithParens();
		return connect(new ExpMixinString(exp, srToCursor(nodeStart)));
	}
	
	protected Expression parseExpWithParens() {
		Expression exp = null;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseAssignExpression_toMissing(true);
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		return exp;
	}
	
	public ExpTypeId parseTypeIdExp() {
		if(tryConsume(DeeTokens.KW_TYPEID) == false)
			return null;
		
		int nodeStart = lastLexElement.getStartPos();
		Reference ref = null;
		Expression exp = null;
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			Resolvable resolvable = parseReferenceOrExpression();
			if(resolvable == null) {
				exp = parseExpression_ToMissing(true);
			} else if(resolvable instanceof Reference) {
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
	
	
	/* ----------------------------------------- */
	
	protected ASTNeoNode parseDeclaration_IdStart() {
		return parseDeclaration_referenceAhead();
	}
	
	protected ASTNeoNode parseDeclaration_RefPrimitiveStart() {
		return parseDeclaration_referenceAhead();
	}
	
	protected ASTNeoNode parseDeclaration_DotStart() {
		return parseDeclaration_referenceAhead();
	}
	
	protected ASTNeoNode parseDeclaration_referenceAhead() {
		RefParseResult refParse = parseReference_begin(false);
		Reference ref = refParse.ref;
		boolean consumedSemiColon = false;
		
		if(!refParse.balanceBroken) {
			
			if(ref instanceof RefModuleQualified) {
				if(((RefModuleQualified) ref).qualifiedName.name == null) {
					return connect(new InvalidDeclaration(ref, false, srToCursor(ref.getStartPos())));
				}
			}
			
			if(lookAhead() == DeeTokens.IDENTIFIER) {
				LexElement defId = consumeInput();
				return parseDefinition_Reference_Identifier(ref, defId);
			}  else {
				reportErrorExpectedToken(DeeTokens.IDENTIFIER);
				if(consumeExpectedToken(DeeTokens.SEMICOLON) != null) {
					consumedSemiColon = true;
				}
				return connect(new InvalidDeclaration(ref, consumedSemiColon, srToCursor(ref.getStartPos())));
			}
			
		}
		// else: Balance is broken
		return connect(new InvalidDeclaration(ref, consumedSemiColon, srToCursor(ref.getStartPos())));
	}
	
	protected ASTNeoNode parseDefinition_Reference_Identifier(Reference ref, LexElement defId) {
		ArrayList<DefinitionVarFragment> fragments = new ArrayList<DefinitionVarFragment>();
		Initializer init = null;
		
		if(tryConsume(DeeTokens.ASSIGN)){ 
			init = parseInitializer();
		}
		
		while(tryConsume(DeeTokens.COMMA)) {
			DefinitionVarFragment defVarFragment = parseVarFragment();
			fragments.add(defVarFragment);
		}
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		SourceRange sr = srToCursor(ref.getStartPos());
		
		return connect(new DefinitionVariable(defUnitNoComments(defId), ref, init, arrayView(fragments), sr));
	}
	
	public DefinitionVarFragment parseVarFragment() {
		Initializer init = null;
		LexElement fragId = tryConsumeIdentifier();
		if(!fragId.isMissingElement()) {
			if(tryConsume(DeeTokens.ASSIGN)){ 
				init = parseInitializer();
			}
		}
		return connect(new DefinitionVarFragment(defUnitNoComments(fragId), init, srToCursor(fragId)));
	}
	
	public static final String INITIALIZER_RULE = "INITIALIZER";
	
	public Initializer parseInitializer() {
		Expression exp = parseAssignExpression();
		if(exp == null) {
			reportErrorExpectedRule(INITIALIZER_RULE);
			int elemStart = getParserPosition();
			// Advance parser position, mark the advanced range as missing element:
			consumeIgnoredTokens(DeeTokens.INTEGER, true);
			exp = connect(new MissingExpression(srToCursor(elemStart)));
		}
		return connect(new InitializerExp(exp, exp.getSourceRange()));
	}
	
	/* ----------------------------------------- */
	
	protected class AttribBodyParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public NodeList2 declList;
		
		public AttribBodyParseRule parseAttribBody(boolean acceptEmptyDecl) {
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
				declList = parseDeclList(null);
			} else if(tryConsume(DeeTokens.OPEN_BRACE)) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				declList = parseDeclList(DeeTokens.CLOSE_BRACE);
				consumeExpectedToken(DeeTokens.CLOSE_BRACE);
			} else {
				ASTNeoNode decl = parseDeclaration(acceptEmptyDecl);
				if(decl == null) {
					reportErrorExpectedRule(DECLARATION_RULE);
					return null;
				} else {
					declList = connect(
						new NodeList2(ArrayView.create(new ASTNeoNode[] {decl}), decl.getSourceRange()));
				}
			}
			
			return this;
		}
	}
	
	protected NodeList2 parseDeclList(DeeTokens bodyListTerminator) {
		int nodeListStart = getParserPosition();
		
		ArrayView<ASTNeoNode> declDefs = parseDeclDefs(bodyListTerminator);
		consumeIgnoredTokens();
		return connect(new NodeList2(declDefs, srToCursor(nodeListStart)));
	}
	
	public DeclarationLinkage parseDeclarationExternLinkage() {
		if(!tryConsume(DeeTokens.KW_EXTERN)) {
			return null;
		}
		int declStart = lastLexElement.getStartPos();
		
		Linkage linkage = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(tryConsume(DeeTokens.OPEN_PARENS)) {
			Token linkageId = consumeIf(DeeTokens.IDENTIFIER);
			if(linkageId != null) {
				linkage = Linkage.fromString(linkageId.source);
				if(linkage == Linkage.C && tryConsume(DeeTokens.INCREMENT)) {
					linkage = Linkage.CPP;
				}
			}
			if(linkage == null) {
				reportError(ParserErrorTypes.INVALID_EXTERN_ID, null, true);
			}
			
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
				ab.parseAttribBody(false);
			}
		} else {
			ab.parseAttribBody(false);
		}
		
		return connect(
			new DeclarationLinkage(linkage, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationAlign parseDeclarationAlign() {
		if(!tryConsume(DeeTokens.KW_ALIGN)) {
			return null;
		}
		int declStart = lastLexElement.getStartPos();
		
		Token alignNum = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(tryConsume(DeeTokens.OPEN_PARENS)) {
			alignNum = consumeExpectedToken(DeeTokens.INTEGER_DECIMAL, true).token;
			
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
				ab.parseAttribBody(false);
			}
		} else {
			ab.parseAttribBody(false);
		}
		
		return connect(new DeclarationAlign(alignNum, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationPragma parseDeclarationPragma() {
		if(!tryConsume(DeeTokens.KW_PRAGMA)) {
			return null;
		}
		int declStart = lastLexElement.getStartPos();
		
		Symbol pragmaId = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			pragmaId = parseSymbol();
			
			// TODO pragma argument list;
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
				ab.parseAttribBody(true);
			}
		}
		
		return connect(
			new DeclarationPragma(pragmaId, null, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}

	public Symbol parseSymbol() {
		LexElement id = consumeExpectedToken(DeeTokens.IDENTIFIER, true);
		return connect(new Symbol(id.token.source, sr(id.token)));
	}
	
	public DeclarationProtection parseDeclarationProtection() {
		if(lookAheadGrouped() != DeeTokens.PROTECTION_KW) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastLexElement.getStartPos();
		Protection protection = DeeTokenSemantics.getProtectionFromToken(lastLexElement.getType());
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(false);
		return connect(new DeclarationProtection(protection, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationBasicAttrib parseDeclarationBasicAttrib() {
		EDeclarationAttribute attrib = EDeclarationAttribute.fromToken(lookAhead());
		if(attrib == null) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastLexElement.getStartPos();
		
		AttribBodyParseRule apr = new AttribBodyParseRule().parseAttribBody(false);
		return connect(new DeclarationBasicAttrib(attrib, apr.bodySyntax, apr.declList, srToCursor(declStart)));
	}
	
	
	/* ----------------------------------------- */
	
	public DeclarationMixinString parseDeclarationMixinString() {
		if(!tryConsume(DeeTokens.KW_MIXIN)) {
			return null;
		}
		int declStart = lastLexElement.getStartPos();
		Expression exp = null;
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseExpression();
			if(exp == null) {
				reportErrorExpectedRule(EXPRESSION_RULE);
			}
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		return connect(new DeclarationMixinString(exp, srToCursor(declStart)));
	}
	
}