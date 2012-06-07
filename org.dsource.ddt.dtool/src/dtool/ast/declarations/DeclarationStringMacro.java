package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public class DeclarationStringMacro extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public final Resolvable exp;

	public DeclarationStringMacro(Resolvable exp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = exp; parentize(this.exp);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
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

}
