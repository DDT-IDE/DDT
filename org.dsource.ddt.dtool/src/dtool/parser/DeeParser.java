package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.math.BigInteger;
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
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.declarations.InvalidSyntaxDeclaration;
import dtool.ast.definitions.DefinitionVarFragment;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.Initializer;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.Reference;
import dtool.parser.ParserError.EDeeParserErrors;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

public class DeeParser extends AbstractDeeParser {
	
	private static final Token BEGIN_OF_SOURCE = new Token(DeeTokens.WHITESPACE, "", 0);
	
	public DeeParser(String source) {
		super(new DeeLexer(source));
		lastRealToken = BEGIN_OF_SOURCE;
	}
	
	public DeeParser(DeeLexer deeLexer) {
		super(deeLexer);
	}
	
	public static DeeParserResult parse(String source) {
		DeeParser deeParser = new DeeParser(source);
		Module module = deeParser.parseModule();
		DeeParserResult parseResult = new DeeParserResult(module, deeParser.errors);
		deeParser.errors = null;
		return parseResult;
	}
	
	/* ----------------------------------------------------------------- */
	
	public Module parseModule() {
		DeclarationModule md = parseModuleDeclaration();
		
		ArrayView<ASTNeoNode> members = parseDeclDefs(null);
		assertTrue(lookAhead() == DeeTokens.EOF);
		assertTrue(lookAheadToken().getEndPos() == deeLexer.source.length());
		
		SourceRange modRange = new SourceRange(0, deeLexer.source.length());
		
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
		
		ArrayList<String> packagesList = new ArrayList<String>(0);
		Token moduleId;
		
		while(true) {
			Token id = tryConsumeIdentifier();
			
			if(!isMissingId(id) && tryConsume(DeeTokens.DOT)) {
				packagesList.add(id.tokenSource);
				id = null;
			} else {
				consumeExpectedToken(DeeTokens.SEMICOLON);
				moduleId = id;
				break;
			}
		}
		assertNotNull(moduleId);
		
		String[] packages = ArrayUtil.createFrom(packagesList, String.class);
		SourceRange modDeclRange = srToCursor(0); // BUG here on 0 ?
		return connect(new DeclarationModule(modDeclRange, packages, tokenInfo(moduleId)));
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
			declStart = lastRealToken.getStartPos();
		}
		
		if(!tryConsume(DeeTokens.KW_IMPORT)) {
			return null;
		}
		declStart = NewUtils.updateIfNull(declStart, lastRealToken.getStartPos());
		
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
		Token aliasId = null;
		ArrayList<String> packages = new ArrayList<String>(0);
		int refModuleStartPos = -1;
		
