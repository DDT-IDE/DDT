package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.INamedElement;
import dtool.engine.modules.IModuleResolver;

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
		
		INamedElement superClass = definitionClass.resolveSuperClass(moduleResolver);
		if(superClass == null) {
			return null;
		}
		return Collections.<INamedElement>singleton(superClass);
	}
	
}