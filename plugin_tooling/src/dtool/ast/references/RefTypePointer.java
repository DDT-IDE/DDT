package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.LanguageIntrinsics;

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
		return Resolvable.wrapResult(LanguageIntrinsics.d_2_063_intrinsics.pointerType);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(elemType, "*");
	}
	
}