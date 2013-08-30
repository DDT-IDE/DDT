package dtool.ast.references;

import java.util.Collection;
import java.util.Collections;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.util.NodeUtil;
import dtool.resolver.api.IModuleResolver;

/** An entity reference starting at module scope. 
 * Example: "a = .foo;"
 */
public class RefModuleQualified extends CommonRefQualified {
	
	public RefModuleQualified(RefIdentifier qualifiedId) {
		super(qualifiedId);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_MODULE_QUALIFIED;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, qualifiedId);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(".");
		cp.appendNodeOrNullAlt(qualifiedId, "/*MISSING*/");
	}
	
	@Override
	public int getDotOffset() {
		return getStartPos();
	}
	
	@Override
	public Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver) {
		final Module module = NodeUtil.getParentModule(this);
		return Collections.singletonList((DefUnit) module);
	}
	
}