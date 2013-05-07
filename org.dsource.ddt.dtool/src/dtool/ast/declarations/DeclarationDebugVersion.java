package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class DeclarationDebugVersion extends AbstractConditionalDeclaration {
	
	public final boolean isDebug;
	public final VersionSymbol value;
	
	public DeclarationDebugVersion(boolean isDebug, VersionSymbol value, AttribBodySyntax bodySyntax, 
		ASTNode thenBody, ASTNode elseBody) {
		super(bodySyntax, thenBody, elseBody);
		this.isDebug = isDebug;
		this.value = parentize(value);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_DEBUG_VERSION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, value);
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, elseBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isDebug ? "debug " : "version ");
		cp.appendNode("(", value, ")");
		toStringAsCodeBodyAndElseBody(cp);
	}
	
}