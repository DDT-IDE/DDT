package dtool.ast.statements;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
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
	
	public BlockStatement(ArrayView<IStatement> statements, boolean hasCurlyBraces, SourceRange sourceRange) {
		this.statements = parentizeI(assertNotNull_(statements));
		this.hasCurlyBraces = hasCurlyBraces;
		initSourceRange(sourceRange);
	}
	
	/** This represents a missing block */
	public BlockStatement(SourceRange sourceRange) {
		this.statements = null;
		this.hasCurlyBraces = false;
		initSourceRange(sourceRange);
	}
	
	public final ArrayView<ASTNeoNode> statements_asNodes() {
		return CoreUtil.<ArrayView<ASTNeoNode>>blindCast(statements);
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
		cp.appendNodeList("\n", statements_asNodes(), "\n", "\n", " ");
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