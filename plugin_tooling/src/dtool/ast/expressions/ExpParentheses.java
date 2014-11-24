package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics.ExpSemantics;
import melnorme.lang.tooling.symbols.INamedElement;

public class ExpParentheses extends Expression {
	
	public final boolean isDotAfterParensSyntax;
	public final Resolvable resolvable;
	
	public ExpParentheses(boolean isDotAfterParensSyntax, Resolvable resolvable) {
		this.isDotAfterParensSyntax = isDotAfterParensSyntax;
		this.resolvable = parentize(assertNotNull(resolvable));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_PARENTHESES;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, resolvable);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("(", resolvable, ")");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new ExpSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			return resolvable.getSemantics().findTargetDefElements(mr, findOneOnly);
		}
		
	};
	
}