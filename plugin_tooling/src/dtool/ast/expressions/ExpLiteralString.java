package dtool.ast.expressions;

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics.ExpSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.parser.common.IToken;
import dtool.resolver.DeeLanguageIntrinsics;

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
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new ExpSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			return DeeLanguageIntrinsics.D2_063_intrinsics.string_type.findTargetDefElements(mr, findOneOnly);
		}
		
	};
	
}