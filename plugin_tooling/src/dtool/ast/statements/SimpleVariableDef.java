package dtool.ast.statements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.Reference;
import dtool.engine.common.DefElementCommon;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;

public class SimpleVariableDef extends DefUnit {
	
	public final Reference type;
	
	public SimpleVariableDef(Reference type, ProtoDefSymbol defId) {
		super(defId);
		this.type = parentize(assertNotNull(type));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SIMPLE_VARIABLE_DEF;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defname);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defname);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInReferredContainer(search, type);
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.resolveTypeForValueContext(mr, type);
	}
	
}