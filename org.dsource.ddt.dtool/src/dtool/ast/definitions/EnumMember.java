package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);	 			
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(type, " ");
		cp.appendNode(defname);
		cp.appendNode(" = ", value);
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