package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.CaseStatement;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementCase extends Statement {

	public Resolvable exp;
	public IStatement st;
	
	public Resolvable[] expList;
	public IStatement[] stList;
	
	public StatementCase(CaseStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = ExpressionConverter.convert(elem.exp, convContext);
		this.st = Statement.convert(elem.statement, convContext);
	}
	
	public StatementCase(Resolvable[] expList, IStatement[] stList) {
		this.expList = expList;
		this.stList = stList;
		
		if (this.expList != null) {
			for (Resolvable e: this.expList) {
				e.setParent(this);
			}
		}
		
		if (this.stList != null) {
			((ASTNeoNode) this.st).setParent(this);
			for (IStatement s: this.stList) {
				((ASTNeoNode) s).setParent(this);
			}
		}
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, exp);
			for (Resolvable e : this.expList) {
				TreeVisitor.acceptChildren(visitor, e);
			}

			//TreeVisitor.acceptChildren(visitor, st);
			for (IStatement s: this.stList) {
				TreeVisitor.acceptChildren(visitor, s);
			}
		}
		visitor.endVisit(this);
	}

}
