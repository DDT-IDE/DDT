package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NeoSourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;

public class TypeTypeof extends CommonRefNative {
	
	public final Expression expression;
	
	public TypeTypeof(Expression exp, NeoSourceRange sourceRange) {
		this.expression = exp;
		maybeSetSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, expression);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "typeof(" + expression.toStringAsElement() +")";
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return expression.getType();
	}
	
}