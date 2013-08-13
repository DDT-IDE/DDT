package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Iterator;
import java.util.List;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.RefModule;
import dtool.parser.BaseLexElement;
import dtool.parser.IToken;
import dtool.parser.Token;
import dtool.resolver.INamedScope;
import dtool.resolver.IScope;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

/**
 * D Module. 
 * The top-level AST class, has no parent, is the first and main node of every compilation unit.
 */
public class Module extends DefUnit implements IScope, INamedScope {
	
	public static class ModuleDefSymbol extends DefSymbol {
		
		protected Module module;
		
		public ModuleDefSymbol(String id) {
			super(id);
		}
		
		@Override
		public DefUnit getDefUnit() {
			return module;
		}
		
		@Override
		protected void checkNewParent() {
		}
	}
	
	public static class DeclarationModule extends ASTNode {
		
		public final Token[] comments;
		public final ArrayView<IToken> packageList;
		public final String[] packages; // Old API
		public final DefSymbol moduleName; 
		
		public DeclarationModule(Token[] comments, ArrayView<IToken> packageList, BaseLexElement moduleDefUnit) {
			this.comments = comments;
			this.packageList = assertNotNull(packageList);
			this.packages = RefModule.tokenArrayToStringArray(packageList);
			this.moduleName = new ModuleDefSymbol(moduleDefUnit.getSourceValue());
			this.moduleName.setSourceRange(moduleDefUnit.getSourceRange());
			this.moduleName.setParsedStatus();
			parentize(moduleName);
		}
		
		public ModuleDefSymbol getModuleSymbol() {
			return (ModuleDefSymbol) moduleName;
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DECLARATION_MODULE;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			//TreeVisitor.acceptChildren(visitor, packages);
			acceptVisitor(visitor, moduleName);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append("module ");
			cp.appendTokenList(packageList, ".", true);
			cp.append(moduleName.name);
			cp.append(";");
		}
		
		@Override
		public String toStringAsElement() {
			ASTCodePrinter cp = new ASTCodePrinter();
			cp.appendTokenList(packageList, ".", true);
			cp.append(moduleName.name);
			return cp.toString();
		}
	}
	
	public static Module createModuleNoModuleDecl(String moduleName, ArrayView<ASTNode> members) {
		ModuleDefSymbol defSymbol = new ModuleDefSymbol(moduleName);
		return new Module(defSymbol, null, members);
	}
	
	public final DeclarationModule md;
	public final ArrayView<ASTNode> members;
	
	public Module(ModuleDefSymbol defSymbol, DeclarationModule md, ArrayView<ASTNode> members) {
		super(defSymbol, false);
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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, md);
		acceptVisitor(visitor, members);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public Token[] getDocComments() {
		if(md != null) {
			return md.comments;
		}
		return null;
	}
	
	@Override
	public IScope getMembersScope(IModuleResolver moduleResolver) {
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
	public Iterator<? extends ASTNode> getMembersIterator(IModuleResolver moduleResolver) {
		return members.iterator();
	}
	
	public String getFullyQualifiedName() {
		ASTCodePrinter cp = new ASTCodePrinter();
		if(md != null) {
			cp.appendTokenList(md.packageList, ".", true);
		}
		cp.append(getName());
		return cp.toString();
	}
	
	public String[] getDeclaredPackages() {
		if(md != null) {
			return md.packages;
		}
		return NewUtils.EMPTY_STRING_ARRAY;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(md, cp.ST_SEP);
		cp.appendList(members, cp.ST_SEP);
	}
	
	@Override
	public String toStringAsElement() {
		if(md == null) {
			return getName();
		}
		return md.toStringAsElement();
	}
	
	@Override
	public String getExtendedName() {
		return getName();
	}
	
}