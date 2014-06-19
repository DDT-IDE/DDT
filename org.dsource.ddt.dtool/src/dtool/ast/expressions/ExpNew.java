package dtool.ast.expressions;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.Reference;
import dtool.engine.modules.IModuleResolver;

/**
 * New expression.
 * Note that the:
 * <code> new AllocatorArgumentsopt Type [ AssignExpression ] </code>
 * case is parsed as a {@link RefIndexing} containing Type and AssignExpression. 
 * Semantic analysis would be necessary to disambiguate.
 */
public class ExpNew extends Expression {
	
	public final Expression outerClassArg;
	public final NodeListView<Expression> allocArgs;
	public final Reference newtype;
	public final NodeListView<Expression> args;
	
	public ExpNew(Expression outerClassArg, NodeListView<Expression> atorArgs, Reference type,
		NodeListView<Expression> args) {
		this.outerClassArg = parentize(outerClassArg);
		this.allocArgs = parentize(atorArgs);
		this.newtype = parentize(type);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_NEW;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, outerClassArg);
		acceptVisitor(visitor, allocArgs);
		acceptVisitor(visitor, newtype);
		acceptVisitor(visitor, args);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("", outerClassArg, ".");
		cp.append("new");
		cp.appendNodeList("(", allocArgs, ", ", ")", " "); 
		cp.append(newtype);
		cp.appendNodeList("(", args, ", ", ")", " ");
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
		// This is not entirely correct for struct-like types, 
		// in that case a pointer to the the type is actually the type of the new exp.
		// But current behavior is acceptable for now.
		
		// Also, if the type ref is a static array, the return type is supposed to be a dynamic array,
		// but we don't implement that
		return findTargetElementsForReference(mr, newtype, findFirstOnly);
	}
	
}