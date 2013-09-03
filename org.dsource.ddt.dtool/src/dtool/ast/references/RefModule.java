package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import descent.core.ddoc.Ddoc;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.SyntheticDefUnit;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;
import dtool.parser.BaseLexElement;
import dtool.parser.IToken;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.ResolverUtil;
import dtool.resolver.ResolverUtil.ModuleNameDescriptor;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;
import dtool.util.ArrayViewExt;

/** 
 * A module reference (in import declarations only).
 */
public class RefModule extends NamedReference {
	
	public final ArrayView<IToken> packageList;
	public final BaseLexElement moduleToken;
	public final ArrayViewExt<String> packages; // TODO: Old API, refactor?
	public final String module;
	
	public RefModule(ArrayView<IToken> packageList, BaseLexElement moduleToken) {
		this.packageList = assertNotNull(packageList);
		this.moduleToken = assertNotNull(moduleToken);
		this.packages = ArrayViewExt.create(tokenArrayToStringArray(packageList));
		this.module = moduleToken.getSourceValue();
	}
	
	public static String[] tokenArrayToStringArray(ArrayView<IToken> tokenArray) {
		String[] stringArray = new String[tokenArray.size()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = tokenArray.get(i).getSourceValue();
		}
		return stringArray;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_MODULE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	public String getModuleSimpleName() {
		return module;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendTokenList(packageList, ".", true);
		cp.append(module);
	}
	
	@Override
	public String getCoreReferenceName() {
		return module;
	}
	
	@Override
	public LightweightModuleProxy findTargetDefElement(IModuleResolver moduleResolver) {
		return (LightweightModuleProxy) super.findTargetDefElement(moduleResolver);
	}
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		if(search instanceof PrefixDefUnitSearch) {
			PrefixDefUnitSearch prefixDefUnitSearch = (PrefixDefUnitSearch) search;
			doSearch_forPrefixSearch(prefixDefUnitSearch);
		} else {
			DefUnitSearch defUnitSearch = (DefUnitSearch) search;
			IModuleResolver mr = search.getModuleResolver();
			Module targetModule;
			try {
				targetModule = mr.findModule(packages.getInternalArray(), module);
			} catch(Exception e) {
				throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
			}
			if(targetModule != null) {
				defUnitSearch.addMatch(new LightweightModuleProxy(targetModule.getFullyQualifiedName(), mr));
			}
		}
	}
	
	public void doSearch_forPrefixSearch(PrefixDefUnitSearch search) {
		String prefix = search.searchOptions.searchPrefix;
		
		String[] strings = search.resolveModules(prefix);
		for (int i = 0; i < strings.length; i++) {
			String fqName = strings[i];
			
			search.addMatch(new LightweightModuleProxy(fqName, search.getModuleResolver()));
		}
	}
	
	public static class LightweightModuleProxy extends SyntheticDefUnit {
		
		protected final IModuleResolver moduleResolver;

		public LightweightModuleProxy(String fqModuleName, IModuleResolver moduleResolver) {
			super(fqModuleName);
			this.moduleResolver = moduleResolver;
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Module;
		}
		
		@Override
		public String getModuleFullyQualifiedName() {
			return getName();
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			assertFail();
		}
		
		@Override
		public Module resolveDefUnit() {
			ModuleNameDescriptor nameDescriptor = ResolverUtil.getNameDescriptor(getModuleFullyQualifiedName());
			try {
				return moduleResolver.findModule(nameDescriptor.packages, nameDescriptor.moduleName);
			} catch(Exception e) {
				throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
			}
		}
		
		@Override
		public Ddoc resolveDDoc() {
			DefUnit resolvedModule = resolveDefUnit();
			if(resolvedModule != null) {
				return resolveDefUnit().getDDoc();
			}
			return null;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			DefUnit resolvedModule = resolveDefUnit();
			if(resolvedModule != null) {
				resolvedModule.resolveSearchInMembersScope(search);
			}
		}
		
	}
	
}