package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

public class ExpCastQual extends Expression {
	
	public static enum CastQualifiers {
		 CONST,
		 CONST_SHARED,
		 INOUT,
		 INOUT_SHARED,
		 SHARED,
		 SHARED_CONST,
		 SHARED_INOUT,
		 IMMUTABLE,
		 ;

		public String toStringAsCode() {
			return toString().toLowerCase().replace("_", " ");
		}
	}
	
	public final CastQualifiers castQualifier;
	public final Resolvable exp;
	
	public ExpCastQual(CastQualifiers castQualifier, Expression exp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.castQualifier = assertNotNull_(castQualifier);
		this.exp = parentize(exp);
		assertTrue(exp == null || castQualifier != null);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_CAST_QUAL;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if(children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("cast");
		cp.append("(", castQualifier.toStringAsCode(), ")");
		cp.append(exp);
	}
	
}