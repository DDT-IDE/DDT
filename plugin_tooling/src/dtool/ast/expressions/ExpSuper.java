package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.IModuleResolver;
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
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		DefinitionClass definitionClass = ExpThis.getClassNodeParent(this);
		if(definitionClass == null) {
			return null;
		}
		
		INamedElement superClass = definitionClass.getNodeSemantics().resolveSuperClass(moduleResolver);
		if(superClass == null) {
			return null;
		}
		return Collections.<INamedElement>singleton(superClass);
	}
	
}