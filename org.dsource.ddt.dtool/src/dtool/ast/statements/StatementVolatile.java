package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.VolatileStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class StatementVolatile extends Statement {
	
	public IStatement st;

	public StatementVolatile(VolatileStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.st = Statement.convert(elem.statement, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}

}
