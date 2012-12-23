package dtool.ast.references;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * A normal qualified reference.
 */
public class RefQualified extends CommonRefQualified {
	
	public final IDefUnitReferenceNode qualifier; //Entity or Expression
	
	public RefQualified(IDefUnitReferenceNode qualifier, RefIdentifier qualifiedName) {
		super(assertNotNull_(qualifiedName));
		this.qualifier = parentizeI(assertNotNull_(qualifier));
	}
	
	public RefQualified(IDefUnitReferenceNode rootRef, RefIdentifier subRef, SourceRange sourceRange) {
		this(rootRef, subRef);
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, qualifier);
			TreeVisitor.acceptChildren(visitor, qualifiedName);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		// TODO, proper use
		cp.append(qualifier.toStringAsElement(), ".", qualifiedName.toStringAsElement());
	}
	
	public IASTNode getRootAsNode() {
		return (IASTNode) this.qualifier;
	}
	
	@Override
	public IDefUnitReferenceNode getQualifier() {
		return qualifier;
	}
	
	@Override
	public Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver) {
		return qualifier.findTargetDefUnits(moduleResolver, false);
	}
	
}