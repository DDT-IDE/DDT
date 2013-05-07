package dtool.ast;

import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.ASTUpTreeVisitor;


/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and  
 * element.endPos (inclusive).
 */
@Deprecated
public abstract class ASTNodeFinderOld {
	
	public static descent.internal.compiler.parser.ast.ASTNode findElement(ASTNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	public static ASTNode findElement(final ASTNode root, int offset, boolean inclusiveEnd) {
		ASTNodeFinder<ASTNode> astNodeFinder = new ASTNodeFinder<ASTNode>() {
			@Override
			public void doAcceptOnRoot() {
				root.accept(new ASTUpTreeVisitor() {
					@Override
					public boolean visit(ASTNode node) {
						return visitNode(node);
					}
				});
			}
		};
		return astNodeFinder.doFindElementInAST(root, offset, inclusiveEnd);
	}

	protected abstract void doAcceptOnRoot();

	
}
