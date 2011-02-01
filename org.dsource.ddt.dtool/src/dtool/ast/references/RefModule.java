package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.Collection;
import java.util.List;

import melnorme.utilbox.misc.StringUtil;
import descent.internal.compiler.parser.IdentifierExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
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
	public String[] packages;
	public String module;

	public RefModule(List<IdentifierExp> packages, IdentifierExp id) {
		this.module = new String(id.ident);
		if(packages == null) {
			this.packages = new String[0];
			setSourceRange(id);
		} else {
			this.packages = new String[packages.size()];
			for (int i = 0; i < packages.size(); i++) {
				this.packages[i] = new String(packages.get(i).ident);
			}
			int startPos = packages.get(0).getStartPos();
			setSourceRange(startPos, id.getEndPos() - startPos);
		}
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
			super(new Symbol(defname));
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
	protected String getReferenceName() {
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
