package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.INamedElement;
import dtool.resolver.api.IModuleResolver;

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
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		DefinitionClass definitionClass = getClassNodeParent(this);
		if(definitionClass == null) {
			return null;
		}
		return Collections.<INamedElement>singleton(definitionClass);
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
	
}