package dtool.ast.statements;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Iterator;

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.IASTVisitor;
import dtool.util.ArrayView;

public abstract class CommonStatementList extends Statement {
	
	public final ArrayView<IStatement> statements;
	
	public CommonStatementList(ArrayView<IStatement> statements) {
		this.statements = parentizeI(assertNotNull(statements));
	}
	
	/** This represents a missing block */
	public CommonStatementList() {
		this.statements = null;
	}
	
	public final ArrayView<ASTNode> statements_asNodes() {
		return CoreUtil.<ArrayView<ASTNode>>blindCast(statements);
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, statements);
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