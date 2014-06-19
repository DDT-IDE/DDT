package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.ModuleProxy;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.BaseLexElement;
import dtool.parser.IToken;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.PrefixDefUnitSearch;
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
	
	protected String getRefModuleFullyQualifiedName() {
		return toStringAsCode();
	}
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		if(search instanceof PrefixDefUnitSearch) {
			PrefixDefUnitSearch prefixDefUnitSearch = (PrefixDefUnitSearch) search;
			doSearch_forPrefixSearch(prefixDefUnitSearch);
		} else {
			assertTrue(isMissingCoreReference() == false);
			DefUnitSearch defUnitSearch = (DefUnitSearch) search;
			IModuleResolver mr = search.getModuleResolver();
			ModuleProxy moduleProxy = getModuleProxy(mr);
			if(moduleProxy.resolveDefUnit() != null) {
				defUnitSearch.addMatch(moduleProxy);
			}
		}
	}
	
	public ModuleProxy getModuleProxy(IModuleResolver mr) {
		return new ModuleProxy(getRefModuleFullyQualifiedName(), mr);
	}
	
	public void doSearch_forPrefixSearch(PrefixDefUnitSearch search) {
		String prefix = search.searchOptions.searchPrefix;
		
		String[] strings = search.resolveModules(prefix);
		for (int i = 0; i < strings.length; i++) {
			String fqName = strings[i];
			
			search.addMatchDirectly(new ModuleProxy(fqName, search.getModuleResolver()));
		}
	}
	
}