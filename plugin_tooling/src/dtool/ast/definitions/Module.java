/*******************************************************************************
 * Copyright (c) 2010, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import static dtool.util.NewUtils.assertCast;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.IModuleNode;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.scoping.IScopeNode;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.references.RefModule;
import dtool.parser.common.BaseLexElement;
import dtool.parser.common.IToken;
import dtool.parser.common.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.util.NewUtils;

/**
 * D Module. 
 * The top-level AST class, has no parent, is the first and main node of every compilation unit.
 */
public class Module extends DefUnit implements IScopeNode, IModuleNode, IConcreteNamedElement {
	
	public static class ModuleDefSymbol extends DefSymbol {
		
		protected Module module;
		
		public ModuleDefSymbol(String id) {
			super(id);
		}
		
		@Override
		protected ASTNode getParent_Concrete() {
			return assertCast(parent, DeclarationModule.class);
		}
		
		@Override
		public DefUnit getDefUnit() {
			return module;
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
		
	}
	
	public static Module createModuleNoModuleDecl(String moduleName, ArrayView<ASTNode> members,
			Path compilationUnitPath, SourceRange modRange) {
		ModuleDefSymbol defSymbol = new ModuleDefSymbol(moduleName);
		defSymbol.setSourceRange(modRange.getStartPos(), 0);
		return new Module(defSymbol, null, members, compilationUnitPath);
	}
	
	public final DeclarationModule md;
	public final ArrayView<ASTNode> members;
	public final Path compilationUnitPath; // can be null. This might be removed in the future.
	
	public Module(ModuleDefSymbol defSymbol, DeclarationModule md, ArrayView<ASTNode> members, 
			Path compilationUnitPath) {
		super(defSymbol, false);
		defSymbol.module = this;
		this.md = parentize(md);
		this.members = parentize(members);
		assertNotNull(members);
		this.compilationUnitPath = compilationUnitPath;
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
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(md, cp.ST_SEP);
		cp.appendList(members, cp.ST_SEP);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public String getFullyQualifiedName() {
		ASTCodePrinter cp = new ASTCodePrinter();
		if(md != null) {
			cp.appendTokenList(md.packageList, ".", true);
		}
		cp.append(getName());
		return cp.toString();
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return getFullyQualifiedName();
	}
	
	public String[] getDeclaredPackages() {
		if(md != null) {
			return md.packages;
		}
		return NewUtils.EMPTY_STRING_ARRAY;
	}
	
	@Override
	public Path getCompilationUnitPath() {
		return compilationUnitPath;
	}
	
	@Override
	public Token[] getDocComments() {
		if(md != null) {
			return md.comments;
		}
		return null;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public INamedElementSemantics getSemantics() {
		return semantics;
	}
	
	protected final TypeSemantics semantics = new TypeSemantics(this) {
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			ReferenceResolver.resolveSearchInScope(search, Module.this);
		}
		
	};
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, members, false);
	}
	
}