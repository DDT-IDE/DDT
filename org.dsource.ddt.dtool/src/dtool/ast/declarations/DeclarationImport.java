package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.INonScopedBlock;
import dtool.util.ArrayView;

/**
 * An import Declaration.
 * This is considered an INonScopedBlock because it might contain aliasing
 * imports and selective imports, which are primary-space {@link DefUnit}s.
 */
public class DeclarationImport extends ASTNode implements INonScopedBlock {
	
	public final ArrayView<IImportFragment> imports;
	public final boolean isStatic;
	public boolean isTransitive; // aka public imports
	
	public DeclarationImport(boolean isStatic, ArrayView<IImportFragment> imports) {
		this.imports = parentizeI(imports);
		this.isStatic = isStatic;
		this.isTransitive = false; // TODO, should be determined by surronding analysis
	}
	
	public final ArrayView<ASTNode> imports_asNodes() {
		return CoreUtil.<ArrayView<ASTNode>>blindCast(imports);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_IMPORT;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}
	
	public static interface IImportFragment extends IASTNeoNode {
		
		/** Performs a search in the secondary/background scope.
		 * Only imports contribute to this secondary namespace. */
		public void searchInSecondaryScope(CommonDefUnitSearch options);

		public RefModule getModuleRef();
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return imports_asNodes().iterator();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isStatic, "static ");
		
		cp.append("import ");
		cp.appendList(imports_asNodes(), ", ");
		cp.append(";");
	}
	
}