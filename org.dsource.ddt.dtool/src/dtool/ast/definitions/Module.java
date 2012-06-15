package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.tree.TreeVisitor;

import org.eclipse.dltk.core.ISourceModule;

import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

/**
 * D Module. 
 * The top-level AST class, has no parent, is the first and main node of every compilation unit.
 */
public class Module extends DefUnit implements IScopeNode {
	
	public static class ModuleDefSymbol extends DefSymbol {
		
		protected Module module;
		
		public ModuleDefSymbol(String id, SourceRange sourceRange) {
			super(id, sourceRange);
		}
		
		@Override
		public DefUnit getDefUnit() {
			return module;
		}
	}
	
	public static class DeclarationModule extends ASTNeoNode {
		
		public final DefSymbol moduleName; 
		public final String[] packages; // non-structural element
		
		public DeclarationModule(SourceRange sourceRange, String[] packages, DefSymbol moduleName) {
			initSourceRange(sourceRange);
			
			assertNotNull(packages);
			this.packages = packages;
			this.moduleName = moduleName; parentize(moduleName);
		}
		
		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				//TreeVisitor.acceptChildren(visitor, packages);
				TreeVisitor.acceptChildren(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public String toStringAsElement() {
			String str = StringUtil.collToString(packages, ".");
			if(str.length() == 0) {
				return moduleName.toStringAsElement();
			} else {
				return str + "." + moduleName.toStringAsElement();
			}
		}
	}
	
	private Object moduleUnit; // The compilation unit/Model Element
	
	public final DeclarationModule md;
	public final ArrayView<ASTNeoNode> members;
	
	public static Module createModule(SourceRange sourceRange, Comment[] comments, String[] packages,
			TokenInfo defName, SourceRange declRange, ArrayView<ASTNeoNode> members) {
		ModuleDefSymbol defSymbol = new ModuleDefSymbol(defName.value, defName.getSourceRange());
		DeclarationModule md = new DeclarationModule(declRange, packages, defSymbol);
		return new Module(defSymbol, comments, md, members, sourceRange);
	}
	
	public static Module createModuleNoModuleDecl(SourceRange sourceRange, ArrayView<ASTNeoNode> members,
			String moduleName) {
		ModuleDefSymbol defSymbol = new ModuleDefSymbol(moduleName, null);
		return new Module(defSymbol, null, null, members, sourceRange);
	}
	
	protected Module(ModuleDefSymbol defSymbol, Comment[] preComments, DeclarationModule md, 
			ArrayView<ASTNeoNode> members, SourceRange sourceRange) {
		super(sourceRange, defSymbol, preComments);
		defSymbol.module = this;
		this.md = parentize(md);
		this.members = parentize(members);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, md);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}
	
	public void setModuleUnit(ISourceModule modUnit) {
		//assertTrue(modUnit.exists());
		if(this.moduleUnit != null) {
			assertTrue(this.moduleUnit.equals(modUnit));
		}
		this.moduleUnit = modUnit;
	}
	@Deprecated
	public ISourceModule getModuleUnit() {
		return (ISourceModule) moduleUnit;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
		return members.iterator();
	}
	
	public String getDeclaredQualifiedName() {
		StringBuilder fullName = new StringBuilder();
		if(md != null) {
			for (String packageStr : md.packages) {
				fullName.append(packageStr);
				fullName.append(".");
			}
		}
		fullName.append(getName());
		return fullName.toString();
	}
	
	public String[] getDeclaredPackages() {
		if(md != null) {
			return md.packages;
		}
		return NewUtils.EMPTY_STRING_ARRAY;
	}
	
	@Override
	public String toStringAsElement() {
		if(md == null) {
			return "<undefined>"; // BUG here
		}
		return md.toStringAsElement();
	}
	
	@Override
	public String toStringForHoverSignature() {
		return toStringAsElement();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName();
	}
	
}