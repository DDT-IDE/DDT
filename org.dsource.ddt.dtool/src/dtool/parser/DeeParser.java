package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.ImportFragment;
import dtool.ast.declarations.ImportContent;
import dtool.ast.definitions.DefUnit.DefUnitDataTuple;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefModule;
import dtool.util.ArrayView;

public class DeeParser extends AbstractDeeParser {
	
	public DeeParser(DeeLexer deeLexer) {
		super(deeLexer);
	}
	
	public static DeeParserResult parse(String source) {
		DeeParser deeParser = new DeeParser(new DeeLexer(source));
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
		assertTrue(idToken.tokenType == DeeTokens.IDENTIFIER);
		return new TokenInfo(idToken.value, idToken.getStartPos());
	}
	
	
	public DefUnitDataTuple defUnitCommon(SourceRange sourceRange, Token id, Comment[] comments) {
		return new DefUnitDataTuple(sourceRange, tokenInfo(id), comments);
	}

	public static <T extends ASTNeoNode> ArrayView<T> arrayView(Collection<? extends T> list, Class<T> cpType) {
		return ArrayView.create(ArrayUtil.createFrom(list, cpType));
	}
	
	/* ----------------------------------------------------------------- */
	
	public Module parseModule() {
		
		Token moduleId = null;
		String[] packages = null;
		
		if(tryConsume(DeeTokens.KW_MODULE)) {
			
			packages = new String[0];
			
			while(true) {
				Token id = consumeExpectedToken(DeeTokens.IDENTIFIER);
				if(id == null) {
					moduleId = missingIdToken(lookAhead().getStartPos());
					recoverStream(DeeTokens.IDENTIFIER, DeeTokens.SEMICOLON);
					break;
				}
				
				if(tryConsume(DeeTokens.SEMICOLON)) {
					moduleId = id;
					break;
				} else if(tryConsume(DeeTokens.DOT)) {
					packages = ArrayUtil.append(packages, id.value);
					
				} else {
					moduleId = id;
					
					pushSyntaxErrorAfter();
					break;
				}
			}
		}
		
		ArrayView<ASTNeoNode> members = parseModuleDecls();
		assertTrue(lookAhead().tokenType == DeeTokens.EOF);
		assertTrue(lookAhead().getEndPos() == deeLexer.source.length());
		
		SourceRange modRange = new SourceRange(0, deeLexer.source.length());
		
		if(packages != null) {
			SourceRange modDeclRange = new SourceRange(0, lastToken.getEndPos()); // BUG here
			return Module.createModule(modRange, null, packages, tokenInfo(moduleId), modDeclRange, members);
		} else {
			return Module.createModuleNoModuleDecl(modRange, "__tests_unnamed", members);
		}
	}
	
	protected Token missingIdToken(int startPos) {
		// TODO, review this behavior
		return new Token(DeeTokens.IDENTIFIER, "", startPos);
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
	
	private ASTNeoNode parseDecl() {
		while(true) {
			DeeTokens la = lookAhead().tokenType;
			
			if(la == DeeTokens.EOF) {
				return null;
			}
			
			if(la == DeeTokens.KW_VOID || la == DeeTokens.KW_VOID) {
				Token type = consumeLookAhead();
				Token id = consumeExpectedToken(DeeTokens.IDENTIFIER);
				Token end = consumeExpectedToken(DeeTokens.SEMICOLON);
				
				RefIdentifier ref = new RefIdentifier(type.value, sr(id)); // bug here
				
				return new DefinitionVariable(defUnitCommon(srFromToken(type, end), id, null), null, ref, null);
			} else if(la == DeeTokens.KW_IMPORT) {
				return parseImportDecl();
			} else {
				consumeLookAhead();
				
				addError(EDeeParserErrors.SYNTAX_ERROR, sr(lastToken), lastToken.value, 
					"parsing declaration.");
			}
		}
	}
	
	
	public DeclarationImport parseImportDecl() {
		
		if(!tryConsume(DeeTokens.KW_IMPORT)) {
			return null;
		}
		int declStart = lookAhead().getStartPos(); // bug here
//		int declStart = lastToken.getStartPos();
		
		
		ArrayList<ImportFragment> fragments;
		Token importId = null;
		
		fragments = new ArrayList<ImportFragment>();
		
		while(true) {
			Token id = consumeExpectedToken(DeeTokens.IDENTIFIER);
			if(id == null) {
				importId = missingIdToken(lookAhead().getStartPos());
				recoverStream(DeeTokens.IDENTIFIER, DeeTokens.SEMICOLON);
				break;
			}
			
			if(tryConsume(DeeTokens.SEMICOLON)) {
				importId = id;
				
				RefModule refModule = new RefModule(null, importId.value, sr(importId));
				fragments.add(new ImportContent(refModule, sr(id)));
				
				break;
			} else if(tryConsume(DeeTokens.COLON)) {
				
			} else {
				//moduleId = id; // BUG here
				
				pushSyntaxErrorAfter();
				//bug here
				//break;
			}
		}
		
		SourceRange sr = new SourceRange(declStart, lastToken.getEndPos());
		
		return new DeclarationImport(arrayView(fragments, ImportFragment.class), false, false, sr);
	}
	
}