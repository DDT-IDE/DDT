package dtool.resolver;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeNode;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.declarations.PackageNamespace;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;

/**
 * Class with static methods encoding D entity lookup rules.
 * Uses an {@link ResolutionLookup} during lookups.
 * A scope plus it's outer scopes is called an extended scope.
 */
public class ReferenceResolver {
	
	
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
		INamedElement targetModule = ResolvableSemantics.findModuleUnchecked(search.modResolver, "object");
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
	
}