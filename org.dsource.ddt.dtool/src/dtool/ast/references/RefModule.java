package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.Collection;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.ReferenceResolver;

/** 
 * A module reference (in import declarations only).
 */
public class RefModule extends NamedReference {
	
	//public String packageName;
<<<<<<< OURS
	public final String[] packages;
	public final String module;
	
=======
	public String[] packages;
	public String module;

>>>>>>> THEIRS
	public RefModule(String[] packages, String module, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.packages = packages;
		this.module = module;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, root);
			//TreeVisitor.acceptChildren(visitor, subent);
		}
		visitor.endVisit(this);	
	}

	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		Module originMod = NodeUtil.getParentModule(this);
		Module targetMod = ReferenceResolver.findModule(originMod, packages, module);
		return DefUnitSearch.wrapResult(targetMod);
	}
	
	public static class LiteModuleDummy extends DefUnit {
		public LiteModuleDummy(String defname) {
			super(null, new TokenInfo(defname), null);
		}

		@Override
		public EArcheType getArcheType() {
			return EArcheType.Module;
		}

		@Override
		public IScopeNode getMembersScope() {
			assertFail(); return null;
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
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
	public String getReferenceName() {
		return module;
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		String prefix = search.searchOptions.searchPrefix;

		Module module = NodeUtil.getParentModule(this);
		String[] strings = ReferenceResolver.findModules(module, prefix);
		for (int i = 0; i < strings.length; i++) {
			String name = strings[i];
			
			search.addMatch(new LiteModuleDummy(name));		
		}

	}
	
	@Override
	public String toStringAsElement() {
		String str = StringUtil.collToString(packages, ".");
		if(str.length() == 0)
			return module;
		else
			return str + "." + module;
	}

}
