package dtool.ast.references;

import java.util.Collection;
import java.util.Collections;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.refmodel.pluginadapters.IModuleResolver;

/** An entity reference starting at module scope. 
 * Example: "a = .foo;"
 */
public class RefModuleQualified extends CommonRefQualified {
	
	public RefModuleQualified(RefIdentifier subref) {
		super(subref);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_MODULE_QUALIFIED;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, qualifiedName);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(".");
		cp.appendNodeOrNullAlt(qualifiedName, "/*MISSING*/");
	}
	
	@Override
	public Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver) {
		final Module module = NodeUtil.getParentModule(this);
		return Collections.singletonList((DefUnit)module);
	}
	
}