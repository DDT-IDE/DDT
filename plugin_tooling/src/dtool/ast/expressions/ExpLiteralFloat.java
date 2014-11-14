package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.parser.common.IToken;
import dtool.resolver.LanguageIntrinsics;

public class ExpLiteralFloat extends Expression {
	
	public final IToken floatNum;
	
	public ExpLiteralFloat(IToken floatNum) {
		this.floatNum = assertNotNull(floatNum);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_FLOAT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(floatNum);
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Collections.<INamedElement>singleton(LanguageIntrinsics.D2_063_intrinsics.float_type);
	}
	
}