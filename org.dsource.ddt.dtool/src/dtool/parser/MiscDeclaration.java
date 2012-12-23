package dtool.parser;

import java.util.ArrayList;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class MiscDeclaration extends ASTNeoNode {
	
	public final ArrayList<Token> tokenList;
	
	public static MiscDeclaration parseMiscDeclaration(DeeParser deeParser) {
		final ArrayList<Token> tokenList = new ArrayList<Token>();
		
		int startPos = deeParser.lookAheadToken().getStartPos();
		Token token;
		do {
			deeParser.lookAhead();
			token = deeParser.consumeLookAhead();
			tokenList.add(token);
		} while(!(token.type == DeeTokens.SEMICOLON || token.type == DeeTokens.EOF));
		
		return new MiscDeclaration(tokenList, deeParser.range(startPos, deeParser.lastToken.getEndPos()));
	}
	
	public MiscDeclaration(ArrayList<Token> tokenList, SourceRange sourceRange) {
		this.tokenList = tokenList;
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		for (Token token : tokenList) {
			cp.append(token.value);
		}
	}
	
}