package dtool.descentadapter;

import descent.internal.compiler.parser.Comment;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.parser.Token;

@Deprecated
public final class DefUnitTuple {
	public final SourceRange sourceRange;
	public final Comment[] comments;
	public final ProtoDefSymbol defSymbol;
	
	public DefUnitTuple(Comment[] comments, String name, SourceRange nameSourceRange, 
		@Deprecated SourceRange sourceRange) {
		this.defSymbol = new ProtoDefSymbol(name, nameSourceRange, null);
		this.comments = comments;
		this.sourceRange = sourceRange;
	}
	
	public DefUnitTuple(SourceRange sourceRange, TokenInfo defName, Comment[] comments) {
		this(comments, defName.getString(), defName.getSourceRange(), sourceRange);
	}

	public Token[] commentsToToken() {
		// TODO Auto-generated method stub
		return null;
	}
	
}