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
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends Statement implements IScopeNode, IFunctionBody {
	
	public final ArrayView<IStatement> statements;
	public final boolean hasCurlyBraces;
	
	public BlockStatement(ArrayView<IStatement> statements, boolean hasCurlyBraces) {
		this.statements = parentizeI(assertNotNull_(statements));
		this.hasCurlyBraces = hasCurlyBraces;
	}
	
	/** This represents a missing block */
	public BlockStatement() {
		this.statements = null;
		this.hasCurlyBraces = false;
	}
	
	public final ArrayView<ASTNode> statements_asNodes() {
		return CoreUtil.<ArrayView<ASTNode>>blindCast(statements);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.BLOCK_STATEMENT;
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
		cp.append(hasCurlyBraces, "{");
		cp.appendList("\n", statements_asNodes(), "\n", "\n", " ");
		cp.append(hasCurlyBraces, "}");
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