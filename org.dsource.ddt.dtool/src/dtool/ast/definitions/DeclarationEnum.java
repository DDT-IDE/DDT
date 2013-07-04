package dtool.ast.definitions;

import java.util.Iterator;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.definitions.DefinitionEnum.EnumBody;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.resolver.INonScopedBlock;

public class DeclarationEnum extends ASTNode implements INonScopedBlock, IDeclaration, IStatement {
	
	public final Reference type;
	public final EnumBody body;
	
	public DeclarationEnum(Reference type, EnumBody body) {
		this.type = parentize(type);
		this.body = parentize(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ENUM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, body);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("enum ");
		cp.append(": ", type);
		cp.append(body);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return NodeListView.getIteratorSafe(body.nodeList /*BUG here NPE*/);
	}
	
}