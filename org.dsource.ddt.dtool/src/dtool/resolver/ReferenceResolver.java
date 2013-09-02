package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import melnorme.utilbox.core.ExceptionAdapter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.IASTNode;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.PartialPackageDefUnitOfPackage;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.Reference;
import dtool.parser.DeeParserResult;
import dtool.resolver.api.IModuleResolver;

/**
 * Class with static methods encoding D entity lookup rules.
 * Uses an {@link DefUnitSearch} during lookups.
 * A scope plus it's outer scopes is called an extended scope.
 */
public class ReferenceResolver {
	
	private static final String[] EMPTY_PACKAGE = new String[0];
	
	public static Module findModuleUnchecked(IModuleResolver modResolver, String[] packages, String module) {
		try {
			return modResolver.findModule(packages, module);
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}
	
	/* ====================  reference lookup  ==================== */
	
	public static void resolveSearchInFullLexicalScope(final ASTNode node, CommonDefUnitSearch search) {
		IScopeNode scope = getNearestLexicalScope(node);
		assertNotNull(scope);
		
		findDefUnitInNativesScope(search);
		
		while(true) {
			findDefUnitInScope(scope, search);
			if(search.isFinished())
				return;

			IScopeNode outerScope = scope.getOuterLexicalScope();
			if(outerScope == null) {
				if(scope instanceof Module) {
					Module module = (Module) scope;
					findDefUnitInModuleDec(module, search);
					findDefUnitInObjectIntrinsic(search);
				}
				return;
			}
			scope = outerScope; 
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
	
	public static void findDefUnitInNativesScope(CommonDefUnitSearch search) {
		findDefUnitInScope(NativesScope.nativesScope, search);
	}
	
	public static void resolveSearchInScope(CommonDefUnitSearch search, IScopeNode scope) {
		if(scope != null) {
			findDefUnitInScope(scope, search);
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
	public static void findDefUnitInScope(IScopeProvider scope, CommonDefUnitSearch search) {
		assertNotNull(scope);
		if(search.hasSearched(scope))
			return;
		
		search.enterNewScope(scope);
		scope.resolveSearchInScope(search);
	}
	
	public static void findInNodeList(CommonDefUnitSearch search, Iterable<? extends IASTNode> nodeIterable, 
		boolean isSequentialLookup) {
		if(nodeIterable != null) {
			if(search.isFinished())
				return;
			findDefUnits(search, nodeIterable.iterator(), isSequentialLookup, false);
			if(search.isFinished())
				return;
			findDefUnits(search, nodeIterable.iterator(), isSequentialLookup, true);
		}
	}
	
	public static void findDefUnits(CommonDefUnitSearch search, Iterator<? extends IASTNode> iter,
			boolean isSequentialLookup, boolean importsOnly) {
		
		while(iter.hasNext()) {
			IASTNode node = iter.next();
			
			int refOffset = search.refOffset;
			// Check if we have passed the reference offset
			if(isSequentialLookup && refOffset < node.getStartPos()) {
				return;
			}
			
			evaluateNodeForSearch(search, isSequentialLookup, importsOnly, node);
		}
	}
	
	public static void evaluateNodeForSearch(CommonDefUnitSearch search, boolean isSequentialLookup, 
		boolean importsOnly, IASTNode node) {
		
		if(node instanceof INonScopedBlock) {
			INonScopedBlock container = ((INonScopedBlock) node);
			findDefUnits(search, container.getMembersIterator(), isSequentialLookup, importsOnly);
			if(search.isFinished() && search.findOnlyOne)
				return; // Return if we only want one match in the scope
		}
		if(!importsOnly && node instanceof DefUnit) {
			DefUnit defunit = (DefUnit) node;
			if(search.matches(defunit)) {
				search.addMatch(defunit);
				if(search.isFinished() && search.findOnlyOne)
					return; // Return if we only want one match in the scope
			}
		} 
		else if(importsOnly && node instanceof DeclarationImport) {
			DeclarationImport declImport = (DeclarationImport) node;
			
			if(!declImport.isTransitive && !privateNodeIsVisible(declImport, search.getSearchOriginModule()))
				return; // Don't consider private imports
			
			for (IImportFragment impFrag : declImport.imports) {
				impFrag.searchInSecondaryScope(search);
				// continue regardless of search.findOnlyOne because of partial packages
			}
		}
	}
	
	public static boolean privateNodeIsVisible(ASTNode node, Module searchOriginModule) {
		if(searchOriginModule == null) 
			return false;
		Module nodeModule = node.getModuleNode();
		// only visible if in node in same module as search origin ref.
		return searchOriginModule.getFullyQualifiedName().equals(nodeModule.getFullyQualifiedName());
	}
	
	/* ====================  ==================== */
	
	private static void findDefUnitInObjectIntrinsic(CommonDefUnitSearch search) {
		Module targetModule = search.resolveModule(EMPTY_PACKAGE, "object");
		if (targetModule != null) {
			findDefUnitInScope(targetModule, search);
		}
	}
	
	private static void findDefUnitInModuleDec(Module module, CommonDefUnitSearch search) {
		DeclarationModule decMod = module.md;
		if(decMod != null) {
			DefUnit defUnit;
			
			if(decMod.packages.length == 0 || decMod.packages[0] == "") {
				defUnit = module;
			} else {
				String[] packNames = decMod.packages;
				
				defUnit = PartialPackageDefUnitOfPackage.createPartialDefUnits(packNames, null, module);
			}
			
			if(search.matches(defUnit))
				search.addMatch(defUnit);
		} else {
			if(search.matches(module)) {
				search.addMatch(module);
			}
		}
	}

	/* ====================  import lookup  ==================== */

	public static void findDefUnitInStaticImport(ImportContent importStatic, CommonDefUnitSearch search) {
		INamedElement defunit = importStatic.getPartialDefUnit(search.modResolver);
		if(defunit != null && search.matches(defunit))
			search.addMatch(defunit);
	}
	
	public static void findDefUnitInContentImport(ImportContent impContent, CommonDefUnitSearch search) {
		findDefUnitInStaticImport(impContent, search);
		//if(search.isScopeFinished()) return;
		
		Module targetModule = findImportTargetModule(search.modResolver, impContent);
		if(targetModule != null) {
			findDefUnitInScope(targetModule, search);
		}
	}
	
	private static Module findImportTargetModule(IModuleResolver modResolver, IImportFragment impSelective) {
		String[] packages = impSelective.getModuleRef().packages.getInternalArray();
		String modules = impSelective.getModuleRef().module;
		Module targetModule = findModuleUnchecked(modResolver, packages, modules);
		return targetModule;
	}
	
	public static void findDefUnitInSelectiveImport(
			ImportSelective impSelective, CommonDefUnitSearch search) {

		Module targetModule = findImportTargetModule(search.modResolver, impSelective);
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
	
	public static class DirectDefUnitResolve {
		
		protected ASTNode pickedNode;
		protected Reference pickedRef;
		public Collection<INamedElement> resolvedDefUnits;
		public boolean invalidPickRef = false;
		
		public void pickLocation(Module module, int offset) {
			ASTNodeFinder nodeFinder = new ASTNodeFinder(module, offset, true);
			
			if(nodeFinder.matchOnLeft instanceof Reference) {
				this.pickedNode = nodeFinder.matchOnLeft;
				this.pickedRef = (Reference) pickedNode;
			} else if(nodeFinder.match instanceof Reference) {
				this.pickedRef = (Reference) nodeFinder.match;
			}
			this.pickedNode = nodeFinder.match;
			
			if(pickedRef instanceof CommonRefQualified || !(pickedRef instanceof NamedReference)) {
				invalidPickRef = true;
			}
		}
		
		public boolean isValidPickRef() {
			return pickedRef != null && invalidPickRef == false;
		}
		
		public Collection<INamedElement> getResolvedDefUnits() {
			assertTrue(isValidPickRef()); // a valid ref must have picked from offset
			return resolvedDefUnits;
		}
		
		public void resolveAtoffset(DeeParserResult parseResult, int offset, IModuleResolver mr) {
			pickLocation(parseResult.module, offset);
			
			if(isValidPickRef()) {
				resolvedDefUnits = pickedRef.findTargetDefElements(mr, false);
			}
		}
		
	}
	
	public static DirectDefUnitResolve resolveAtOffset(DeeParserResult parseResult, int offset, IModuleResolver mr) {
		DirectDefUnitResolve refResolve = new DirectDefUnitResolve();
		refResolve.resolveAtoffset(parseResult, offset, mr);
		return refResolve;
	}
	
}