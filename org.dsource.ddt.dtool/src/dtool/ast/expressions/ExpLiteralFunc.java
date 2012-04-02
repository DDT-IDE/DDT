package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;

public class ExpLiteralFunc extends Expression {
	
	public final Reference rettype;
	public final ArrayView<IFunctionParameter> params;
	public final int varargs;

	public final IStatement frequire;
	public final IStatement fbody;
	public final IStatement fensure;

	public ExpLiteralFunc(Reference retType, IFunctionParameter[] params, int varargs, IStatement freq,
			IStatement fbody, IStatement fensure, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		
		this.frequire = freq; parentize(this.frequire); 
		this.fbody = fbody; parentize(this.fbody);
		this.fensure = fensure; parentize(this.fensure);
		this.params = new ArrayView<IFunctionParameter>(params); parentize(this.params);
		this.varargs = varargs;
		this.rettype = retType; parentize(this.rettype);
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
