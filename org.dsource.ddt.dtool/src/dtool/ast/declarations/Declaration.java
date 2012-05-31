package dtool.ast.declarations;

import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;

public abstract class Declaration extends ASTNeoNode implements IDeclaration {
	public PROT protection;
	public int effectiveModifiers;
	
	@Override
	public void setAttributes(int effectiveModifiers) {
		this.effectiveModifiers = effectiveModifiers; 
	}
	
	@Override
	public int getAttributes() {
		return this.effectiveModifiers;
	}
	
	@Override
	public void setProtection(PROT prot) {
		this.protection = prot; 
	}
	
	@Override
	public PROT getEffectiveProtection() {
		return this.protection;
	}
}
