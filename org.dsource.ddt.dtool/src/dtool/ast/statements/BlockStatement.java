package dtool.ast.statements;

import dtool.ast.ASTNodeTypes;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends CommonStatementList implements IScopeNode, IFunctionBody {
	
	public BlockStatement(ArrayView<IStatement> statements) {
		super(statements);
	}
	
	public BlockStatement() {
		super();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.BLOCK_STATEMENT;
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, statements_asNodes(), true);
	}
	
}