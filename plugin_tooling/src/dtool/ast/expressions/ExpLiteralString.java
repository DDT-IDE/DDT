package dtool.ast.expressions;

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.common.IToken;
import dtool.resolver.LanguageIntrinsics;

public class ExpLiteralString extends Expression {
	
	public final IToken[] stringTokens;
	
	public ExpLiteralString(IToken stringToken) {
		this(new IToken[] { stringToken });
	}
	
	public ExpLiteralString(IToken[] stringToken) {
		this.stringTokens = stringToken;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_STRING;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		for (IToken stringToken : stringTokens) {
			cp.appendToken(stringToken);
		}
	}
	
	@Override
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
		return LanguageIntrinsics.D2_063_intrinsics.string_type.findTargetDefElements(mr, findFirstOnly);
	}
	
}