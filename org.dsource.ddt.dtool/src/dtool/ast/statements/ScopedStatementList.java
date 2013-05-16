package dtool.ast.statements;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;
import java.util.List;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNeoNode;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A scoped statement list. Used by case/default statements
 */
public class ScopedStatementList extends CommonStatementList implements IScopeNode {
	
	public ScopedStatementList(ArrayView<IStatement> statements) {
		super(assertNotNull_(statements));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SCOPED_STATEMENT_LIST;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList("\n", statements_asNodes(), "\n", "\n");
	}
	
	@Override
	public Iterator<? extends IASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
		return statements.iterator();
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
}