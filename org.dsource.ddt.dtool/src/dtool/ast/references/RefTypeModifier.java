package dtool.ast.references;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.parser.DeeTokens;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class RefTypeModifier extends Reference implements IQualifierNode {
	
	public static enum TypeModifierKinds {
		CONST(DeeTokens.KW_CONST),
		IMMUTABLE(DeeTokens.KW_IMMUTABLE),
		SHARED(DeeTokens.KW_SHARED),
		INOUT(DeeTokens.KW_INOUT),
		;
		public final String sourceValue;
		
		TypeModifierKinds(DeeTokens token) {
			sourceValue = token.getSourceValue();
		}
	}
	
	public final TypeModifierKinds modifier;
	public final Reference ref;
	
	public RefTypeModifier(TypeModifierKinds modifier, Reference ref) {
		this.modifier = assertNotNull_(modifier);
		this.ref = parentize(ref);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_MODIFIER;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ref);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(modifier.sourceValue);
		cp.append("(", ref, ")");
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		/*BUG here null*/
		return ref.findTargetDefUnits(moduleResolver, findFirstOnly);
	}
	
	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		return false;
	}
	
}