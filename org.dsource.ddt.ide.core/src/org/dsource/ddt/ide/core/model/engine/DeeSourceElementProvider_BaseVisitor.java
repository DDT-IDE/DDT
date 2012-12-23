package org.dsource.ddt.ide.core.model.engine;


import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Symbol;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralNewAnonClass;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.CommonRefNative;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;

public abstract class DeeSourceElementProvider_BaseVisitor implements IASTNeoVisitor {
	
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
	public void endVisit(ASTNeoNode node) {
	}
	
	@Override
	public final boolean visit(Symbol elem) {
		return true;
	}
	
	@Override
	public final boolean visit(DefUnit elem) {
		return true;
	}
	@Override
	public void endVisit(DefUnit node) { }
	
	@Override
	public boolean visit(DeclarationModule node) {
		return true;
	}
	@Override
	public void endVisit(DeclarationModule node) { }
	
	@Override
	public boolean visit(ImportContent node) { return true; }
	@Override
	public boolean visit(ImportSelective node) { return true; }
	@Override
	public boolean visit(ImportAlias node) { return true; }
	@Override
	public boolean visit(ImportSelectiveAlias node) { return true; }
	
	/* ---------------------------------- */
	
	@Override
	public final boolean visit(Resolvable elem) {
		return true;
	}
	@Override
	public void endVisit(Resolvable node) {
	}
	
	@Override
	public final boolean visit(Reference elem) {
		return true;
	}
	@Override
	public void endVisit(Reference node) {
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
	public final boolean visit(DeclarationImport elem) {
		return false;
	}
	@Override
	public void endVisit(DeclarationImport elem) { }
	
	@Override
	public final boolean visit(DeclarationInvariant elem) {
		return true;
	}
	@Override
	public void endVisit(DeclarationInvariant elem) { }
	
	@Override
	public final boolean visit(DeclarationUnitTest elem) {
		return true;
	}
	@Override
	public void endVisit(DeclarationUnitTest elem) { }
	
	@Override
	public final boolean visit(DeclarationConditional elem) {
		return true;
	}
	@Override
	public void endVisit(DeclarationConditional elem) { }
	
	@Override
	public final boolean visit(ExpLiteralFunc elem) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpLiteralNewAnonClass elem) {
		return true;
	}
	
}