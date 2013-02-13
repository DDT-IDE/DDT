package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
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
public class DeclarationImport extends ASTNeoNode implements INonScopedBlock {
	
	public final ArrayView<IImportFragment> imports;
	public final ArrayView<ASTNeoNode> imports_asNodes;
	public final boolean isStatic;
	public boolean isTransitive; // aka public imports
	
	public DeclarationImport(ArrayView<IImportFragment> imports, boolean isStatic, boolean isTransitive,
			SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.imports = parentizeI(imports);
		this.imports_asNodes = CoreUtil.<ArrayView<ASTNeoNode>>blindCast(imports);
		this.isStatic = isStatic;
		this.isTransitive = isTransitive;
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
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return imports_asNodes.iterator();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		// TODO public specifier
		if(isStatic) {
			cp.append("static ");
		}
		
		cp.append("import ");
		cp.appendNodeList(imports_asNodes, ", ");
		cp.append(";");
	}
	
}