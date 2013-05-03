package dtool.ast.definitions;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

public class DefinitionEnum extends Definition implements IScopeNode, IStatement {
	
	public final Reference type;
	public final EnumBody body;
	
	public DefinitionEnum(ProtoDefSymbol defId, Reference type, EnumBody body) {
		super(assertNotNull_(defId));
		this.type = parentize(type);
		this.body = parentize(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_ENUM;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
		
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("enum ");
		cp.appendNode(defname, " ");
		cp.appendNode(": ", type);
		cp.appendNode(body);
	}
	
	public static class EnumBody extends NodeList<EnumMember> {
		
		public final boolean hasEndingComma;

		public EnumBody(ArrayView<EnumMember> nodes, boolean endingComma) {
			super(nodes);
			this.hasEndingComma = endingComma;
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ENUM_BODY;
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append("{");
			cp.appendNodeList(nodes, ", ", hasEndingComma);
			cp.append("}");
		}
	}
	
	public static class NoEnumBody extends EnumBody {
		
		public static ArrayView<EnumMember> NULL_DECLS = ArrayView.create(new EnumMember[0]);
		
		public NoEnumBody() {
			super(NULL_DECLS, false);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ENUM_BODY;
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(";");
		}
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Enum;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
	@Override
	public Iterator<EnumMember> getMembersIterator(IModuleResolver moduleResolver) {
		return body.nodes.iterator(); /*BUG here NPE*/
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
}