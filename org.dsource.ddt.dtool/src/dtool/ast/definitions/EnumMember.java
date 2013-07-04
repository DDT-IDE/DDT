package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;

public class EnumMember extends DefUnit {
	
	public final Reference type;
	public final Expression value;
	
	public EnumMember(Reference type, ProtoDefSymbol defId, Expression value) {
		super(defId);
		this.type = parentize(type);
		this.value = parentize(value);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ENUM_MEMBER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, value);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defname);
		cp.append(" = ", value);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.EnumMember;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return getType().getTargetScope(moduleResolver);
	}
	
	private Reference getType() {
		return ((DefinitionEnum) getParent()).type;
	}
	
}