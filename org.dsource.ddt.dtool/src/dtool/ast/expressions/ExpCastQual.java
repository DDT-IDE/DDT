package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class ExpCastQual extends Expression {
	
	public static enum CastQualifiers {
		CONST("const"),
		CONST_SHARED("const shared"),
		INOUT("inout"),
		INOUT_SHARED("inout shared"),
		SHARED("shared"),
		SHARED_CONST("shared const"),
		SHARED_INOUT("shared inout"),
		IMMUTABLE("immutable"),
		;
		
		private String sourceValue;
		
		private CastQualifiers(String sourceValue) {
			this.sourceValue = sourceValue;
		}
		public String toStringAsCode() {
			return sourceValue;
		}
	}
	
	public final CastQualifiers castQualifier;
	public final Resolvable exp;
	
	public ExpCastQual(CastQualifiers castQualifier, Expression exp) {
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
		cp.appendStrings("(", castQualifier.toStringAsCode(), ")");
		cp.append(exp);
	}
	
}