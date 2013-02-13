package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.util.ArrayView;

public class ExpLiteralFunc extends Expression {
	
	public final Reference retType;
	public final ArrayView<IFunctionParameter> params;
	public final int varargs;
	
	public final IStatement frequire;
	public final IStatement fbody;
	public final IStatement fensure;
	
	
	public ExpLiteralFunc(Reference retType, ArrayView<IFunctionParameter> params, int varargs, IStatement freq,
			IStatement fbody, IStatement fensure, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		
		this.frequire = parentizeI(freq); 
		this.fbody = parentizeI(fbody);
		this.fensure = parentizeI(fensure);
		this.params = parentizeI(params);
		this.varargs = varargs;
		this.retType = parentize(retType);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, retType);
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, frequire);
			TreeVisitor.acceptChildren(visitor, fbody);
			TreeVisitor.acceptChildren(visitor, fensure);
		}
		visitor.endVisit(this);	 
	}
	
}