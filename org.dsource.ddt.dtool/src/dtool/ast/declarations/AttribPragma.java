package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Expression;

public class AttribPragma extends Attribute {
	
	public final Symbol pragmaId;
	public final NodeListView<Expression> expList;
	
	public AttribPragma(Symbol id, NodeListView<Expression> expList) {
		this.pragmaId = parentize(id);
		this.expList = parentize(expList);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_PRAGMA;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if(children) {
			TreeVisitor.acceptChildren(visitor, pragmaId);
			TreeVisitor.acceptChildren(visitor, expList);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("pragma(");
		if(pragmaId != null) {
			cp.append(pragmaId);
			cp.appendNodeList(", ", expList, ", ", "");
		}
		cp.append(")");
	}
}