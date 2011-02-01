package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NeoSourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;

/**
 * A normal qualified reference.
 */
public class RefQualified extends CommonRefQualified {
	
	
	public final IDefUnitReferenceNode qualifier; //Entity or Expression
	
	public RefQualified(IDefUnitReferenceNode qualifier, RefIdentifier qualifiedName) {
		assertNotNull(qualifier);
		assertNotNull(qualifiedName);
		this.qualifier = qualifier;
		this.qualifiedName = qualifiedName;
	}
	
	public RefQualified(IDefUnitReferenceNode rootRef, RefIdentifier subRef, NeoSourceRange sourceRange) {
		this(rootRef, subRef);
		maybeSetSourceRange(sourceRange);
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
	public String toStringAsElement() {
		return qualifier.toStringAsElement() + "." + qualifiedName.toStringAsElement();
	}
	
	public IASTNode getRootAsNode() {
		return (IASTNode) this.qualifier;
	}
	
	@Override
	public IDefUnitReferenceNode getQualifier() {
		return qualifier;
	}
	
	@Override
	public Collection<DefUnit> findRootDefUnits() {
		return qualifier.findTargetDefUnits(false);
	}
	
}