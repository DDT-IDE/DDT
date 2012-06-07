package dtool.refmodel;

import java.util.Iterator;

import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.misc.IteratorUtil;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.ImportFragment;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportStatic;
import dtool.ast.declarations.PartialPackageDefUnitOfPackage;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * Class with static methods encoding D entity lookup rules.
 * Uses an {@link DefUnitSearch} during lookups.
 * A scope plus it's outer scopes is called an extended scope.
 */
public class ReferenceResolver {
	
	private static final String[] EMPTY_PACKAGE = new String[0];
	protected static IModuleResolver modResolver;

	// XXX: blergh, remove this singleton design
	/** Initializes the EntityResolver with a ModuleResolver. */
	public static void initializeEntityResolver(IModuleResolver modResolver) {
		ReferenceResolver.modResolver = modResolver;
	}

	/** Convenience method to call mod resolver. */
	public static Module findModule(Module refSourceModule, String[] packages, String module) {
		try {
			return modResolver.findModule(refSourceModule, packages, module);
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}

	/** Convenience method to call mod resolver. */
	public static String[] findModules(Module refSourceModule, String fqName) {
		try {
			return modResolver.findModules(refSourceModule, fqName);
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}

	/* ====================  reference lookup  ==================== */
	
	
	/** Searches for the given CommonDefUnitSearch search, in the scope's 
	 * immediate namespace, secondary namespace (imports), and super scopes.
	 * 
	 * The set of matched {@link DefUnit}s must all be visible in the same
	 * non-extended scope, (altough due to imports, they may originate from 
	 * different scopes XXX: fix this behavior? This is an ambiguity error in D).
	 */
	public static void findDefUnitInExtendedScope(IScopeNode scope,
			CommonDefUnitSearch search) {

		do {
			findDefUnitInScope(scope, search);
			if(search.isFinished())
				return;

			IScopeNode outerscope = NodeUtil.getOuterScope(scope);
			if(outerscope == null) {
				Module module = (Module) scope;
				findDefUnitInModuleDec(module, search);
				findDefUnitInObjectIntrinsic(module, search);
				return;
			}

			// retry in outer scope
			scope = outerscope; 
		} while (true);
		
	}

	private static void findDefUnitInObjectIntrinsic(Module originModule, CommonDefUnitSearch search) {
		Module targetModule = findModule(originModule, EMPTY_PACKAGE, "object");
		if (targetModule != null)
			findDefUnitInScope(targetModule, search);
	}

	private static void findDefUnitInModuleDec(Module module,
			CommonDefUnitSearch search) {
		DeclarationModule decMod = module.md;
		if(decMod != null) {
			DefUnit defUnit;
			
			if(decMod.packages.length == 0 || decMod.packages[0] == "") {
				defUnit = module;
			} else {
				// Cache this?
			
				String[] packNames = new String[decMod.packages.length];
				for(int i = 0; i< decMod.packages.length; ++i){
					packNames[i] = decMod.packages[i];
				}
				
				defUnit = PartialPackageDefUnitOfPackage.createPartialDefUnits(
						packNames, null, module);
			}
			
			if(search.matches(defUnit))
				search.addMatch(defUnit);
		}
	}

	/** Searches for the given CommonDefUnitSearch search, in the scope's 
	 * immediate namespace, secondary namespace (imports), and super scopes.
	 *  
	 * Does not search, if the scope has alread been searched in this search.
	 * The set of matched {@link DefUnit}s must all be visible in the same
	 * non-extended scope, (altough due to imports, they may originate from 
	 * different scopes XXX: fix this behavior? This is an ambiguity error in D).
	 */
	public static void findDefUnitInScope(IScope scope, CommonDefUnitSearch search) {
		if(search.hasSearched(scope))
			return;
		
		search.enterNewScope(scope);
		
		findDefUnitInImmediateScope(scope, search);
		if(search.isFinished())
			return;
		
		findDefUnitInSecondaryScope(scope, search);
		if(search.isFinished())
			return;

		// Search super scope 
		if(scope.getSuperScopes() != null) {
			for(IScope superscope : scope.getSuperScopes()) {
				if(superscope != null)
					findDefUnitInScope(superscope, search); 
				if(search.isFinished())
					return;
			}
		}
		
	}
	

	private static void findDefUnitInImmediateScope(IScope scope, CommonDefUnitSearch search) {
		Iterator<IASTNode> iter = IteratorUtil.recast(scope.getMembersIterator());
		
		findDefUnits(search, iter, scope.hasSequentialLookup(), false, null);
	}
	
	private static void findDefUnitInSecondaryScope(IScope scope, CommonDefUnitSearch search) {
		Iterator<IASTNode> iter = IteratorUtil.recast(scope.getMembersIterator());

		IScope thisModule = scope.getModuleScope();
		findDefUnits(search, iter, scope.hasSequentialLookup(), true, thisModule);
	}


	private static void findDefUnits(CommonDefUnitSearch search,
			Iterator<? extends IASTNode> iter, boolean isStatementScope, 
			boolean importsOnly, IScope thisModule) {
		
		IScope refsModule = search.getReferenceModuleScope();
		int refOffset = search.refOffset;
		
		while(iter.hasNext()) {
			IASTNode elem = iter.next();
			
			if (elem instanceof INonScopedBlock) {
				INonScopedBlock container = ((INonScopedBlock) elem);
				findDefUnits(search, container.getMembersIterator(), 
						isStatementScope, importsOnly, thisModule);
				if(search.isFinished() && search.findOnlyOne)
					return; // Return if we only want one match in the scope
			}
			
			// Check if the reference is before the point of definition
			if(isStatementScope && refOffset < elem.getEndPos()) {
				/* XXX: Technically we could return right away, since
				 * no further nodes should match, but keep going in case 
				 * there are source range errors. */ 
				continue;
			}

			if(!importsOnly && elem instanceof DefUnit) {
				DefUnit defunit = (DefUnit) elem;
				if(search.matches(defunit)) {
					search.addMatch(defunit);
					if(search.isFinished() && search.findOnlyOne)
						return; // Return if we only want one match in the scope
				}
			} else if(importsOnly && elem instanceof DeclarationImport) {
				DeclarationImport declImport = (DeclarationImport) elem;

				if(!refsModule.equals(thisModule) && !declImport.isTransitive)
					continue; // Don't consider private imports
				
				for (ImportFragment impFrag : declImport.imports) {
					impFrag.searchInSecondaryScope(search);
					// continue regardless of search.findOnlyOne because of partial packages
				}
			} 

		}
	}

	
	/* ====================  import lookup  ==================== */

	public static void findDefUnitInStaticImport(ImportStatic importStatic, CommonDefUnitSearch search) {
		DefUnit defunit = importStatic.getPartialDefUnit();
		if(defunit != null && search.matches(defunit))
			search.addMatch(defunit);
	}

	public static void findDefUnitInContentImport(ImportContent impContent, CommonDefUnitSearch search) {
		findDefUnitInStaticImport(impContent, search);
		//if(search.isScopeFinished()) return;

		Module targetModule = findImporTargetModule(impContent);
		if (targetModule != null)
			findDefUnitInScope(targetModule, search);
	}
	
	private static Module findImporTargetModule(ImportFragment impSelective) {
		Module refModule = NodeUtil.getParentModule(impSelective);
		String[] packages = impSelective.moduleRef.packages.getInternalArray();
		String modules = impSelective.moduleRef.module;
		Module targetModule;
		targetModule = findModule(refModule, packages, modules);
		return targetModule;
	}

	public static void findDefUnitInSelectiveImport(
			ImportSelective impSelective, CommonDefUnitSearch search) {

		Module targetModule = findImporTargetModule(impSelective);
		if (targetModule == null)
			return;
			
		for(ASTNeoNode impSelFrag: impSelective.impSelFrags) {
			if(impSelFrag instanceof RefImportSelection) {
				String name;
				name = ((RefImportSelection) impSelFrag).name;
				// Do pre-emptive matching
				if(search.matchesName(name)) {
					findDefUnitInScope(targetModule, search);
				}
			} // Aliases are matched in the primary namespace 
			/*
				else if(impSelFrag instanceof ImportSelectiveAlias) {
				ImportSelectiveAlias selFrag = (ImportSelectiveAlias) impSelFrag;
				if(search.matches(selFrag))
					search.addMatch(selFrag);
			} */
		}
	}

	public static IScopeNode getStartingScope(RefIdentifier refSingle) {
		IScopeNode scope = NodeUtil.getOuterScope(refSingle);
		if(scope instanceof DefinitionFunction) {
			// Skip it as this scope can't look into itself
			scope = NodeUtil.getOuterScope(scope);
			
		}
		return scope;
	}


}
