package dtool.ast.declarations;

import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;

public abstract class Declaration extends ASTNeoNode implements IDeclaration {
	public PROT protection;
	public int effectiveModifiers;
	
}
