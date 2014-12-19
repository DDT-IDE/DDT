package dtool.ast.expressions;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics.ExpSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefinitionClass;

public class ExpSuper extends Expression {
	
	public ExpSuper() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_SUPER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("super");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
		
			@Override
			public INamedElement doResolveTargetElement() {
				DefinitionClass definitionClass = ExpThis.getClassNodeParent(ExpSuper.this);
				if(definitionClass == null) {
					return null;
				}
				
				INamedElement superClass = definitionClass.getSemantics(context).resolveSuperClass(context);
				if(superClass == null) {
					return null;
				}
				return superClass;
			}
			
		};
	}
	
}