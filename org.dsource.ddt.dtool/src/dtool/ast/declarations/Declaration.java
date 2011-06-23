package dtool.ast.declarations;


import java.util.Iterator;
import java.util.List;

import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.TOK;
import dtool.ast.ASTNeoNode;
import dtool.refmodel.IScope;

public abstract class Declaration {
	
	public static int hasModifier(List<Modifier> modifiers, TOK tok) {
		int i = 0;
		if(modifiers == null)
			return -1;
		for (Iterator<Modifier> iter = modifiers.iterator(); iter.hasNext(); i++) {
			Modifier modifier = iter.next();
			if(modifier.tok == tok)
				return i;
		}
		return -1;
	}

	public static boolean hasInheritedProtection(ASTNeoNode elem, TOK tok) {
		while(elem != null && !(elem instanceof IScope)) {
			elem = elem.getParent();
			if(elem instanceof DeclarationProtection) {
				DeclarationProtection pdecl = (DeclarationProtection) elem;
				if(pdecl.modifier.tok == tok)
					return true;
				else
					return false;
			}
		} 
		return false;
	}
}
