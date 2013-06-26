package dtool.ast.definitions;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.api.IModuleResolver;

public class DefinitionEnum extends CommonDefinition implements IScopeNode, IDeclaration, IStatement {
	
	public final Reference type;
	public final EnumBody body;
	
	public DefinitionEnum(Token[] comments, ProtoDefSymbol defId, Reference type, EnumBody body) {
		super(comments, defId);
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
		cp.append(defname, " ");
		cp.append(": ", type);
		cp.append(body);
	}
	
	public static class EnumBody extends ASTNode {
		
		public final NodeListView<EnumMember> nodeList;
		
		public EnumBody(NodeListView<EnumMember> nodes) {
			this.nodeList = parentize(assertNotNull_(nodes));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ENUM_BODY;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, nodeList);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.appendNodeList("{", nodeList, ", ", "}");
		}
	}
	
	public static class NoEnumBody extends EnumBody {
		
		public static NodeListView<EnumMember> NULL_DECLS = new NodeListView<>(new EnumMember[0], false);
		
		public NoEnumBody() {
			super(NULL_DECLS);
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
		return body.nodeList.iterator(); /*BUG here NPE*/
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
}