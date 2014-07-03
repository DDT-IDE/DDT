package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.LanguageIntrinsics;

public class RefTypeDynArray extends CommonRefNative {
	
	public final Reference elemtype;
	
	public RefTypeDynArray(Reference elemType) {
		this.elemtype = parentize(elemType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPE_DYN_ARRAY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, elemtype);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(elemtype, "[]");
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Resolvable.wrapResult(LanguageIntrinsics.d_2_063_intrinsics.dynArrayType);
	}
	
}