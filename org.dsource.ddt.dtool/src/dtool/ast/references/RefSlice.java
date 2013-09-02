package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Expression;
import dtool.resolver.api.DefUnitDescriptor;
import dtool.resolver.api.IModuleResolver;

public class RefSlice extends Reference {
	
	public final Reference slicee;
	public final Expression startIndex;
	public final Expression endIndex;
	
	public RefSlice(Reference slicee, Expression startIndex, Expression endIndex) {
		this.slicee = parentize(slicee);
		this.startIndex = parentize(assertNotNull(startIndex));
		this.endIndex = parentize(assertNotNull(endIndex));
		assertTrue((endIndex == null) || (startIndex != null));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_SLICE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, slicee);
		acceptVisitor(visitor, startIndex);
		acceptVisitor(visitor, endIndex);
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
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return null; // TODO:
	}
}