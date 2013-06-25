package dtool.ast.references;

import static dtool.util.NewUtils.assertInstance;
import static dtool.util.NewUtils.assertNotNull_;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * A normal qualified reference.
 */
public class RefQualified extends CommonRefQualified {
	
	public final Resolvable qualifier;
	public final boolean isExpressionQualifier;
	public final int dotOffset;
	
	public RefQualified(IQualifierNode qualifier, int dotOffset, RefIdentifier qualifiedId) {
		super(assertNotNull_(qualifiedId));
		this.qualifier = parentizeI(assertInstance(qualifier, Resolvable.class));
		this.dotOffset = dotOffset;
		this.isExpressionQualifier = isExpressionQualifier(qualifier);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_QUALIFIED;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, qualifier);
			TreeVisitor.acceptChildren(visitor, qualifiedId);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(qualifier, qualifier instanceof ExpLiteralInteger ? " ." : ".");
		cp.append(qualifiedId);
	}
	
	@Override
	public Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver) {
		return qualifier.findTargetDefUnits(moduleResolver, false);
	}
	
	public static boolean isExpressionQualifier(IQualifierNode qualifier) {
		return qualifier instanceof Expression || 
			((qualifier instanceof RefQualified) && ((RefQualified) qualifier).isExpressionQualifier);
	}
	
}