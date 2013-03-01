package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TypeTypeof extends CommonRefNative implements IQualifierNode {
	
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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, expression);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("typeof");
		cp.appendNode("(", expression, ")");
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return expression.getType(moduleResolver);
	}
	
}