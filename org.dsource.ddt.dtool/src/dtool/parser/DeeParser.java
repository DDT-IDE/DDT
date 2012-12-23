package dtool.parser;

import static dtool.util.NewUtils.lastElement;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.definitions.DefUnit.DefUnitTuple;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
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
		return deeParser.parseInput();
	}
	
	protected DeeParserResult parseInput() {
		Module module = parseModule();
		DeeParserResult parseResult = new DeeParserResult(module, errors);
		errors = null;
		return parseResult;
	}
	
	public TokenInfo tokenInfo(Token idToken) {
		if(idToken == null) {
			return null;
		}
		assertTrue(idToken.type == DeeTokens.IDENTIFIER);
		return new TokenInfo(idToken.value, idToken.getStartPos());
	}
	
	public Token consumeIdentifier() {
		Token id = consumeExpectedToken(DeeTokens.IDENTIFIER);
		if(id == null) {
			id = missingIdToken(lookAheadToken().getStartPos());
		}
		return id;
	}
	
	public static String MISSING_ID_NAME = "";
	
	protected Token missingIdToken(int startPos) {
		return new Token(DeeTokens.IDENTIFIER, MISSING_ID_NAME, startPos) {
			@Override
			public int getLength() {
				return 0;
			}
			
			@Override
			public int getEndPos() {
				return start;
			}
		};
	}
	
	public static boolean isRecoveredId(Token id) {
		return id.value == MISSING_ID_NAME;
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
		
		ArrayView<ASTNeoNode> members = parseModuleDecls();
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
				endPos = lastToken.getEndPos();
				break;
			} else if(!isRecoveredId(id) && tryConsume(DeeTokens.DOT)) {
				packagesList.add(id.value);
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
	
	
	public ArrayView<ASTNeoNode> parseModuleDecls() {
		ArrayList<ASTNeoNode> moduleDecls = new ArrayList<ASTNeoNode>();
		while(true) {
			ASTNeoNode decl = parseDecl();
			if(decl == null) { 
				break;
			}
			moduleDecls.add(decl);
		}
		
		return ArrayView.create(ArrayUtil.createFrom(moduleDecls, ASTNeoNode.class));
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
		int declEnd;
		
		ArrayList<IImportFragment> fragments = new ArrayList<IImportFragment>();
		
		while(true) {
			IImportFragment fragment = parseImportFragment();
			assertNotNull(fragment);
			fragments.add(fragment);
			
			if(tryConsume(DeeTokens.COMMA)) {
				continue;
			} else if(tryConsume(DeeTokens.SEMICOLON)) {
				declEnd = lastToken.getEndPos();
				break;
			} else {
				declEnd = fragment.getEndPos();
				reportErrorExpectedToken(DeeTokens.SEMICOLON);
				break;
			}
		}
		
		SourceRange sr = range(declStart, declEnd);
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
				packages.add(id.value);
				firstPackage = firstPackage == null ? id : firstPackage;
			} else if(packages.isEmpty() && tryConsume(DeeTokens.ASSIGN)) { // BUG here
				aliasId = id;
			} else {
				Token refStartToken = firstPackage != null ? firstPackage : id;
				RefModule refModule = new RefModule(arrayViewS(packages), id.value, sr(refStartToken, id)); 
				
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
				
			RefImportSelection refImportSelection = connect(new RefImportSelection(id.value, sr(id)));
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
	
	public ASTNeoNode parseDecl() {
		while(true) {
			DeeTokens la = lookAhead();
			
			if(la == DeeTokens.EOF) {
				return null;
			}
			
			if(la == DeeTokens.KW_IMPORT || la == DeeTokens.KW_STATIC) {
				return parseImportDecl();
			} else if(la == DeeTokens.IDENTIFIER || la == DeeTokens.KW_VOID || la == DeeTokens.KW_INT 
				|| la == DeeTokens.ASSIGN || la == DeeTokens.DOT) {
				return MiscDeclaration.parseMiscDeclaration(this);
			} else {
				consumeLookAhead();
				
				addError(EDeeParserErrors.TOKEN_SYNTAX_ERROR, sr(lastToken), lastToken.value, 
					"parsing declaration.");
			}
		}
	}
	
}