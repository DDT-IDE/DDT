package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.lastElement;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationLinkage.Linkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationProtection.Protection;
import dtool.ast.declarations.DeclarationStorageClass;
import dtool.ast.declarations.DeclarationStorageClass.EDeclarationAttribute;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.definitions.DefUnit.DefUnitTuple;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.Expression;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.parser.ParserError.EDeeParserErrors;
import dtool.util.ArrayView;

public class DeeParser extends AbstractDeeParser {
	
	private static final Token BEGIN_OF_SOURCE = new Token(DeeTokens.WHITESPACE, "", 0);
	
	public DeeParser(String source) {
		super(new DeeLexer(source));
		lastToken = BEGIN_OF_SOURCE;
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
	
	public TokenInfo tokenInfo(Token idToken) {
		if(idToken == null) {
			return null;
		}
		assertTrue(idToken.type == DeeTokens.IDENTIFIER);
		return new TokenInfo(idToken.tokenSource, idToken.getStartPos());
	}
	
	public Token consumeIdentifier() {
		Token id = consumeExpectedToken(DeeTokens.IDENTIFIER);
		if(id == null) {
			id = missingIdToken(lookAheadToken().getStartPos());
		}
		return id;
	}
	
	public static String MISSING_ID_VALUE = "";
	
	protected Token missingIdToken(int startPos) {
		return new Token(DeeTokens.IDENTIFIER, MISSING_ID_VALUE, startPos) {
			@Override
			public int getLength() {
				return 0;
			}
			
			@Override
			public int getEndPos() {
				return startPos;
			}
		};
	}
	
	public static boolean isRecoveredId(Token id) {
		return id.tokenSource == MISSING_ID_VALUE;
	}
	
	public SourceRange range(int startPos, int endPos) {
		assertTrue(startPos >= 0 && endPos >= startPos);
		return new SourceRange(startPos, endPos - startPos);
	}
	
	public DefUnitTuple defUnitTuple(SourceRange sourceRange, Token id, Comment[] comments) {
		return new DefUnitTuple(sourceRange, tokenInfo(id), comments);
	}
	
	public DefUnitTuple defUnitRaw(SourceRange sourceRange, Token id) {
		return defUnitTuple(sourceRange, id, null);
	}
	
	public static <T> ArrayView<T> arrayView(T[] arr) {
		return ArrayView.create(arr);
	}
	
	public static <T extends IASTNeoNode> ArrayView<T> arrayView(Collection<? extends T> list, Class<T> cpType) {
		return ArrayView.create(ArrayUtil.createFrom(list, cpType));
	}
	
	public static ArrayView<ASTNeoNode> arrayView(Collection<? extends ASTNeoNode> list) {
		return ArrayView.create(ArrayUtil.createFrom(list, ASTNeoNode.class));
	}
	
	public static ArrayView<String> arrayViewS(Collection<String> list) {
		return ArrayView.create(ArrayUtil.createFrom(list, String.class));
	}
	
	public int updateIfNull(int currentValue, int newValue) {
		return currentValue == -1 ? newValue : currentValue;
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
		
		ArrayList<String> packagesList = new ArrayList<String>(0);;
		Token id;
		int endPos;
		
		while(true) {
			id = consumeIdentifier();
			
			if(tryConsume(DeeTokens.SEMICOLON)) {
				endPos = getLastTokenEndPos();
				break;
			} else if(!isRecoveredId(id) && tryConsume(DeeTokens.DOT)) {
				packagesList.add(id.tokenSource);
				id = null;
			} else {
				endPos = id.getEndPos();
				reportErrorExpectedToken(DeeTokens.SEMICOLON);
				break;
			}
		}
		assertNotNull(id);
		
		String[] packages = ArrayUtil.createFrom(packagesList, String.class);
		Token moduleId = id;
		SourceRange modDeclRange = new SourceRange(0, endPos); // BUG here on 0 ?
		return connect(new DeclarationModule(modDeclRange, packages, tokenInfo(moduleId)));
	}
	
	
	public ArrayView<ASTNeoNode> parseDeclDefs(DeeTokens nodeListTerminator) {
		ArrayList<ASTNeoNode> declarations = new ArrayList<ASTNeoNode>();
		while(true) {
			ASTNeoNode decl = parseDeclaration(nodeListTerminator);
			if(decl == null) { 
				break;
			}
			declarations.add(decl);
		}
		
		return ArrayView.create(ArrayUtil.createFrom(declarations, ASTNeoNode.class));
	}
	
	
	
	public DeclarationImport parseImportDecl() {
		boolean isStatic = false;
		int declStart = -1;
		
		if(tryConsume(DeeTokens.KW_STATIC)) { // BUG here
			isStatic = true;
			declStart = lastToken.getStartPos();
		}
		
		if(!tryConsume(DeeTokens.KW_IMPORT)) {
			return null;
		}
		declStart = updateIfNull(declStart, lastToken.getStartPos());
		
		ArrayList<IImportFragment> fragments = new ArrayList<IImportFragment>();
		
		do {
			IImportFragment fragment = parseImportFragment();
			assertNotNull(fragment);
			fragments.add(fragment);
		} while(tryConsume(DeeTokens.COMMA));
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		SourceRange sr = range(declStart, getCurrentParserPosition());
		boolean isTransitive = false;
		
		return connect(
			new DeclarationImport(arrayView(fragments, IImportFragment.class), isStatic, isTransitive, sr));
	}
	
	public IImportFragment parseImportFragment() {
		Token aliasId = null;
		ArrayList<String> packages = new ArrayList<String>(0);
		Token firstPackage = null;
		
		while(true) {
			Token id = consumeIdentifier();
			
			if(!isRecoveredId(id) && tryConsume(DeeTokens.DOT)) {
				packages.add(id.tokenSource);
				firstPackage = firstPackage == null ? id : firstPackage;
			} else if(packages.isEmpty() && tryConsume(DeeTokens.ASSIGN)) { // BUG here
				aliasId = id;
			} else {
				Token refStartToken = firstPackage != null ? firstPackage : id;
				RefModule refModule = new RefModule(arrayViewS(packages), id.tokenSource, sr(refStartToken, id)); 
				
				IImportFragment fragment;
				if(aliasId == null) {
					fragment = connect(new ImportContent(refModule));
				} else {
					fragment = connect(new ImportAlias(defUnitRaw(sr(aliasId, id), aliasId), refModule));
				}
				
				if(tryConsume(DeeTokens.COLON)) {
					return parseSelectiveModuleImport(fragment);
				}
				
				return fragment;
			}
		}
	}
	
	public ImportSelective parseSelectiveModuleImport(IImportFragment fragment) {
		ArrayList<ASTNeoNode> selFragments = new ArrayList<ASTNeoNode>();
		
		while(true) {
			Token aliasId = null;
			Token id = consumeIdentifier();
			
			if(tryConsume(DeeTokens.ASSIGN)){ // BUG here
				aliasId = id;
				id = consumeIdentifier();
			} 
				
			RefImportSelection refImportSelection = connect(new RefImportSelection(id.tokenSource, sr(id)));
			if(aliasId == null) {
				selFragments.add(refImportSelection);
			} else {
				selFragments.add(connect(new ImportSelectiveAlias(
					defUnitTuple(sr(aliasId, id), aliasId, null), refImportSelection)));
			}
			
			if(tryConsume(DeeTokens.COMMA)){
				continue;
			} else {
				break;
			}
		}
		
		int endPos = lastElement(selFragments).getEndPos();
		
		SourceRange isRange = range(fragment.getStartPos(), endPos);
		return connect(new ImportSelective(fragment, arrayView(selFragments, ASTNeoNode.class), isRange));
	}
	
	/* --------------------- DECLARATION --------------------- */
	public static String DECLARATION_RULE = "declaration";
	
	public ASTNeoNode parseDeclaration(DeeTokens nodeListTerminator) {
		while(true) {
			DeeTokens la = assertNotNull_(lookAhead());
			
			if(la == DeeTokens.EOF || la == nodeListTerminator) {
				return null;
			}
			
			switch (la) {
			case KW_MIXIN: return parseMixinStringDeclaration();
			
			case KW_EXTERN: return parseDeclarationExternLinkage();
			case KW_ALIGN: return parseDeclarationAlign();
			case KW_PRAGMA:
				
			case KW_STATIC: 
				if(lookAhead(/*1*/) == DeeTokens.KW_IMPORT) { // TODO 
					return parseImportDecl();
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
			
			case KW_AUTO: // TODO:
				
			default:
				break;
			}
			
			if(la == DeeTokens.KW_IMPORT || la == DeeTokens.KW_STATIC) {
				return parseImportDecl();
			} else if(la == DeeTokens.IDENTIFIER || la == DeeTokens.KW_VOID || la == DeeTokens.KW_INT 
				|| la == DeeTokens.ASSIGN || la == DeeTokens.DOT) {
				return MiscDeclaration.parseMiscDeclaration(this);
			} else if(tryConsume(DeeTokens.SEMICOLON)) {
				return connect(new DeclarationEmpty(sr(lastToken)));
			} else {
				reportSyntaxError(lookAheadToken(), DECLARATION_RULE);
				return connect(MiscDeclaration.parseMiscDeclaration(this));
			}
		}
	}
	
	public final SourceRange srToLastToken(int declStart) {
		return range(declStart, getLastTokenEndPos());
	}
	
	public final int getLastTokenEndPos() {
		return lastToken.getEndPos();
	}
	
	public class AttribParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public NodeList2 declList;
		
		public AttribParseRule parseAttribBody() {
			DeeTokens bodyListTerminator = null;
			
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
			} else if(tryConsume(DeeTokens.OPEN_BRACE)) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				bodyListTerminator = DeeTokens.CLOSE_BRACE;
			}
			declList = parseDeclList(bodyListTerminator);
			
			if(bodyListTerminator != null) {
				consumeExpectedToken(bodyListTerminator);
			}
			return this;
		}
	}
	
	public NodeList2 parseDeclList(DeeTokens bodyListTerminator) {
		int nodeListStart = getLastTokenEndPos();
		
		ArrayView<ASTNeoNode> declDefs = parseDeclDefs(bodyListTerminator);
		NodeList2 nodeList = new NodeList2(declDefs, srToLastToken(nodeListStart));
		return nodeList;
	}
	
	public DeclarationLinkage parseDeclarationExternLinkage() {
		if(!tryConsume(DeeTokens.KW_EXTERN)) {
			return null;
		}
		int declStart = lastToken.getStartPos();
		
		Linkage linkage = null;
		
		if(tryConsume(DeeTokens.OPEN_PARENS)) {
			Token linkageId = consumeIf(DeeTokens.IDENTIFIER);
			if(linkageId != null) {
				linkage = Linkage.fromString(linkageId.tokenSource);
				if(linkage == Linkage.C && tryConsume(DeeTokens.INCREMENT)) {
					linkage = Linkage.CPP;
				}
			}
			if(linkage == null) {
				reportMissingTokenError(EDeeParserErrors.INVALID_EXTERN_ID, null);
			}
			
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null) {
				SourceRange sr = srToLastToken(declStart);
				return connect(new DeclarationLinkage(linkage, AttribBodySyntax.SINGLE_DECL, null, sr));
			}
		}
		
		AttribParseRule attribPR = new AttribParseRule();
		attribPR.parseAttribBody();
		
		return connect(
			new DeclarationLinkage(linkage, attribPR.bodySyntax, attribPR.declList, srToLastToken(declStart)));
	}
	
	public DeclarationAlign parseDeclarationAlign() {
		if(!tryConsume(DeeTokens.KW_ALIGN)) {
			return null;
		}
		int declStart = lastToken.getStartPos();
		
		int align = -1;
		
		if(tryConsume(DeeTokens.OPEN_PARENS)) {
			Token alignNum = consumeExpectedToken(DeeTokens.INTEGER);
			if(alignNum != null) {
				try {
					align = Integer.parseInt(alignNum.tokenSource);
				} catch(NumberFormatException e) {
					// TODO report error
				}
			}
			
			Token expectedToken = consumeExpectedToken(DeeTokens.CLOSE_PARENS);
			if(expectedToken == null) {
				SourceRange sr = srToLastToken(declStart);
				return connect(new DeclarationAlign(align, AttribBodySyntax.SINGLE_DECL, null, sr));
			}
		}
		
		AttribParseRule attribRule = new AttribParseRule();
		attribRule.parseAttribBody();
		
		return connect(
			new DeclarationAlign(align, attribRule.bodySyntax, attribRule.declList, srToLastToken(declStart)));
	}
	
	public DeclarationPragma parseDeclarationPragma() {
		if(!tryConsume(DeeTokens.KW_PRAGMA)) {
			return null;
		}
		int declStart = lastToken.getStartPos();
		
		Token pragmaId = null;
		if(tryConsume(DeeTokens.OPEN_PARENS)) {
			pragmaId = consumeIdentifier();
			if(!isRecoveredId(pragmaId)) {
			}
			
			// TODO pragma argument list;
			Token expectedToken = consumeExpectedToken(DeeTokens.CLOSE_PARENS);
			if(expectedToken == null) {
				SourceRange sr = srToLastToken(declStart);
				return connect(new DeclarationPragma(symbol(pragmaId), null, AttribBodySyntax.SINGLE_DECL, null, sr));
			}
		}
		// BUG standalone pragma
		
		AttribParseRule apr = new AttribParseRule();
		apr.parseAttribBody();
		
		// BUG here: connect(
		return new DeclarationPragma(symbol(pragmaId), null, apr.bodySyntax, apr.declList, srToLastToken(declStart));
	}
	
	public Symbol symbol(Token pragmaId) {
		// BUG here
//		return pragmaId == null ? null : new Symbol(pragmaId.tokenSource, sr(pragmaId));
		return new Symbol(pragmaId.tokenSource, sr(pragmaId));
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
		int declStart = lastToken.getStartPos();
		Protection protection = Protection.fromToken(lastToken.type);
		
		AttribParseRule apr = new AttribParseRule().parseAttribBody();
		// BUG here: connect(
		return new DeclarationProtection(protection, apr.bodySyntax, apr.declList, srToLastToken(declStart));
	}
	
	public DeclarationStorageClass parseDeclarationBasicAttrib() {
		EDeclarationAttribute attrib = EDeclarationAttribute.fromToken(lookAhead());
		if(attrib == null) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastToken.getStartPos();
		
		AttribParseRule apr = new AttribParseRule().parseAttribBody();
		// BUG here: connect(
		return new DeclarationStorageClass(attrib, apr.bodySyntax, apr.declList, srToLastToken(declStart));
	}
	
	
	/* ----------------------------------------- */
	
	public DeclarationMixinString parseMixinStringDeclaration() {
		if(!tryConsume(DeeTokens.KW_MIXIN)) {
			return null;
		}
		int declStart = lastToken.getStartPos();
		Expression exp = null;
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			// TODO rest strings BUG here
			if(tryConsume(DeeTokens.STRING_DQ)) {
				Token expToken = lastToken;
				exp = connect(new ExpLiteralString(expToken, sr(expToken)));
			} else {
				reportErrorExpectedRule(EXPRESSION_RULE);
			}
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		return connect(new DeclarationMixinString(exp, srToLastToken(declStart)));
	}
	
	// TODO:
	
	public static String EXPRESSION_RULE = "expression";

}