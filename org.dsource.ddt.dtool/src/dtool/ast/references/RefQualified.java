package dtool.ast.references;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * A normal qualified reference.
 */
public class RefQualified extends CommonRefQualified {
	
	public final IQualifierNode qualifier;
	
	public RefQualified(IQualifierNode qualifier, RefIdentifier qualifiedIdRef, SourceRange sourceRange) {
		super(assertNotNull_(qualifiedIdRef));
		this.qualifier = parentizeI(assertNotNull_(qualifier));
		initSourceRange(sourceRange);
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
			TreeVisitor.acceptChildren(visitor, qualifiedName);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode((ASTNeoNode) qualifier);
		cp.appendNode(" . ", qualifiedName);
	}
	
	public IASTNode getRootAsNode() {
		return (IASTNode) this.qualifier;
	}
	
	@Override
	public IQualifierNode getQualifier() {
		return qualifier;
	}
	
	@Override
	public Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver) {
		return qualifier.findTargetDefUnits(moduleResolver, false);
	}
	
	public static IQualifierNode getRootNode(IQualifierNode ref) {
		if(ref instanceof RefQualified) {
			return getRootNode(((RefQualified) ref).qualifier);
		}
		return ref;
	}
	
}