package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public class DeclarationMixinString extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public final Resolvable exp;
	
	public DeclarationMixinString(Resolvable exp) {
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
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		// TODO: parse the exp string
		return IteratorUtil.getEMPTY_ITERATOR();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("mixin(");
		cp.appendNode(exp);
		cp.append(");");
	}
	
}