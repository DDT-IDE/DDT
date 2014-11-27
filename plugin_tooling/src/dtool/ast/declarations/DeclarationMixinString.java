package dtool.ast.declarations;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.expressions.ExpMixinString;
import dtool.ast.statements.IStatement;

/**
 * Parse exp contents as code (exp must resolve to a string).
 * http://dlang.org/module.html#MixinDeclaration
 */
public class DeclarationMixinString extends ASTNode implements INonScopedContainer, IDeclaration, IStatement {
	
	public final ExpMixinString exp;
	
	public DeclarationMixinString(ExpMixinString exp) {
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_MIXIN_STRING;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, exp);
	}
	
	@Override
	public Iterable<? extends IASTNode> getMembersIterable() {
		// TODO: parse the exp string
		return IteratorUtil.emptyIterable();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(exp);
		cp.append(";");
	}
	
}