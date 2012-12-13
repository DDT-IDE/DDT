package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.DefUnit.DefUnitDataTuple;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefIdentifier;
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
		return new DeeParserResult(module);
	}
	
	public TokenInfo tokenInfo(Token idToken) {
//		if(idToken == null) {
//			return null;
//		}
		assertTrue(idToken.tokenType == DeeTokens.IDENTIFIER);
		return new TokenInfo(idToken.value, idToken.getStartPos());
	}
	
	
	/* ----------------------------------------------------------------- */
	
	public Module parseModule() {
		Token la = lookAhead();
		
		String[] packages = new String[0];
		
		Token moduleId = null;
		if(tryConsume(DeeTokens.KW_MODULE)) {
			
			while(true) {
				Token id = consumeExpectedToken(DeeTokens.IDENTIFIER);
				if(id == null) {
					//ERROR RECOVERY
					consumeInputUntil(DeeTokens.SEMICOLON);
					break;
				}
				
				if(tryConsume(DeeTokens.SEMICOLON)) {
					moduleId = id;
					break;
				} else if(tryConsume(DeeTokens.DOT)) {
					packages = ArrayUtil.append(packages, id.value);
					
				} else {
					//moduleId = id; // BUG here
					
					//ERROR RECOVERY
					consumeInputUntil(DeeTokens.SEMICOLON);
				}
			}
		}
		
		ArrayView<ASTNeoNode> members = parseModuleDecls();
		
		
		consumeInputUntil(DeeTokens.EOF);
		SourceRange modRange = new SourceRange(0, lastToken.getEndPos());
		SourceRange modDeclRange = new SourceRange(0, lastToken.getEndPos()); // BUG here
		
		if(moduleId != null) {
			return Module.createModule(modRange, null, packages, tokenInfo(moduleId), modDeclRange, members);
		} else {
			return Module.createModuleNoModuleDecl(modRange, "__tests_unnamed", members);
		}
		
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
		DeeTokens la = lookAhead().tokenType;
		
		if(la == DeeTokens.KW_VOID || la == DeeTokens.KW_VOID) {
			Token type = consumeLookAhead();
			Token id = consumeExpectedToken(DeeTokens.IDENTIFIER);
			Token end = consumeExpectedToken(DeeTokens.COMMA);
			
			RefIdentifier ref = new RefIdentifier(type.value, srFromToken(id)); // bug here
			
			return new DefinitionVariable(defUnitCommon(srFromToken(type, end), id, null), null, ref, null);
		}
		return null;
	}
	
	public DefUnitDataTuple defUnitCommon(SourceRange sourceRange, Token id, Comment[] comments) {
		return new DefUnitDataTuple(srFromToken(id), tokenInfo(id), comments);
	}
	
}