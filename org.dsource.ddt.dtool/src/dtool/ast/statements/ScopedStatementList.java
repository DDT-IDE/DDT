package dtool.ast.statements;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A scoped statement list. Used by case/default statements
 */
public class ScopedStatementList extends Statement implements IScopeNode, IFunctionBody {
	
	public final ArrayView<IStatement> statements;
	
	public ScopedStatementList(ArrayView<IStatement> statements) {
		this.statements = parentizeI(assertNotNull_(statements));
	}
	
	public final ArrayView<ASTNode> statements_asNodes() {
		return CoreUtil.<ArrayView<ASTNode>>blindCast(statements);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SCOPED_STATEMENT_LIST;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
			TreeVisitor.acceptChildren(visitor, statements);
		}
		visitor.endVisit(this);
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