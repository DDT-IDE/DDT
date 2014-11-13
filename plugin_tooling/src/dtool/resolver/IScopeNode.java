package dtool.resolver;

import melnorme.lang.tooling.ast.IASTNode;

public interface IScopeNode extends IScopeProvider, IASTNode {
	
	IScopeNode getOuterLexicalScope();
	
}