package dtool.descentadapter;

import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.IASTVisitor;
import dtool.ast.ASTNeoNode;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.parser.DeeTokens;
import dtool.parser.Token;

/**
 * This class is a mixin. 
 * Do not use it, instead use it's subclass: {@link DeclarationConverterVisitor}
 */
public abstract class ASTCommonConverter implements IASTVisitor {
	
	protected ASTConversionContext convContext;
	
	ASTNeoNode ret = null;
	
	ASTNeoNode convert(ASTNode elem) {
		elem.accept(this);
		return ret;
	}
	
	@Override
	public void postVisit(ASTNode elem) {
	}
	@Override
	public boolean preVisit(ASTNode elem) {
		return true;
	}
	
	
	/* ---- common adaptors ---- */
	
	protected boolean endAdapt(ASTNeoNode newelem) {
		ret = newelem;
		return false;
	}
	
	protected boolean assertFailFAKENODE() {
		Assert.fail("Fake Node"); return false;
	}
	protected boolean assertFailABSTRACT_NODE() {
		Assert.fail("Abstract Node"); return false;
	}
	protected boolean assertFailHandledDirectly() {
		Assert.fail("This class is not converted directly by the visitor. ");
		return true;
	}
	
	public static Token makeToken(DeeTokens tokenType, char[] source, int offset) {
		return new Token(tokenType, source == null ? "" : new String(source), offset);
	}
	
}