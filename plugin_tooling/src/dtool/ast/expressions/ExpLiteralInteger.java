package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.common.IToken;
import dtool.resolver.LanguageIntrinsics;

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
	public Collection<ILangNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Collections.<ILangNamedElement>singleton(LanguageIntrinsics.D2_063_intrinsics.int_type);
	}
	
}