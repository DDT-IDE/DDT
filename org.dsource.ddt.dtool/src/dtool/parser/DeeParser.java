package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.Module;
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
	
	/* ----------------------------------------------------------------- */
	
	public Module parseModule() {
		Token la = lookAhead();
		
		String[] packages = new String[0];
		
		Token moduleId = null;
		if(la.tokenType == DeeTokens.KW_MODULE) {
			consumeInput();
			
			while(true) {
				
				Token id = consumeToken(DeeTokens.IDENTIFIER);
				if(id == null) {
					//ERROR RECOVERY
					consumeInputUntil(DeeTokens.SEMICOLON);
					break;
				}
				
				if(lookAhead().tokenType == DeeTokens.SEMICOLON) {
					moduleId = id;
					consumeToken(DeeTokens.SEMICOLON);
					break;
				} if(lookAhead().tokenType == DeeTokens.DOT) {
					consumeToken(DeeTokens.DOT);
					packages = ArrayUtil.append(packages, id.value);
					
				} else {
					//moduleId = id; // BUG here
					
					//ERROR RECOVERY
					consumeInputUntil(DeeTokens.SEMICOLON);
				}
			}
		}
		
		ArrayView<ASTNeoNode> members = ArrayView.create(new ASTNeoNode[0]);
		
		
		consumeInputUntil(DeeTokens.EOF);
		SourceRange modRange = new SourceRange(0, lastToken.getEndPos());
		SourceRange modDeclRange = new SourceRange(0, lastToken.getEndPos()); //BUG here
		
		if(moduleId != null) {
			return Module.createModule(modRange, null, packages, tokenInfo(moduleId), modDeclRange, members);
		} else {
			return Module.createModuleNoModuleDecl(modRange, "__tests_undefined", members);
		}
		
	}
	
	public TokenInfo tokenInfo(Token idToken) {
//		if(idToken == null) {
//			return null;
//		}
		assertTrue(idToken.tokenType == DeeTokens.IDENTIFIER);
		return new TokenInfo(idToken.value, idToken.getStartPos());
	}
	
}