package dtool.ast.statements;

import java.util.Iterator;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.resolver.INonScopedContainer;
import dtool.util.ArrayView;

public class BlockStatementUnscoped extends CommonStatementList implements INonScopedContainer {
	
	public BlockStatementUnscoped(ArrayView<IStatement> nodes) {
		super(nodes);
	}
	
	public BlockStatementUnscoped() {
		super();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.BLOCK_STATEMENT_UNSCOPED;
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return super.getMembersIterator();
	}
	
}