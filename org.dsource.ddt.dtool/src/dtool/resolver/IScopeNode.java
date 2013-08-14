package dtool.resolver;

import dtool.ast.IASTNode;

public interface IScopeNode extends IScopeProvider, IASTNode {
	
	IScopeNode getOuterLexicalScope();
	
}