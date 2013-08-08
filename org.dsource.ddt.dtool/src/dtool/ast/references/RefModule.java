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
import dtool.parser.IToken;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;
import dtool.util.ArrayViewExt;

/** 
 * A module reference (in import declarations only).
 */
public class RefModule extends NamedReference {
	
	public final ArrayView<IToken> packageList;
	public final ArrayViewExt<String> packages; // TODO: Old API, refactor?
	public final String module;
	
	public RefModule(ArrayView<IToken> packageList, String module) {
		this.packageList = assertNotNull(packageList);
		this.packages = ArrayViewExt.create(tokenArrayToStringArray(packageList));
		this.module = module;
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
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendTokenList(packageList, ".", true);
		cp.append(module);
	}
	
	@Override
	public boolean syntaxIsMissingIdentifier() {
		return module.isEmpty();
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
	
	public static class LiteModuleDummy extends SyntheticDefUnit {
		
		public LiteModuleDummy(String fqModuleName) {
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
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			assertFail(); return null;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			assertFail();
		}
		
		@Override
		public String toStringForCodeCompletion() {
			return getName();
		}
		
	}
	
	
	@Override
	public String getTargetSimpleName() {
		return module;
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		String prefix = search.searchOptions.searchPrefix;
		
		String[] strings = search.resolveModules(prefix);
		for (int i = 0; i < strings.length; i++) {
			String fqName = strings[i];
			
			search.addMatch(new LiteModuleDummy(fqName));		
		}
	}
	
	@Override
	public String toStringAsElement() {
		assertNotNull(module);
		return toStringAsCode();
	}
	
}