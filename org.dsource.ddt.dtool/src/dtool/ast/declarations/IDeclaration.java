package dtool.ast.declarations;

import descent.internal.compiler.parser.PROT;
import dtool.ast.statements.IStatement;

public interface IDeclaration extends IStatement {
	
	void setAttributes(int effectiveModifiers);
	int getAttributes();
	
	void setProtection(PROT prot);
	PROT getEffectiveProtection();
}
