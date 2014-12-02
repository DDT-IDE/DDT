package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.Reference;

/**
 * An Expression wrapping a {@link Reference}
 */
public class ExpReference extends Expression {
	
	public final Reference ref;
	
	public ExpReference(Reference ref) {
		assertNotNull(ref);
		this.ref = parentize(ref);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_REFERENCE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, ref);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(ref);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics(ISemanticContext parentContext) {
		return new ResolvableSemantics(this, parentContext) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(boolean findOneOnly) {
			return ref.getSemantics(context).findTargetDefElements(findOneOnly);
		}
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue() {
			return ref.getSemantics(context).resolveTypeOfUnderlyingValue();
		}
		
	};
	}
	
}