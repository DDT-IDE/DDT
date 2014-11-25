package dtool.resolver;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeNode;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.PackageNamespace;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.references.RefImportSelection;

/**
 * Class with static methods encoding D entity lookup rules.
 * Uses an {@link ResolutionLookup} during lookups.
 * A scope plus it's outer scopes is called an extended scope.
 */
public class ReferenceResolver {
	
	public static INamedElement findModuleUnchecked(ISemanticContext mr, String moduleFullName) {
		return findModuleUnchecked(mr, new ModuleFullName(moduleFullName));
	}
	
	public static INamedElement findModuleUnchecked(ISemanticContext mr, ModuleFullName moduleName) {
		try {
			return mr.findModule(moduleName);
		} catch (ModuleSourceException pse) {
			/* FIXME: TODO: add error to SemanticResolution / semantic operation. */
			return null;
		}
	}
	
	/* ====================  reference lookup  ==================== */
	
	public static void resolveSearchInFullLexicalScope(final ASTNode node, CommonScopeLookup search) {
		findDefUnitInPrimitivesScope(search);
		if(search.isFinished())
			return;
		
		IScopeNode scope = getNearestLexicalScope(node);
		
		while(scope != null) {
			CommonScopeLookup.findDefUnitInScope(scope, search);
			if(search.isFinished())
				return;
			if(scope instanceof Module) {
				Module module = (Module) scope;
				findDefUnitInModuleDec(module, search);
				findDefUnitInObjectIntrinsic(search);
			}
			
			scope = scope.getOuterLexicalScope();
		}
	}
	
	public static IScopeNode getNearestLexicalScope(ASTNode node) {
		if (node instanceof IScopeNode)
			return (IScopeNode) node;
		
		return getOuterLexicalScope(node);
	}
	
	public static IScopeNode getOuterLexicalScope(ASTNode node) {
		ASTNode parent = node.getParent();
		if(parent == null) {
			return null;
		}
		return getNearestLexicalScope(parent);
	}
	
	public static void findDefUnitInPrimitivesScope(CommonScopeLookup search) {
		DeeLanguageIntrinsics.D2_063_intrinsics.primitivesScope.resolveSearchInScope(search);
	}
	
	public static void resolveSearchInScope(CommonScopeLookup search, IScopeNode scope) {
		if(scope != null) {
			CommonScopeLookup.findDefUnitInScope(scope, search);
		}
	}
	
	
	/* ====================  ==================== */
	
	private static void findDefUnitInObjectIntrinsic(CommonScopeLookup search) {
		INamedElement targetModule = ReferenceResolver.findModuleUnchecked(search.modResolver, "object");
		if (targetModule != null) {
			targetModule.resolveSearchInMembersScope(search);
		}
	}
	
	private static void findDefUnitInModuleDec(Module module, CommonScopeLookup search) {
		DeclarationModule decMod = module.md;
		INamedElement moduleElement;
		if(decMod != null) {
			
			if(decMod.packages.length == 0 || decMod.packages[0] == "") {
				moduleElement = module;
			} else {
				String[] packNames = decMod.packages;
				moduleElement = PackageNamespace.createPartialDefUnits(packNames, module);
			}
		} else {
			moduleElement = module;
		}
		search.evaluateNamedElementForSearch(moduleElement);
	}
	
	/* ====================  import lookup  ==================== */

	public static void findDefUnitInStaticImport(ImportContent importStatic, CommonScopeLookup search) {
		INamedElement namedElement = importStatic.getPartialDefUnit(search.modResolver);
		search.evaluateNamedElementForSearch(namedElement);
	}
	
	public static void findDefUnitInContentImport(ImportContent impContent, CommonScopeLookup search) {
		findDefUnitInStaticImport(impContent, search);
		//if(search.isScopeFinished()) return;
		
		INamedElement targetModule = findImportTargetModule(search.modResolver, impContent);
		if(targetModule != null) {
			targetModule.resolveSearchInMembersScope(search);
		}
	}
	
	private static INamedElement findImportTargetModule(ISemanticContext modResolver, IImportFragment impSelective) {
		String[] packages = impSelective.getModuleRef().packages.getInternalArray();
		String moduleName = impSelective.getModuleRef().module;
		
		return findModuleUnchecked(modResolver, new ModuleFullName(ArrayUtil.concat(packages, moduleName)));
	}
	
	public static void findDefUnitInSelectiveImport(
			ImportSelective impSelective, CommonScopeLookup search) {

		INamedElement targetModule = findImportTargetModule(search.modResolver, impSelective);
		if (targetModule == null)
			return;
			
		for(ASTNode impSelFrag: impSelective.impSelFrags) {
			if(impSelFrag instanceof RefImportSelection) {
				RefImportSelection refImportSelection = (RefImportSelection) impSelFrag;
				String name = refImportSelection.getDenulledIdentifier();
				// Do pre-emptive matching
				if(!search.matchesName(name)) {
					continue;
				}
				INamedElement namedElement = refImportSelection.findTargetDefElement(search.modResolver);
				if(namedElement != null) { 
					search.addMatch(namedElement);
				}
			}
		}
	}
	
}