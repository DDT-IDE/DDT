package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
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
public class DeclarationImport extends ASTNeoNode implements INonScopedBlock, IDeclaration {
	
	public final ArrayView<ImportFragment> imports;
	public final boolean isStatic;
	public boolean isTransitive; // aka public imports
	
	public DeclarationImport(ArrayView<ImportFragment> imports, boolean isStatic, boolean isTransitive,
			SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.imports = imports; parentize(this.imports);
		this.isStatic = isStatic;
		this.isTransitive = isTransitive;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}
	
	
	public static abstract class ImportFragment extends ASTNeoNode {
		public RefModule moduleRef;
		
		public ImportFragment(RefModule moduleRef, SourceRange sourceRange) {
			initSourceRange(sourceRange);
			this.moduleRef = moduleRef; parentize(this.moduleRef);
		}
		
		/** Performs a search in the secondary/background scope.
		 * Only imports contribute to this secondary namespace. */
		public abstract void searchInSecondaryScope(CommonDefUnitSearch options);
		
		@Override
		public String toStringAsElement() {
			return moduleRef.toStringAsElement();
		}
		
	}
	
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return imports.iterator();
	}
	
	@Override
	public String toStringAsElement() {
		String str = "";
		for (int i = 0; i < imports.size(); i++) {
			ImportFragment fragment = imports.get(i);
			if(i > 0)
				str = str + ", ";
			str = str + fragment.toStringAsElement();
		}
		return "[import "+str+"]";
	}
	
}
