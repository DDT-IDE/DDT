package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.definitions.DefinitionClass;
import dtool.engine.common.IDeeNamedElement;
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
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		DefinitionClass definitionClass = ExpThis.getClassNodeParent(this);
		if(definitionClass == null) {
			return null;
		}
		
		IDeeNamedElement superClass = definitionClass.resolveSuperClass(moduleResolver);
		if(superClass == null) {
			return null;
		}
		return Collections.<IDeeNamedElement>singleton(superClass);
	}
	
}