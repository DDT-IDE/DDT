package dtool.resolver;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.engine.IScopeProvider;

public interface IScopeNode extends IScopeProvider, IASTNode {
	
	IScopeNode getOuterLexicalScope();
	
}