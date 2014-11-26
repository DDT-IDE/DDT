package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.IModuleElement;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.RefModule;
import dtool.ast.statements.IStatement;

/**
 * An import Declaration.
 * This is considered an INonScopedBlock because it might contain aliasing
 * imports and selective imports, which are primary-space {@link DefUnit}s.
 */
public class DeclarationImport extends ASTNode implements INonScopedContainer, IDeclaration, IStatement {
	
	public final ArrayView<IImportFragment> imports;
	public final boolean isStatic;
	public boolean isTransitive; // aka public imports
	
	public DeclarationImport(boolean isStatic, ArrayView<IImportFragment> imports) {
		this.imports = parentizeI(imports);
		this.isStatic = isStatic;
		this.isTransitive = false; // TODO, should be determined by surrounding analysis
	}
	
	public final ArrayView<ASTNode> imports_asNodes() {
		return CoreUtil.<ArrayView<ASTNode>>blindCast(imports);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_IMPORT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, imports);
	}
	
	public static interface IImportFragment extends IASTNode {
		
		/** Performs a search in the secondary/background scope.
		 * Only imports contribute to this secondary namespace. */
		public void searchInSecondaryScope(CommonScopeLookup options);

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
	
	/* -----------------  ----------------- */
	
	@Override
	public void evaluateForScopeLookup(CommonScopeLookup lookup, boolean importsOnly, boolean isSequentialLookup) {
		super.evaluateForScopeLookup(lookup, importsOnly, isSequentialLookup);
		
		if(!importsOnly) {
			return;
		}
		
		if(!isTransitive && !searchOriginInSameModule(lookup, this))
			return; // Don't consider private imports
		
		for (IImportFragment impFrag : imports) {
			impFrag.searchInSecondaryScope(lookup);
			// continue regardless of search.findOnlyOne because of partial packages
		}
		
	}
	
	protected static boolean searchOriginInSameModule(CommonScopeLookup search, DeclarationImport declImport) {
		IModuleElement searchOriginModule = search.getSearchOriginModule();
		if(searchOriginModule == null) 
			return false;
		// only visible if search lexical origin in same module as the private import.
		return searchOriginModule == declImport.getModuleNode_();
	}
	
}