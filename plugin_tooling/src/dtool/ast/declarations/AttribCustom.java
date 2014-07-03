package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.expressions.Expression;
import dtool.ast.references.RefIdentifier;

/**
 * Node for User Defined Attributes ( http://dlang.org/attribute.html#uda )
 * Note: the spec/compiler, as of 2.063 only allows an RefIdentifier as base reference 
 * (and it's a reference and not a symbol def, it should refer to another element.
 * 
 */
public class AttribCustom extends Attribute {
	
	public final RefIdentifier ref;
	public final NodeListView<Expression> args; // if null, no argument list
	
	public AttribCustom(RefIdentifier ref, NodeListView<Expression> args) {
		this.ref = parentize(ref);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_CUSTOM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, ref);
		acceptVisitor(visitor, args);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("@");
		cp.append(ref);
		cp.appendNodeList("(", args, ",", ")");
	}
	
}