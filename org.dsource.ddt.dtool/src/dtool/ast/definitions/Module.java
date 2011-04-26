package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Arrays;
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

/**
 * D Module.
 * Uses the fake name "<undefined>" when a module name is not defined.
 * XXX: Should we infer the module name from the module unit instead?
 */
public class Module extends DefUnit implements IScopeNode {
	
	public static class ModuleDefSymbol extends DefSymbol {
		
		protected Module module;
		
		public ModuleDefSymbol(TokenInfo id) {
			super(id, null);
		}
		
		public ModuleDefSymbol(String id) {
			super(id);
		}
		
		@Override
		public DefUnit getDefUnit() {
			return module;
		}
	}
	
	public static class DeclarationModule extends ASTNeoNode {
		
		public DefSymbol moduleName; 
		public String[] packages; // non-structural element
		
		public DeclarationModule(SourceRange sourceRange, String[] packages, DefSymbol moduleName) {
			initSourceRange(sourceRange);
			
			assertNotNull(packages);
			this.packages = packages;
			this.moduleName = moduleName; 
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
			//String str = ASTPrinter.toStringAsElements(packages, "."); 
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
	public final ASTNeoNode[] members;
	
	public static Module createModule(SourceRange sourceRange, Comment[] comments, String[] packages,
			TokenInfo defName, SourceRange declRange, ASTNeoNode[] members) {
		ModuleDefSymbol defSymbol = new ModuleDefSymbol(defName);
		DeclarationModule md = new DeclarationModule(declRange, packages, defSymbol);
		return new Module(defSymbol, comments, md, members, sourceRange);
	}
	
	public static Module createModule(SourceRange sourceRange, ASTNeoNode[] members) {
		ModuleDefSymbol defSymbol = new ModuleDefSymbol("<unnamed>");
		return new Module(defSymbol, null, null, members, sourceRange);
	}
	
	protected Module(ModuleDefSymbol defSymbol, Comment[] preComments, DeclarationModule md, 
			ASTNeoNode[] members, SourceRange sourceRange) {
		super(sourceRange, defSymbol, preComments);
		defSymbol.module = this;
		this.md = md;
		this.members = members;
	}
	
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	public void setModuleUnit(ISourceModule modUnit) {
		//assertTrue(modUnit.exists());
		if(this.moduleUnit != null) {
			assertTrue(this.moduleUnit.equals(modUnit));
		}
		this.moduleUnit = modUnit;
	}
	public ISourceModule getModuleUnit() {
		return (ISourceModule) moduleUnit;
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
	
	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes() {
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return Arrays.asList(members).iterator();
	}
	
	@Override
	public String toStringAsElement() {
		if(md == null) {
			return "<undefined>";
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
