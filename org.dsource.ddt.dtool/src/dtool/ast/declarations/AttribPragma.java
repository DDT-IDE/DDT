package dtool.ast.declarations;

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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, pragmaId);
		acceptVisitor(visitor, expList);
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