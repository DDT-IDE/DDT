package dtool.descentadapter;

import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.ast.IASTVisitor;
import dtool.ast.ASTNode;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclBlock;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.parser.DeeTokens;
import dtool.parser.Token;
import dtool.util.ArrayView;

/**
 * This class is a mixin. 
 * Do not use it, instead use it's subclass: {@link DeclarationConverterVisitor}
 */
public abstract class ASTCommonConverter implements IASTVisitor {
	
	protected ASTConversionContext convContext;
	
	ASTNode ret = null;
	
	ASTNode convert(descent.internal.compiler.parser.ast.ASTNode elem) {
		elem.accept(this);
		return ret;
	}
	
	@Override
	public void postVisit(descent.internal.compiler.parser.ast.ASTNode elem) {
	}
	@Override
	public boolean preVisit(descent.internal.compiler.parser.ast.ASTNode elem) {
		return true;
	}
	
	protected static <T extends ASTNode> T connect(SourceRange sourceRange, T node) {
		if(sourceRange != null) {
			node.setSourceRange(sourceRange);
		}
		return node;
	}
	
	/* ---- common adaptors ---- */
	
	protected boolean endAdapt(ASTNode newelem) {
		ret = newelem;
		return false;
	}
	protected boolean endAdapt(SourceRange sourceRange, ASTNode newelem) {
		if(sourceRange != null) {
			newelem.setSourceRange(sourceRange);
		}
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
	
	public DeclBlock createDeclList(ArrayView<ASTNode> elems) {
		return elems == null ? null : new DeclBlock(elems);
	}
	
}