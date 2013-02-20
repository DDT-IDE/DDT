package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * An reference consisting of an element reference and an indexing paramater .
 * Can represent a static array, associative array (aka map), or tuple indexing. 
 * It can be possible to determine which one it represents by syntax analysis only (example: foo[int] or foo[4]), 
 * but sometimes semantic analysis is necessary 
 * (example foo[bar] - is bar a number or a type? is foo a type or a tuple?)
 */
public class RefIndexing extends Reference {
	
	public final Reference elemType;
	public final Resolvable indexArg;
	
	public RefIndexing(Reference keyType, Resolvable indexArg, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.elemType = parentize(keyType);
		this.indexArg = parentize(indexArg);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_INDEXING;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemType);
			TreeVisitor.acceptChildren(visitor, indexArg);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(elemType);
		cp.appendNode("[", indexArg);
		cp.append("]");
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		throw assertFail(); // TODO:
	}
	
	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		return false; // TODO:
	}
	
}