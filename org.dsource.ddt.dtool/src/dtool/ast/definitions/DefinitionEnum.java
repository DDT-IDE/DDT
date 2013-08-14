package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;

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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, body);
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
			this.nodeList = parentize(assertNotNull(nodes));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ENUM_BODY;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, nodeList);
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
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, body.nodeList /*BUG here NPE*/, true);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		ReferenceResolver.resolveSearchInScope(search, this);
	}
	
}