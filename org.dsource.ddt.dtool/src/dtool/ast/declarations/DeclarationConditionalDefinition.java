package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.VersionSymbol;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;
import dtool.descentadapter.DefinitionConverter;

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
	
	public DeclarationConditionalDefinition(DebugSymbol elem) {
		setSourceRange(elem);
		if(elem.ident != null)
			this.identifier = DefinitionConverter.convertId(elem.ident);
		else 
			this.identifier = new Symbol(new String(elem.version.value));
		conditionalKind = Type.DEBUG;
	}
	
	public DeclarationConditionalDefinition(VersionSymbol elem) {
		setSourceRange(elem);
		if(elem.ident != null)
			this.identifier = DefinitionConverter.convertId(elem.ident);
		else 
			this.identifier = new Symbol(new String(elem.version.value));
		conditionalKind = Type.VERSION;
	}
	
	public DeclarationConditionalDefinition(Symbol id, int t) {
		this.identifier = id;
		if (this.identifier != null)
			this.identifier.setParent(this);
		
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

	@Override
	public void setAttributes(int effectiveModifiers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAttributes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setProtection(PROT prot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PROT getEffectiveProtection() {
		// TODO Auto-generated method stub
		return null;
	}
}
