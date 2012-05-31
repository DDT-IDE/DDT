package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.SliceExp;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;
import dtool.refmodel.IDefUnitReferenceNode;

public class ExpSlice extends Expression {

	public IDefUnitReferenceNode slicee;
	public Resolvable from;
	public Resolvable to;
	
	public ExpSlice(SliceExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		slicee = ExpressionConverter.convert(elem.e1, convContext);
		from = ExpressionConverter.convert(elem.lwr, convContext);
		to = ExpressionConverter.convert(elem.upr, convContext);
	}
	
	public ExpSlice(Resolvable slicee, Resolvable from, Resolvable to, SourceRange sourceRange) {
		this.slicee = slicee;
		this.from = from;
		this.to = to;
		setSourceRange(sourceRange);
		
		if (this.slicee != null)
			((ASTNeoNode) this.slicee).setParent(this);
		if (this.from != null)
			this.from.setParent(this);
		if (this.to != null)
			this.to.setParent(this);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, slicee);
			TreeVisitor.acceptChildren(visitor, from);
			TreeVisitor.acceptChildren(visitor, to);
		}
		visitor.endVisit(this);
	}

}
