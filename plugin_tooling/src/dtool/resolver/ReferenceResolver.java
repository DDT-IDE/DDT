package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.lang.tooling.engine.scoping.IScopeProvider;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.PackageNamespace;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.references.CommonQualifiedReference;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.Reference;
import dtool.parser.DeeParserResult;

/**
 * Class with static methods encoding D entity lookup rules.
 * Uses an {@link DefUnitSearch} during lookups.
 * A scope plus it's outer scopes is called an extended scope.
 */
public class ReferenceResolver {
	
	public static INamedElement findModuleUnchecked(IModuleResolver mr, String moduleFullName) {
		return findModuleUnchecked(mr, new ModuleFullName(moduleFullName));
	}
	
	public static INamedElement findModuleUnchecked(IModuleResolver mr, ModuleFullName moduleName) {
		try {
			return mr.findModule(moduleName);
		} catch (ModuleSourceException pse) {
			/* FIXME: TODO: add error to SemanticResolution / semantic operation. */
			return null;
		}
	}
	
	/* ====================  reference lookup  ==================== */
	
	public static void resolveSearchInFullLexicalScope(final ASTNode node, CommonDefUnitSearch search) {
		findDefUnitInPrimitivesScope(search);
		if(search.isFinished())
			return;
		
		IScopeNode scope = getNearestLexicalScope(node);
		
		while(scope != null) {
			findDefUnitInScope(scope, search);
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
	
	public static void findDefUnitInPrimitivesScope(CommonDefUnitSearch search) {
		LanguageIntrinsics.D2_063_intrinsics.primitivesScope.resolveSearchInScope(search);
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
			if(search.isFinished() && search.findOnlyOne) // TODO make BUG HERE 
				return;
		}
	}
	
	public static void evaluateNodeForSearch(CommonDefUnitSearch search, boolean isSequentialLookup, 
		boolean importsOnly, IASTNode node) {
		
		if(node instanceof INonScopedContainer) {
			INonScopedContainer container = ((INonScopedContainer) node);
			findDefUnits(search, container.getMembersIterator(), isSequentialLookup, importsOnly);
		}
		if(!importsOnly && node instanceof DefUnit) {
			DefUnit defunit = (DefUnit) node;
			search.visitElement(defunit);
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
	
	public static void findInNamedElementList(CommonDefUnitSearch search, 
		Iterable<? extends ILangNamedElement> elementIterable) {
		if(elementIterable != null) {
			if(search.isFinished())
				return;
			
			for (ILangNamedElement namedElement : elementIterable) {
				evaluateNamedElementForSearch(search, namedElement);
				if(search.isFinished() && search.findOnlyOne) // TODO make BUG HERE 
					return;
			}
		}
	}
	
	public static void evaluateNamedElementForSearch(CommonDefUnitSearch search, ILangNamedElement namedElement) {
		if(namedElement != null) {
			search.visitElement(namedElement);
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
		INamedElement targetModule = ReferenceResolver.findModuleUnchecked(search.modResolver, "object");
		if (targetModule != null) {
			targetModule.resolveSearchInMembersScope(search);
		}
	}
	
	private static void findDefUnitInModuleDec(Module module, CommonDefUnitSearch search) {
		DeclarationModule decMod = module.md;
		ILangNamedElement moduleElement;
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
		evaluateNamedElementForSearch(search, moduleElement);
	}
	
	/* ====================  import lookup  ==================== */

	public static void findDefUnitInStaticImport(ImportContent importStatic, CommonDefUnitSearch search) {
		ILangNamedElement namedElement = importStatic.getPartialDefUnit(search.modResolver);
		evaluateNamedElementForSearch(search, namedElement);
	}
	
	public static void findDefUnitInContentImport(ImportContent impContent, CommonDefUnitSearch search) {
		findDefUnitInStaticImport(impContent, search);
		//if(search.isScopeFinished()) return;
		
		INamedElement targetModule = findImportTargetModule(search.modResolver, impContent);
		if(targetModule != null) {
			targetModule.resolveSearchInMembersScope(search);
		}
	}
	
	private static INamedElement findImportTargetModule(IModuleResolver modResolver, IImportFragment impSelective) {
		String[] packages = impSelective.getModuleRef().packages.getInternalArray();
		String moduleName = impSelective.getModuleRef().module;
		
		return findModuleUnchecked(modResolver, new ModuleFullName(ArrayUtil.concat(packages, moduleName)));
	}
	
	public static void findDefUnitInSelectiveImport(
			ImportSelective impSelective, CommonDefUnitSearch search) {

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
				ILangNamedElement namedElement = refImportSelection.findTargetDefElement(search.modResolver);
				if(namedElement != null) { 
					search.addMatch(namedElement);
				}
			}
		}
	}
	
	public static class DirectDefUnitResolve {
		
		protected ASTNode pickedNode;
		protected Reference pickedRef;
		public Collection<ILangNamedElement> resolvedDefUnits;
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
			
			if(pickedRef instanceof CommonQualifiedReference || !(pickedRef instanceof NamedReference)) {
				invalidPickRef = true;
			}
		}
		
		public boolean isValidPickRef() {
			return pickedRef != null && invalidPickRef == false;
		}
		
		public Collection<ILangNamedElement> getResolvedDefUnits() {
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