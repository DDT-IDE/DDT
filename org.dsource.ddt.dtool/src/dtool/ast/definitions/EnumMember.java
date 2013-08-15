package dtool.ast.definitions;

import static dtool.util.NewUtils.assertCast;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefinitionEnum.EnumBody;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.resolver.CommonDefUnitSearch;

public class EnumMember extends DefUnit {
	
	public final Reference type;
	public final Expression value;
	
	public EnumMember(Reference type, ProtoDefSymbol defId, Expression value) {
		super(defId);
		this.type = parentize(type);
		this.value = parentize(value);
	}
	
	@Override
	protected EnumBody getParent_Concrete() {
		return assertCast(parent, EnumBody.class);
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
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		Reference effectiveType = type != null ? type : getEnumParentType();
		Reference.resolveSearchInReferedMembersScope(search, effectiveType);
	}
	
	public Reference getEnumParentType() {
		EnumBody enumBody = getParent_Concrete();
		ASTNode parentEnum = enumBody.getParent();
		if(parentEnum instanceof DeclarationEnum) {
			DeclarationEnum declarationEnum = (DeclarationEnum) parentEnum;
			return declarationEnum.type; 
		}
		if(parentEnum instanceof DefinitionEnum) {
			DefinitionEnum definitionEnum = (DefinitionEnum) parentEnum;
			return definitionEnum.type; 
		} 
		return null;
	}
	
}