		while(true) {
			Token id = tryConsumeIdentifier();
			refModuleStartPos = refModuleStartPos == -1 ? id.getStartPos() : refModuleStartPos;
			
			if(!isMissingId(id) && tryConsume(DeeTokens.DOT)) {
				packages.add(id.tokenSource);
			} else if(packages.isEmpty() && tryConsume(DeeTokens.ASSIGN)) { // BUG here
				aliasId = id;
				refModuleStartPos = -1;
			} else {
				RefModule refModule = 
					new RefModule(arrayViewS(packages), id.tokenSource, srToCursor(refModuleStartPos)); 
				
				IImportFragment fragment = (aliasId == null) ? 
					connect(new ImportContent(refModule)) : 
					connect(new ImportAlias(defUnitRaw(srToCursor(aliasId.getStartPos()), aliasId), refModule));
				
				if(tryConsume(DeeTokens.COLON)) {
					return parseSelectiveModuleImport(fragment);
				}
				
				return fragment;
			}
		}
	}
	
	public ImportSelective parseSelectiveModuleImport(IImportFragment fragment) {
		ArrayList<ASTNeoNode> selFragments = new ArrayList<ASTNeoNode>();
		
		do {
			Token aliasId = null;
			Token id = tryConsumeIdentifier();
			
			if(tryConsume(DeeTokens.ASSIGN)){ // BUG here
				aliasId = id;
				id = tryConsumeIdentifier();
			}
			
			RefImportSelection refImportSelection = connect(new RefImportSelection(id.tokenSource, sr(id)));
			if(aliasId == null) {
				selFragments.add(refImportSelection);
			} else {
				selFragments.add(connect(new ImportSelectiveAlias(
					defUnitTuple(srToCursor(aliasId), aliasId, null), refImportSelection)));
			}
			
		} while(tryConsume(DeeTokens.COMMA));
		
		SourceRange isRange = srToCursor(fragment.getStartPos());
		return connect(new ImportSelective(fragment, arrayView(selFragments, ASTNeoNode.class), isRange));
	}
	
	/* --------------------- DECLARATION --------------------- */
	public static String DECLARATION_RULE = "declaration";
	
	public ASTNeoNode parseDeclaration() {
		return parseDeclaration(true);
	}
	
	/** This rule always returns a node, except only on EOF where it returns null. */
	public ASTNeoNode parseDeclaration(boolean acceptEmptyDecl) {
		while(true) {
			DeeTokens la = assertNotNull_(lookAhead());
			
			if(la == DeeTokens.EOF) {
				return null;
			}
			
			switch (la) {
			case KW_MIXIN: return parseMixinStringDeclaration();
			
			case KW_EXTERN: return parseDeclarationExternLinkage();
			case KW_ALIGN: return parseDeclarationAlign();
			case KW_PRAGMA: return parseDeclarationPragma();
				
			case KW_STATIC: 
				if(lookAhead(/*1*/) == DeeTokens.KW_IMPORT) { // TODO 
					return parseImportDeclaration();
				}
				return parseDeclarationBasicAttrib();
			case KW_DEPRECATED: return parseDeclarationBasicAttrib();
			case KW_FINAL: return parseDeclarationBasicAttrib();
			case KW_SYNCHRONIZED: return parseDeclarationBasicAttrib();
			case KW_OVERRIDE: return parseDeclarationBasicAttrib();
			case KW_ABSTRACT: return parseDeclarationBasicAttrib();
			case KW_CONST: return parseDeclarationBasicAttrib();
			case KW_SCOPE: return parseDeclarationBasicAttrib();
			case KW___GSHARED: return parseDeclarationBasicAttrib();
			case KW_SHARED: return parseDeclarationBasicAttrib();
			case KW_IMMUTABLE: return parseDeclarationBasicAttrib();
			case KW_INOUT: return parseDeclarationBasicAttrib();
			// @disable keywords?
			
			
			case KW_PRIVATE: return parseDeclarationProtection();
			case KW_PACKAGE: return parseDeclarationProtection();
			case KW_PROTECTED: return parseDeclarationProtection();
			case KW_PUBLIC: return parseDeclarationProtection();
			case KW_EXPORT: return parseDeclarationProtection();
			
			case IDENTIFIER: return parseDeclaration_IdStart();
			
			case KW_BOOL:  
			case KW_BYTE: case KW_UBYTE: 
			case KW_SHORT: case KW_USHORT: case KW_INT: case KW_UINT: case KW_LONG: case KW_ULONG: 
			case KW_CHAR: case KW_WCHAR: case KW_DCHAR: 
			//case KW_FLOAT: 
			case KW_DOUBLE: case KW_REAL: 
			case KW_VOID: 
			case KW_IFLOAT: case KW_IDOUBLE: case KW_IREAL: case KW_CFLOAT: case KW_CDOUBLE: case KW_CREAL: 
				return parseDeclaration_RefPrimitiveStart(la);
			
			case KW_AUTO: // TODO:
				
			default:
				break;
			}
			
			if(la == DeeTokens.KW_IMPORT || la == DeeTokens.KW_STATIC) {
				return parseImportDeclaration();
			} else if(acceptEmptyDecl && tryConsume(DeeTokens.SEMICOLON)) {
				return connect(new DeclarationEmpty(srToCursor(lastRealToken)));
			} else if(la == DeeTokens.ASSIGN || la == DeeTokens.DOT) {
				return MiscDeclaration.parseMiscDeclaration(this);
			} else {
				Token badToken = consumeLookAhead();
				reportSyntaxError(DECLARATION_RULE);
				return connect(new InvalidSyntaxDeclaration(badToken));
			}
		}
	}
	
	protected ASTNeoNode parseDeclaration_IdStart() {
		Token id = consumeLookAhead();
		assertTrue(id.type == DeeTokens.IDENTIFIER);
		return parseDeclaration_referenceStart(refIdentifier(id));
	}
	
	protected RefIdentifier refIdentifier(Token id) {
		return new RefIdentifier(id.tokenSource, sr(id));
	}
	
	protected ASTNeoNode parseDeclaration_RefPrimitiveStart(DeeTokens primitiveType) {
		Token token = consumeLookAhead();
		assertTrue(token.type == primitiveType);
		RefPrimitive ref = new RefPrimitive(token, sr(token));
		return parseDeclaration_referenceStart(ref);
	}
	
	protected ASTNeoNode parseDeclaration_referenceStart(Reference ref) {
		DeeTokens la = lookAhead();
		if(la == DeeTokens.IDENTIFIER) {
			Token defId = consumeLookAhead();
			return parseDefinition_Reference_Identifier(ref, defId);
		} else if(lookAhead() == DeeTokens.DOT) {
			consumeLookAhead();
			
			RefIdentifier qualifiedRef = refIdentifier(tryConsumeIdentifier());
			ref = new RefQualified(ref, qualifiedRef, srToCursor(ref.getStartPos()));
			return parseDeclaration_referenceStart(ref);
		} else {
			reportErrorExpectedToken(DeeTokens.IDENTIFIER);
			consumeExpectedToken(DeeTokens.SEMICOLON);
			return connect(new InvalidSyntaxDeclaration(ref, srToCursor(ref.getStartPos())));
		}
	}
	
	protected static final Comment[] COMMENT_TODO = null;

	protected ASTNeoNode parseDefinition_Reference_Identifier(Reference ref, Token defId) {
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
			new DefinitionVariable(defUnitTuple(srToCursor(ref.getStartPos()), defId, COMMENT_TODO), ref, init, 
				arrayView(fragments, DefinitionVarFragment.class))
		);
	}
	
	public DefinitionVarFragment parseVarFragment() {
		Token fragId = tryConsumeIdentifier();
		Initializer init = null;
		if(!isMissingId(fragId)) {
			if(tryConsume(DeeTokens.ASSIGN)){ 
				init = parseInitializer();
			}
		}
		/*BUG here connect*/
		return new DefinitionVarFragment(defUnitRaw(srToCursor(fragId), fragId), init);
	}
	
	
	public Initializer parseInitializer() {
		Expression exp;
		if(lookAhead() == DeeTokens.INTEGER) {
			exp = parseExpression();
		} else {
			reportErrorExpectedRule("INITIALIZER"); //TODO constant
			exp = new MissingExpression(srToCursor(lastRealToken.getEndPos()));
		}
		return new InitializerExp(exp, exp.getSourceRange());
	}
	
	/* ----------------------------------------- */
	// TODO expression rule
	public ExpLiteralInteger parseExpression() {
		tryConsume(DeeTokens.INTEGER);
		return new ExpLiteralInteger(new BigInteger(lastRealToken.tokenSource), srToCursor(lastRealToken));
	}
	
	/* ----------------------------------------- */
	
	public class AttribBodyParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public NodeList2 declList;
		
		public AttribBodyParseRule parseAttribBody(boolean accepEmptyDecl) {
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
				declList = parseDeclList(null);
			} else if(tryConsume(DeeTokens.OPEN_BRACE)) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				declList = parseDeclList(DeeTokens.CLOSE_BRACE);
				consumeExpectedToken(DeeTokens.CLOSE_BRACE);
			} else {
				ASTNeoNode decl = parseDeclaration(accepEmptyDecl);
				if(decl == null) {
					reportErrorExpectedRule(DECLARATION_RULE);
				} else {
					declList = new NodeList2(ArrayView.create(new ASTNeoNode[] {decl}), decl.getSourceRange());
				}
			}
			
			return this;
		}
	}
	
	public NodeList2 parseDeclList(DeeTokens bodyListTerminator) {
		int nodeListStart = getLastTokenEndPos();
		
		ArrayView<ASTNeoNode> declDefs = parseDeclDefs(bodyListTerminator);
		NodeList2 nodeList = new NodeList2(declDefs, srToCursor(nodeListStart));
		return nodeList;
	}
	
	public DeclarationLinkage parseDeclarationExternLinkage() {
		if(!tryConsume(DeeTokens.KW_EXTERN)) {
			return null;
		}
		int declStart = lastRealToken.getStartPos();
		
		Linkage linkage = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(tryConsume(DeeTokens.OPEN_PARENS)) {
			Token linkageId = consumeIf(DeeTokens.IDENTIFIER);
			if(linkageId != null) {
				linkage = Linkage.fromString(linkageId.tokenSource);
				if(linkage == Linkage.C && tryConsume(DeeTokens.INCREMENT)) {
					linkage = Linkage.CPP;
				}
			}
			if(linkage == null) {
				reportError(EDeeParserErrors.INVALID_EXTERN_ID, null, true);
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
		int declStart = lastRealToken.getStartPos();
		
		int align = -1;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(tryConsume(DeeTokens.OPEN_PARENS)) {
			Token alignNum = consumeExpectedToken(DeeTokens.INTEGER);
			if(alignNum != null) {
				try {
					align = Integer.parseInt(alignNum.tokenSource);
				} catch(NumberFormatException e) {
					// TODO report error
				}
			}
			
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
				ab.parseAttribBody(false);
			}
		} else {
			ab.parseAttribBody(false);
		}
		
		return connect(new DeclarationAlign(align, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationPragma parseDeclarationPragma() {
		if(!tryConsume(DeeTokens.KW_PRAGMA)) {
			return null;
		}
		int declStart = lastRealToken.getStartPos();
		
		Token pragmaId = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			pragmaId = consumeExpectedToken(DeeTokens.IDENTIFIER);
			
			// TODO pragma argument list;
			Token expectedToken = consumeExpectedToken(DeeTokens.CLOSE_PARENS);
			if(expectedToken != null) {
				ab.parseAttribBody(true);
			}
		}
		
		SourceRange sr = srToCursor(declStart);
		return connect(new DeclarationPragma(symbol(pragmaId), null, ab.bodySyntax, ab.declList, sr));
	}
	
	public Symbol symbol(Token pragmaId) {
		return pragmaId == null ? null : new Symbol(pragmaId.tokenSource, sr(pragmaId));
	}
	
	public DeclarationProtection parseDeclarationProtection() {
		switch (lookAhead()) {
		case KW_PRIVATE:
		case KW_PACKAGE:
		case KW_PROTECTED:
		case KW_PUBLIC:
		case KW_EXPORT: 
			break;
		default:
			return null;
		}
		consumeLookAhead();
		int declStart = lastRealToken.getStartPos();
		Protection protection = Protection.fromToken(lastRealToken.type);
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(false);
		return connect(
			new DeclarationProtection(protection, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationBasicAttrib parseDeclarationBasicAttrib() {
		EDeclarationAttribute attrib = EDeclarationAttribute.fromToken(lookAhead());
		if(attrib == null) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastRealToken.getStartPos();
		
		AttribBodyParseRule apr = new AttribBodyParseRule().parseAttribBody(false);
		return connect(new DeclarationBasicAttrib(attrib, apr.bodySyntax, apr.declList, srToCursor(declStart)));
	}
	
	
	/* ----------------------------------------- */
	
	public DeclarationMixinString parseMixinStringDeclaration() {
		if(!tryConsume(DeeTokens.KW_MIXIN)) {
			return null;
		}
		int declStart = lastRealToken.getStartPos();
		Expression exp = null;
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			// TODO rest strings BUG here
			if(tryConsume(DeeTokens.STRING_DQ)) {
				Token expToken = lastRealToken;
				exp = connect(new ExpLiteralString(expToken, sr(expToken)));
			} else {
				reportErrorExpectedRule(EXPRESSION_RULE);
			}
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		return connect(new DeclarationMixinString(exp, srToCursor(declStart)));
	}
	
	// TODO:
	
	public static String EXPRESSION_RULE = "expression";

}