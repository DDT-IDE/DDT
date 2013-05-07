package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList;
import dtool.ast.definitions.DefinitionEnum.EnumBody;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public class DeclarationEnum extends ASTNode implements IStatement, INonScopedBlock {
	
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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
		
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("enum ");
		cp.appendNode(": ", type);
		cp.appendNode(body);
	}

	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return NodeList.getMembersIterator(body);
	}
	
	
}