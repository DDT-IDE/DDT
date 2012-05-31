package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;

public class ExpLiteralFunc extends Expression {
	
	public final Reference rettype;
	public final IFunctionParameter[] params;
	public final int varargs;

	public final IStatement frequire;
	public final IStatement fbody;
	public final IStatement fensure;

	public ExpLiteralFunc(Reference retType, IFunctionParameter[] params, int varargs, IStatement freq,
			IStatement fbody, IStatement fensure, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		
		this.frequire = freq;
		if (this.frequire != null)
			((ASTNeoNode) this.frequire).setParent(this);
		
		this.fbody = fbody;
		if (this.fbody != null)
			((ASTNeoNode) this.fbody).setParent(this);

		this.fensure = fensure;
		if (this.fensure != null)
			((ASTNeoNode) this.fensure).setParent(this);
		
		this.params = params;
		if (this.params != null) {
			for (IFunctionParameter fp : this.params) {
				((ASTNeoNode) fp).setParent(this);
			}
		}
		
		this.varargs = varargs;
		this.rettype = retType;
		if (this.rettype != null)
			this.rettype.setParent(this);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, frequire);
			TreeVisitor.acceptChildren(visitor, fbody);
			TreeVisitor.acceptChildren(visitor, fensure);
		}
		visitor.endVisit(this);	 
	}

}
