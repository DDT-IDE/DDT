package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
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
import dtool.ast.expressions.ExpConditional;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralChar;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpNull;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.ExpInfix;
import dtool.ast.expressions.ExpInfix.InfixOpType;
import dtool.ast.expressions.Initializer;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.ExpPrefix;
import dtool.ast.expressions.ExpPrefix.PrefixOpType;
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
			return new Module(md.getModuleSymbol(), null, md, members, modRange);
		} else {
			return connect(Module.createModuleNoModuleDecl(modRange, "__tests_unnamed" /*BUG here*/, members));
		}
	}
	
	public DeclarationModule parseModuleDeclaration() {
		if(!tryConsume(DeeTokens.KW_MODULE)) {
			return null;
		}
		int declStart = lastLexElement.getStartPos();
		
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
		SourceRange modDeclRange = srToCursor(declStart);
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
		
		return arrayView(declarations, ASTNeoNode.class);
	}
	
	
	public DeclarationImport parseImportDeclaration() {
		boolean isStatic = false;
		int declStart = -1;
		
		if(tryConsume(DeeTokens.KW_STATIC)) { // BUG here
			isStatic = true;
			declStart = lastLexElement.getStartPos();
		}
		
		if(!tryConsume(DeeTokens.KW_IMPORT)) {
			return null;
		}
		declStart = NewUtils.updateIfNull(declStart, lastLexElement.getStartPos());
		
		ArrayList<IImportFragment> fragments = new ArrayList<IImportFragment>();
		do {
			IImportFragment fragment = parseImportFragment();
			assertNotNull(fragment);
			fragments.add(fragment);
		} while(tryConsume(DeeTokens.COMMA));
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		SourceRange sr = srToCursor(declStart);
		boolean isTransitive = false;
		
		return connect(
			new DeclarationImport(arrayView(fragments, IImportFragment.class), isStatic, isTransitive, sr));
	}
	
	public IImportFragment parseImportFragment() {
		LexElement aliasId = null;
		ArrayList<String> packages = new ArrayList<String>(0);
		int refModuleStartPos = -1;
		
		while(true) {
			LexElement id = tryConsumeIdentifier();
			refModuleStartPos = refModuleStartPos == -1 ? id.getStartPos() : refModuleStartPos;
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packages.add(id.token.source);
			} else if(packages.isEmpty() && tryConsume(DeeTokens.ASSIGN)) { // BUG here
				aliasId = id;
				refModuleStartPos = -1;
			} else {
				RefModule refModule = 
					konnect(new RefModule(arrayViewS(packages), id.token.source, srToCursor(refModuleStartPos))); 
				
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
		
		SourceRange isRange = srToCursor(fragment.getStartPos());
		return connect(
			new ImportSelective(fragment, arrayView(selFragments, IImportSelectiveSelection.class), isRange));
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
		return parseReference_extraInfo(false).ref;
	}
	
	public Reference parseReference(boolean expressionContext) {
		return parseReference_extraInfo(expressionContext).ref;
	}
	
	protected RefParseResult parseReference_extraInfo(boolean parsingExp) {
		DeeTokens la = lookAheadGrouped();
		
		switch (la) {
		case DOT: return parseReference_referenceStart(parseRefModuleQualified(), parsingExp);
		case IDENTIFIER: return parseReference_referenceStart(parseRefIdentifier(), parsingExp);
		case PRIMITIVE_KW: return parseReference_referenceStart(parseRefPrimitive(lookAhead()), parsingExp);
		
		default:
		return new RefParseResult(null);
		}
	}
	
	protected RefParseResult parseReference_referenceStart(Reference ref, boolean parsingExp) {
		if(lookAhead() == DeeTokens.STAR) {
			if(parsingExp) {
				// Star is multiply infix operator, dont parse as pointer ref
				return new RefParseResult(ref);
			}
			consumeLookAhead();
			
			RefTypePointer pointerRef = connect(new RefTypePointer(ref, srToCursor(ref.getStartPos())));
			return parseReference_referenceStart(pointerRef, parsingExp);
		} else if(tryConsume(DeeTokens.DOT)) {
			RefIdentifier qualifiedId = parseRefIdentifier();
			ref = connect(new RefQualified(ref, qualifiedId, srToCursor(ref.getStartPos())));
			if(qualifiedId.name == null) {
				return new RefParseResult(true, ref);
			}
			return parseReference_referenceStart(ref, parsingExp);
		} else if(tryConsume(DeeTokens.OPEN_BRACKET)) {
			if(tryConsume(DeeTokens.CLOSE_BRACKET)) {
				RefTypeDynArray dynArrayRef = connect(new RefTypeDynArray(ref, srToCursor(ref.getStartPos())));
				return parseReference_referenceStart(dynArrayRef, parsingExp);
			} else {
				Resolvable resolvable = parseReferenceOrExpression();
				if(consumeExpectedToken(DeeTokens.CLOSE_BRACKET) == null) {
					// Note: if resolvable == null then this case should always be entered into
					if(resolvable == null) {
						ref = new RefTypeDynArray(ref, srToCursor(ref.getStartPos()));
					} else {
						ref = new RefIndexing(ref, resolvable, srToCursor(ref.getStartPos()));
					}
					return new RefParseResult(true, connect(ref));
				}
				assertNotNull(resolvable);
				RefIndexing refIndexing = new RefIndexing(ref, resolvable, srToCursor(ref.getStartPos()));
				return parseReference_referenceStart(refIndexing, parsingExp);
			}
		}
		return new RefParseResult(ref);
	}
	
	public Resolvable parseReferenceOrExpression() {
		Reference ref = parseReference(); /*BUG here*/
		if(ref != null) {
			return ref;
		}
		return parseExpression();
	}
	
	/* ----------------------------------------- */
	public static String EXPRESSION_RULE = "expression";
	
	public Expression parseExpression() {
		return parseExpression(0);
	}
	
	protected Expression parseExpression(int precedenceLimit) {
		Expression leftExpression = parseUnaryExpression();
		if(leftExpression == null)
			return null;
		
		return parseExpression_ExpStart(leftExpression, precedenceLimit);
	}
	
	public Expression parseAssignExpression() {
		return parseExpression(InfixOpType.ASSIGN.precedence);
	}
	
	protected Expression parseExpression_ExpStart(final Expression leftExp, int precedenceLimit) {
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
			return leftExp;
		}
		
		Expression infixExp = null;
		switch (infixOp.category) {
		case COMMA:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.COMMA);
			break;
		case ASSIGN:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.ASSIGN);
			break;
		case CONDITIONAL:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.CONDITIONAL);
			break;
		case LOGICAL_OR:
			infixExp = parseInfixOperator(leftExp, infixOp,precedenceLimit, InfixOpType.LOGICAL_AND);
			break;
		case LOGICAL_AND: 
			infixExp = parseInfixOperator(leftExp, infixOp,precedenceLimit, InfixOpType.OR);
			break;
		case OR:
			infixExp = parseInfixOperator(leftExp, infixOp,precedenceLimit, InfixOpType.XOR);
			break;
		case XOR:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.AND);
			break;
		case AND:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.EQUALS);
			break;
		case EQUALS:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.SHIFT);
			break;
		case SHIFT:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.ADD);
			break;
		case ADD:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.MUL);
			break;
		case MUL:
			infixExp = parseInfixOperator(leftExp, infixOp, precedenceLimit, InfixOpType.NULL);
			break;
		default:
			assertUnreachable();
		}
		if(infixExp == null) {
			return leftExp;
		}
		
		return parseExpression_ExpStart(infixExp, precedenceLimit);
	}
	
	public Expression parseInfixOperator(Expression leftExp, InfixOpType op, int precedenceLimit, 
		InfixOpType rightExpLimitToken) {
		
		// If lower precedence it cant be parsed to right expression, 
		// instead this expression must become left children of new parent
		if(op.precedence < precedenceLimit) 
			return null;
		
		consumeLookAhead();
		if(op == InfixOpType.NOT_IS || op == InfixOpType.NOT_IN) {
			consumeLookAhead(); // consume second infix token
		}
		checkValidAssociativity(leftExp, op);
		
		Expression middleExp = null;
		Expression rightExp = null;
		
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
			
			rightExp = parseExpression(rightExpLimitToken.precedence);
			if(rightExp == null) {
				reportErrorExpectedRule(EXPRESSION_RULE);
				break parsing;
			}
			checkValidAssociativity(rightExp, op);
		}
		
		if(op == InfixOpType.CONDITIONAL) {
			return connect(new ExpConditional(leftExp, middleExp, rightExp, srToCursor(leftExp)));
		}
		
		return connect(new ExpInfix(leftExp, op, rightExp, srToCursor(leftExp)));
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
	
	public Expression parseUnaryExpression() {
		switch (lookAheadGrouped()) {
		case KW_TRUE: case KW_FALSE: {
			Token token = consumeLookAhead();
			return new ExpLiteralBool(token.type == DeeTokens.KW_TRUE, srToCursor(lastLexElement));
		}
		case KW_THIS:
			consumeLookAhead();
			return new ExpThis(srToCursor(lastLexElement));
		case KW_SUPER:
			consumeLookAhead();
			return new ExpSuper(srToCursor(lastLexElement));
		case KW_NULL:
			consumeLookAhead();
			return new ExpNull(srToCursor(lastLexElement));
		case DOLLAR:
			consumeLookAhead();
			return new ExpArrayLength(srToCursor(lastLexElement));
			
		case KW___LINE__:
			return new ExpLiteralInteger(consumeLookAhead(), srToCursor(lastLexElement));
		case KW___FILE__:
			return new ExpLiteralString(consumeLookAhead(), srToCursor(lastLexElement));
		case INTEGER:
			return connect(new ExpLiteralInteger(consumeLookAhead(), srToCursor(lastLexElement)));
		case CHARACTER: 
			return connect(new ExpLiteralChar(consumeLookAhead(), srToCursor(lastLexElement)));
		case FLOAT:
			return connect(new ExpLiteralFloat(consumeLookAhead(), srToCursor(lastLexElement)));
		case STRING:
			return parseStringLiteral();
			
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
			Expression exp = parseUnaryExpression();
			if(exp == null) {
				reportErrorExpectedRule(EXPRESSION_RULE);
			}
			return connect(new ExpPrefix(prefixOpType, exp, srToCursor(prefixExpToken)));
		default:
			Reference ref = parseReference(true);
			if(ref != null) {
				return attachExpReference(new ExpReference(ref, ref.getSourceRange()));
			}
			return null;
		}
	}
	
	protected Expression attachExpReference(ExpReference expReference) {
		Reference ref = expReference.ref;
		if(isBuiltinTypeRef(ref)) {
			addError(ParserErrorTypes.TYPE_USED_AS_EXP_VALUE, ref.getSourceRange(), null);
		}
		return expReference;
	}
	
	protected static boolean isBuiltinTypeRef(Reference ref) {
		switch (ref.getNodeType()) {
		case REF_PRIMITIVE:
		case REF_TYPE_DYN_ARRAY:
		case REF_TYPE_POINTER:
			return true;
		case REF_INDEXING:
		RefIndexing refIndexing = (RefIndexing) ref;
		Resolvable indexParam = refIndexing.indexParam;
		return isBuiltinTypeRef(refIndexing.elemType) ||
			((indexParam instanceof Reference) && isBuiltinTypeRef((Reference) indexParam));
		
		default:
			return false;
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
		RefParseResult refParse = parseReference_extraInfo(false);
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
		
		return connect(
			new DefinitionVariable(defUnitNoComments(defId), ref, init, 
				arrayView(fragments, DefinitionVarFragment.class), srToCursor(ref.getStartPos()))
		);
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
			consumeIgnoredTokens(DeeTokens.INTEGER, true);
			exp = connect(new MissingExpression(srToCursor(elemStart)));
		}
		return new InitializerExp(exp, exp.getSourceRange());
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