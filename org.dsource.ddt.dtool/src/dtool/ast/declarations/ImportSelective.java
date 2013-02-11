package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.Iterator;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.INonScopedBlock;
import dtool.refmodel.ReferenceResolver;
import dtool.util.ArrayView;

public class ImportSelective extends ASTNeoNode implements INonScopedBlock, IImportFragment {
	
	public static interface IImportSelectiveSelection extends IASTNeoNode {
		//String getTargetName();
	}
	
	public final IImportFragment fragment;
	public final ArrayView<ASTNeoNode> impSelFrags;
	
	public ImportSelective(IImportFragment subFragment, ArrayView<IImportSelectiveSelection> frags, 
		SourceRange sourceRange) {
		this.impSelFrags = CoreUtil.<ArrayView<ASTNeoNode>>blindCast(parentizeFrags(frags));
		this.fragment = parentizeI(subFragment);
		initSourceRange(sourceRange);
	}
	
	public ArrayView<IImportSelectiveSelection> parentizeFrags(ArrayView<IImportSelectiveSelection> frags) {
		if (frags != null) {
			for (IImportSelectiveSelection n : frags) {
				((ASTNeoNode) n).setParent(this);
				if (n instanceof ImportSelectiveAlias) {
					((ImportSelectiveAlias) n).target.impSel = this;
				} else if (n instanceof RefImportSelection) {
					((RefImportSelection) n).impSel = this;
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
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, fragment);
			TreeVisitor.acceptChildren(visitor, impSelFrags);
		}
		visitor.endVisit(this);
		
	}
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return impSelFrags.iterator();
	}
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch search) {
		ReferenceResolver.findDefUnitInSelectiveImport(this, search);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode((ASTNeoNode) fragment, " : ");
		cp.appendNodeList(impSelFrags, ", ");
	}
	
}