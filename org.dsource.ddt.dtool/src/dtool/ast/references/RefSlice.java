package dtool.ast.references;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.resolver.api.DefUnitDescriptor;
import dtool.resolver.api.IModuleResolver;

public class RefSlice extends Reference {
	
	public final Reference slicee;
	public final Expression startIndex;
	public final Expression endIndex;
	
	public RefSlice(Reference slicee, Expression startIndex, Expression endIndex) {
		this.slicee = parentize(slicee);
		this.startIndex = parentize(assertNotNull_(startIndex));
		this.endIndex = parentize(assertNotNull_(endIndex));
		assertTrue((endIndex == null) || (startIndex != null));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_SLICE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, slicee);
			TreeVisitor.acceptChildren(visitor, startIndex);
			TreeVisitor.acceptChildren(visitor, endIndex);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(slicee, "[");
		cp.append(startIndex);
		cp.append(" .. ", endIndex);
		cp.append("]");
	}

	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		return false;
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return null; // TODO:
	}
}