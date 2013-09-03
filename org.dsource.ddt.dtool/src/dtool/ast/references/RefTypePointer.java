package dtool.ast.references;

import java.util.Collection;

import descent.core.ddoc.Ddoc;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.IntrinsicDefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.api.IModuleResolver;

public class RefTypePointer extends CommonRefNative {
	
	public final Reference elemType;
	
	public RefTypePointer(Reference elemType) {
		this.elemType = parentize(elemType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPE_POINTER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, elemType);
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Resolvable.wrapResult(IntrinsicPointer.instance);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(elemType, "*");
	}
	
	public static class IntrinsicPointer extends IntrinsicDefUnit {
		public IntrinsicPointer() {
			super("<pointer>");
		}
		
		public static final IntrinsicPointer instance = new IntrinsicPointer();
		
		@Override
		public Ddoc resolveDDoc() {
			return null; // TODO
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			// TODO
		}
	}
	
}