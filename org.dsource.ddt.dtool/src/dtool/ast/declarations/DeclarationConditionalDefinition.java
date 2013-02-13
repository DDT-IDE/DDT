package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Symbol;

/**
 * Node of type:
 *	version = someident;
 *  debug = someident;   
 */
public class DeclarationConditionalDefinition extends ASTNeoNode {
	
	public interface Type {
		int DEBUG = 9;
		int VERSION = 10;
	}
	
	public final Symbol identifier;
	public final int conditionalKind;
	
	public DeclarationConditionalDefinition(Symbol id, int conditionalKind, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.identifier = parentize(id);
		this.conditionalKind = conditionalKind;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, identifier);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "["+ (conditionalKind == Type.VERSION?"debug":"version") + "="+identifier.toStringAsCode()+")]";
	}
	
}
