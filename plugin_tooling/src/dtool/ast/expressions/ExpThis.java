package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics.ExpSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefinitionClass;

public class ExpThis extends Expression {
	
	public ExpThis() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_THIS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("this");
	}
	
	public static DefinitionClass getClassNodeParent(ASTNode node) {
		do {
			node = node.getParent();
			if(node instanceof DefinitionClass) {
				DefinitionClass definitionClass = (DefinitionClass) node;
				return definitionClass;
			}
		} while(node != null);
		return null;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics(ISemanticContext parentContext) {
		return new ExpSemantics(this, parentContext) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(boolean findOneOnly) {
			DefinitionClass definitionClass = getClassNodeParent(ExpThis.this);
			if(definitionClass == null) {
				return null;
			}
			return Collections.<INamedElement>singleton(definitionClass);
		}
		
	};
	}
	
}