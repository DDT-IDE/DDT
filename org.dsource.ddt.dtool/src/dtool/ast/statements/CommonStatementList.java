package dtool.ast.statements;


import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.IASTVisitor;
import dtool.util.ArrayView;

public abstract class CommonStatementList extends Statement {
	
	public final ArrayView<IStatement> statements;
	
	public CommonStatementList(ArrayView<IStatement> statements) {
		this.statements = parentizeI(assertNotNull_(statements));
	}
	
	/** This represents a missing block */
	public CommonStatementList() {
		this.statements = null;
	}
	
	public final ArrayView<ASTNode> statements_asNodes() {
		return CoreUtil.<ArrayView<ASTNode>>blindCast(statements);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
			TreeVisitor.acceptChildren(visitor, statements);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		if(statements == null) {
			cp.append(" ");
			return;
		}
		cp.append("{");
		cp.appendList("\n", statements_asNodes(), "\n", "\n");
		cp.append("}");
	}
	
	public Iterator<? extends ASTNode> getMembersIterator() {
		return statements_asNodes().iterator(); //TODO: latent NPE bug here
	}
	
}