package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TypeTypeof extends CommonRefNative {
	
	public final Expression expression;
	
	public TypeTypeof(Expression exp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.expression = parentize(exp);
	}
	
	public TypeTypeof(Expression exp) {
		this.expression = exp;
		
		if (this.expression != null)
			this.expression.setParent(this);
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
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return expression.getType(moduleResolver);
	}
	
}