package dtool.ast.definitions;

import static dtool.util.NewUtils.assertCast;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.engine.common.IVarDefinitionLike;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.operations.CommonDefVarSemantics;
import dtool.resolver.CommonDefUnitSearch;

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
	
	@Override
	public Reference getDeclaredType() {
		return getParent_Concrete().type;
	}
	
	@Override
	public IInitializer getDeclaredInitializer() {
		return initializer;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		getNodeSemantics().resolveSearchInMembersScope(search);
	}
	
	@Override
	public ILangNamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return getNodeSemantics().resolveEffectiveType(mr);
	}
	
	protected final CommonDefVarSemantics nodeSemantics = new CommonDefVarSemantics(this) { };
	
	@Override
	public CommonDefVarSemantics getNodeSemantics() {
		return nodeSemantics;
	}
	
}