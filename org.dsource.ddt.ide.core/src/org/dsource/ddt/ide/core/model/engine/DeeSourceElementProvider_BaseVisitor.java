package org.dsource.ddt.ide.core.model.engine;


import dtool.ast.ASTNeoAbstractVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationBasicAttrib;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralNewAnonClass;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.CommonRefNative;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;

public abstract class DeeSourceElementProvider_BaseVisitor extends ASTNeoAbstractVisitor {
	
	@Override
	public final boolean preVisit(ASTNeoNode elem) {
		return true;
	}
	
	@Override
	public final void postVisit(ASTNeoNode elem) {
	}
	
	@Override
	public final boolean visit(ASTNeoNode elem) {
		return true; // Default implementation: do nothing, visit children
	}
	
	@Override
	public final boolean visit(Symbol elem) {
		return true;
	}
	
	@Override
	public final boolean visit(DefUnit elem) {
		return true;
	}
	
	@Override public boolean visit(DeclarationModule node) { return true; }
	
	@Override public boolean visit(DeclarationImport elem) { return false; } // BUG here: possibly??
	@Override public boolean visit(ImportContent node) { return true; }
	@Override public boolean visit(ImportSelective node) { return true; }
	@Override public boolean visit(ImportAlias node) { return true; }
	@Override public boolean visit(ImportSelectiveAlias node) { return true; }
	
	@Override public boolean visit(DeclarationEmpty node) { return true; }
	
	//-- Various Declarations
	@Override public boolean visit(DeclarationLinkage node) { return true; }
	@Override public boolean visit(DeclarationAlign node) { return true; }
	@Override public boolean visit(DeclarationPragma node) { return true; }
	@Override public boolean visit(DeclarationProtection node) { return true; }
	@Override public boolean visit(DeclarationBasicAttrib node) { return true; }
	
	/* ---------------------------------- */
	
	@Override
	public final boolean visit(Resolvable elem) {
		return true;
	}
	
	@Override
	public final boolean visit(Reference elem) {
		return true;
	}
	
	@Override
	public final boolean visit(CommonRefNative elem) {
		return true;
	}
	
	
	@Override
	public final boolean visit(CommonRefQualified elem) {
		return visit((NamedReference) elem); // Go up the hierarchy
	}
	
	@Override
	public final boolean visit(RefIdentifier elem) {
		return visit((NamedReference) elem); // Go up the hierarchy
	}
	
	@Override
	public final boolean visit(RefTemplateInstance elem) {
		return true;
	}
	
	
	/* ---------------------------------- */
	
	@Override
	public boolean visit(DeclarationMixinString node) { return true; }

	
	@Override
	public final boolean visit(DeclarationInvariant elem) {
		return true;
	}
	
	@Override
	public final boolean visit(DeclarationUnitTest elem) {
		return true;
	}
	@Override
	public final boolean visit(DeclarationConditional elem) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpLiteralFunc elem) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpLiteralNewAnonClass elem) {
		return true;
	}
	
}