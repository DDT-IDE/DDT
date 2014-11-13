package dtool.ast.declarations;

import static dtool.util.NewUtils.assertCast;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.Iterator;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.engine.common.INonScopedContainer;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;

public class ImportSelective extends ASTNode implements INonScopedContainer, IImportFragment {
	
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
	protected DeclarationImport getParent_Concrete() {
		return assertCast(parent, DeclarationImport.class);
	}
	
	public DeclarationImport getDeclarationImport() {
		return getParent_Concrete();
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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, fragment);
		acceptVisitor(visitor, impSelFrags);
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