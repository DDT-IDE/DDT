package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

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
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		Module targetMod;
		try {
			targetMod = moduleResolver.findModule(packages.getInternalArray(), module);
		} catch (Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		return DefUnitSearch.wrapResult(targetMod);
	}
	
	public Module findTargetModule(IModuleResolver moduleResolver) {
		try {
			Module targetMod = moduleResolver.findModule(packages.getInternalArray(), module);
			return targetMod;
		} catch (Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		if(search instanceof PrefixDefUnitSearch) {
			PrefixDefUnitSearch prefixDefUnitSearch = (PrefixDefUnitSearch) search;
			doSearch_forPrefixSearch(prefixDefUnitSearch);
		} else {
			assertFail();
			// TODO: harmonize both kinds of searches
		}
	}
	
	public void doSearch_forPrefixSearch(PrefixDefUnitSearch search) {
		String prefix = search.searchOptions.searchPrefix;
		
		String[] strings = search.resolveModules(prefix);
		for (int i = 0; i < strings.length; i++) {
			String fqName = strings[i];
			
			search.addMatch(new LightweightModuleProxy(fqName));		
		}
	}
	
	public static class LightweightModuleProxy extends SyntheticDefUnit {
		
		public LightweightModuleProxy(String fqModuleName) {
			super(fqModuleName);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Module;
		}
		
		public String getFullyQualifiedName() {
			return getName();
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			throw assertFail();
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			assertFail();
		}
		
	}
	
}