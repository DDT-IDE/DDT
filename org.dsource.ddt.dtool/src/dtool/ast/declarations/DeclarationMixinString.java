package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IStatement;
import dtool.resolver.INonScopedBlock;

/**
 * Parse exp contents as code (exp must resolve to a string).
 * http://dlang.org/module.html#MixinDeclaration
 */
public class DeclarationMixinString extends ASTNode implements INonScopedBlock, IDeclaration, IStatement {
	
	public final Expression exp;
	
	public DeclarationMixinString(Expression exp) {
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_MIXIN_STRING;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		// TODO: parse the exp string
		return IteratorUtil.getEMPTY_ITERATOR();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("mixin(");
		cp.append(exp);
		cp.append(");");
	}
	
}