package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.LanguageIntrinsics;

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
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Collections.<IDeeNamedElement>singleton(LanguageIntrinsics.D2_063_intrinsics.bool_type);
	}
	
}