package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.parser.common.IToken;
import dtool.resolver.DeeLanguageIntrinsics;

public class ExpLiteralInteger extends Expression {
	
	public final IToken num;
	
	public ExpLiteralInteger(IToken num) {
		this.num = assertNotNull(num);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_INTEGER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(num);
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(ISemanticContext moduleResolver, boolean findFirstOnly) {
		return Collections.<INamedElement>singleton(DeeLanguageIntrinsics.D2_063_intrinsics.int_type);
	}
	
}