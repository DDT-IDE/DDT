package dtool.ast.declarations;

import static dtool.util.NewUtils.assertCast;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;

public class ImportSelective extends ASTNode implements INonScopedContainer, IImportFragment {
	
	public static interface IImportSelectiveSelection extends IASTNode {
		//String getTargetName();
	}
	
	public final IImportFragment fragment;
	public final ArrayView<ASTNode> impSelFrags;
	
	public ImportSelective(IImportFragment subFragment, ArrayView<IImportSelectiveSelection> frags) {
		this.impSelFrags = CoreUtil.<ArrayView<ASTNode>>blindCast(parentizeFrags(frags));
		this.fragment = parentize(subFragment);
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
	public Iterable<? extends IASTNode> getMembersIterable() {
		return impSelFrags;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(fragment, " : ");
		cp.appendList(impSelFrags, ", ");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void evaluateShadowScopeContribution(ScopeNameResolution scopeRes) {
		findDefUnitInSelectiveImport(this, scopeRes);
	}
	
	public static void findDefUnitInSelectiveImport(ImportSelective impSelective, ScopeNameResolution scopeRes) {
		
		ISemanticContext context = scopeRes.getContext();
		
		INamedElement targetModule = ImportContent.findImportTargetModule(context, impSelective);
		if (targetModule == null)
			return;
			
		for(ASTNode impSelFrag: impSelective.impSelFrags) {
			if(impSelFrag instanceof RefImportSelection) {
				RefImportSelection refImportSelection = (RefImportSelection) impSelFrag;
				String name = refImportSelection.getDenulledIdentifier();
				// Do pre-emptive matching
				if(!scopeRes.getLookup().matchesName(name)) {
					continue;
				}
				INamedElement namedElement = refImportSelection.findTargetDefElement(context);
				
				/*FIXME: BUG here if element not found*/
				if(namedElement != null) { 
					scopeRes.visitNamedElement(namedElement);
				}
			}
		}
	}
	
}