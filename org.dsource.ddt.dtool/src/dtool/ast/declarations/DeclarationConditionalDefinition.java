package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Symbol;

/**
 * Node of type:
 *	version = someident;
 *  debug = someident;   
 */
public class DeclarationConditionalDefinition extends ASTNeoNode implements IDeclaration {

	public interface Type {
		int DEBUG = 9;
		int VERSION = 10;
	}

	public Symbol identifier;
	public int conditionalKind;
	
	public DeclarationConditionalDefinition(Symbol id, int t, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.identifier = id; parentize(this.identifier);
		this.conditionalKind = t;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, identifier);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "["+ (conditionalKind == Type.VERSION?"debug":"version") 
			+ "="+identifier.toStringAsElement()+")]";
	}
}
