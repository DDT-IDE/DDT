package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.parser.Token;
import dtool.refmodel.INamedScope;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

/**
 * D Module. 
 * The top-level AST class, has no parent, is the first and main node of every compilation unit.
 */
public class Module extends DefUnit implements IScopeNode, INamedScope {
	
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
		
		public DeclarationModule(String[] packages, Token moduleDefUnit, SourceRange sourceRange) {
			assertNotNull(packages);
			this.packages = packages;
			this.moduleName = new ModuleDefSymbol(moduleDefUnit.source, moduleDefUnit.getSourceRange());
			parentize(moduleName);
			
			initSourceRange(sourceRange);
		}
		
		public ModuleDefSymbol getModuleSymbol() {
			return (ModuleDefSymbol) moduleName;
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DECL_MODULE;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				//TreeVisitor.acceptChildren(visitor, packages);
				TreeVisitor.acceptChildren(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append("module ");
			cp.appendList(packages, ".", true);
			cp.append(moduleName.name);
			cp.append(";");
		}
		
		@Override
		public String toStringAsElement() {
			ASTCodePrinter cp = new ASTCodePrinter();
			cp.appendList(packages, ".", true);
			cp.append(moduleName.name);
			return cp.toString();
		}
	}
	
	public static Module createModuleNoModuleDecl(SourceRange sourceRange, String moduleName,
		ArrayView<ASTNeoNode> members) {
		ModuleDefSymbol defSymbol = new ModuleDefSymbol(moduleName, null);
		return new Module(defSymbol, null, null, members, sourceRange);
	}
	
	public final DeclarationModule md;
	public final ArrayView<ASTNeoNode> members;
	
	public Module(ModuleDefSymbol defSymbol, Comment[] preComments, DeclarationModule md, 
			ArrayView<ASTNeoNode> members, SourceRange sourceRange) {
		super(defSymbol, preComments, sourceRange);
		defSymbol.module = this;
		this.md = parentize(md);
		this.members = parentize(members);
		assertNotNull(members);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.MODULE;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, md);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
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
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(md, cp.ST_SEP);
		cp.appendNodeList(members, cp.ST_SEP);
	}
	
	@Override
	public String toStringAsElement() {
		if(md == null) {
			return getName();
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