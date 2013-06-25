package dtool.ast.references;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeUtil;
import dtool.ast.declarations.SyntheticDefUnit;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;
import dtool.parser.Token;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;
import dtool.util.ArrayViewExt;

/** 
 * A module reference (in import declarations only).
 */
public class RefModule extends NamedReference {
	
	public final ArrayView<Token> packageList;
	public final ArrayViewExt<String> packages; // TODO: Old API, refactor?
	public final String module;
	
	public RefModule(ArrayView<Token> packageList, String module) {
		this.packageList = assertNotNull_(packageList);
		this.packages = ArrayViewExt.create(NodeUtil.tokenArrayToStringArray(packageList));
		this.module = module;
	}
	
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_MODULE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendTokenList(packageList, ".", true);
		cp.append(module);
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
	
	public static class LiteModuleDummy extends SyntheticDefUnit {
		
		public LiteModuleDummy(String defname) {
			super(defname);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Module;
		}
		
		@Override
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			assertFail(); return null;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			assertFail();
		}
		
		@Override
		public String toStringForCodeCompletion() {
			return getName();
		}
		
		@Override
		public String toStringForHoverSignature() {
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
			String name = strings[i];
			
			search.addMatch(new LiteModuleDummy(name));		
		}
	}
	
	@Override
	public String toStringAsElement() {
		assertNotNull(module);
		return toStringAsCode();
	}
	
}