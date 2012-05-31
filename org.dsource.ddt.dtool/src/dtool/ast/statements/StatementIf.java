package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.IfStatement;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementIf extends Statement {

	public Resolvable pred;
	public IStatement thenbody;
	public IStatement elsebody;

	public StatementIf(IfStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.pred = ExpressionConverter.convert(elem.condition, convContext);
		this.thenbody = Statement.convert(elem.ifbody, convContext);
		this.elsebody = Statement.convert(elem.elsebody, convContext);
	}
	
	public StatementIf(Resolvable pred, IStatement thenBody, IStatement elseBody) {
		this.pred = pred;
		this.thenbody = thenBody;
		this.elsebody = elseBody;
		
		if (this.pred != null)
			this.pred.setParent(this);
		if (this.thenbody != null)
			((ASTNeoNode) this.thenbody).setParent(this);
		if (this.elsebody != null)
			((ASTNeoNode) this.elsebody).setParent(this);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, pred);
			TreeVisitor.acceptChildren(visitor, thenbody);
			TreeVisitor.acceptChildren(visitor, elsebody);
		}
		visitor.endVisit(this);
	}

}
