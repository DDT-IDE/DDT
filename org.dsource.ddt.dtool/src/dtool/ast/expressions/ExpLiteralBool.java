package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.INamedElement;
import dtool.resolver.LanguageIntrinsics;
import dtool.resolver.api.IModuleResolver;

public class ExpLiteralBool extends Expression {
	
	public final boolean value;
	
	public ExpLiteralBool(boolean value) {
		this.value = value;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_BOOL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(value ? "true" : "false");
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Collections.<INamedElement>singleton(LanguageIntrinsics.d_2_063_intrinsics.bool_type);
	}
	
}