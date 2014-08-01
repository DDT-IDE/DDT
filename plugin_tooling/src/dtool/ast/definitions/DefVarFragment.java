package dtool.ast.definitions;

import static dtool.util.NewUtils.assertCast;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IResolvable;

/**
 * A fragment of a variable definition in a multi-identifier variable declaration
 */
public class DefVarFragment extends DefUnit implements IVarDefinitionLike {
	
	public final IInitializer initializer;
	
	public DefVarFragment(ProtoDefSymbol defId, IInitializer initializer) {
		super(defId);
		this.initializer = parentize(initializer);
	}
	
	@Override
	public DefinitionVariable getParent_Concrete() {
		return assertCast(parent, DefinitionVariable.class);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_VAR_FRAGMENT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, initializer);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defname);
		cp.append("= ", initializer);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	public IInitializer getInitializer() {
		return initializer;
	}
	
	public Reference getDeclaredTypeReference() {
		return getParent_Concrete().type;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		DefinitionVariable.resolveSearchInReferredContainer(search, getEffectiveType());
	}
	
	@Override
	public IResolvable getEffectiveType() {
		return DefinitionVariable.getEffectiveType(getParent_Concrete().type, initializer);
	}
	
}