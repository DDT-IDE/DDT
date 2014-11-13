package dtool.ast.declarations;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
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