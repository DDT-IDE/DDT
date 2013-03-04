package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.parser.AbstractParser;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class RefTypeof extends CommonRefNative implements IQualifierNode, ITemplateRefNode /*BUG here*/ {
	
	public final Expression expression;
	
	public RefTypeof(Expression exp, SourceRange sourceRange) {
		this.expression = parentize(exp);
		if(exp instanceof ExpRefReturn) {
			exp.setData(AbstractParser.PARSED_STATUS);
		}
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPEOF;
	}
	
	public static class ExpRefReturn extends Expression {
		
		public ExpRefReturn(SourceRange sourceRange) {
			initSourceRange(sourceRange);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.EXP_REF_RETURN;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			visitor.visit(this);
			visitor.endVisit(this);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append("return");
		}
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