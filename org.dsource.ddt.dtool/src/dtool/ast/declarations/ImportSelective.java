package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Iterator;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNode;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.INonScopedBlock;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

public class ImportSelective extends ASTNode implements INonScopedBlock, IImportFragment {
	
	public static interface IImportSelectiveSelection extends IASTNode {
		//String getTargetName();
	}
	
	public final IImportFragment fragment;
	public final ArrayView<ASTNode> impSelFrags;
	
	public ImportSelective(IImportFragment subFragment, ArrayView<IImportSelectiveSelection> frags) {
		this.impSelFrags = CoreUtil.<ArrayView<ASTNode>>blindCast(parentizeFrags(frags));
		this.fragment = parentizeI(subFragment);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IMPORT_SELECTIVE;
	}
	
	@Override
	protected void checkNewParent() {
		assertTrue(parent instanceof DeclarationImport);
	}
	
	public DeclarationImport getDeclarationImport() {
		return (DeclarationImport) super.getParent();
	}
	
	public ArrayView<IImportSelectiveSelection> parentizeFrags(ArrayView<IImportSelectiveSelection> frags) {
		if (frags != null) {
			for (IImportSelectiveSelection selection : frags) {
				((ASTNode) selection).setParent(this);
				if (selection instanceof ImportSelectiveAlias) {
					((ImportSelectiveAlias) selection).target.impSel = this;
				} else if (selection instanceof RefImportSelection) {
					((RefImportSelection) selection).impSel = this;
				} else {
					assertFail();
				}
			}
		}
		return frags;
	}
	
	@Override
	public RefModule getModuleRef() {
		return fragment.getModuleRef();
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, fragment);
			TreeVisitor.acceptChildren(visitor, impSelFrags);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return impSelFrags.iterator();
	}
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch search) {
		ReferenceResolver.findDefUnitInSelectiveImport(this, search);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(fragment, " : ");
		cp.appendList(impSelFrags, ", ");
	}
	
}