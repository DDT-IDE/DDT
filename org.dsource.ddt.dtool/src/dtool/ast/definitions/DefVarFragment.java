package dtool.ast.definitions;

import static dtool.util.NewUtils.assertCast;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.resolver.CommonDefUnitSearch;

/**
 * A fragment of a variable definition in a multi-identifier variable declaration
 */
public class DefVarFragment extends DefUnit {
	
	public final IInitializer init;
	
	public DefVarFragment(ProtoDefSymbol defId, IInitializer init) {
		super(defId);
		this.init = parentize(init);
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
		acceptVisitor(visitor, init);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defname);
		cp.append("= ", init);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	public IInitializer getInitializer() {
		return init;
	}
	
	public Reference getDeclaredTypeReference() {
		return getParent_Concrete().type;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInReferredContainer(search, getParent_Concrete().type);
	}
	
}