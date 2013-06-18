package org.dsource.ddt.ide.core.model.engine;


import dtool.ast.ASTAbstractVisitor;
import dtool.ast.ASTNode;
import dtool.ast.declarations.AttribAlign;
import dtool.ast.declarations.AttribBasic;
import dtool.ast.declarations.AbstractConditionalDeclaration;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.AttribLinkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.AttribPragma;
import dtool.ast.declarations.AttribProtection;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpArrayLength;
import dtool.ast.expressions.ExpConditional;
import dtool.ast.expressions.ExpFunctionLiteral;
import dtool.ast.expressions.ExpInfix;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralChar;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpNewAnonClass;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpNull;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.ast.expressions.ExpPrefix;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.RefTypeFunction;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.RefTypeof;
import dtool.ast.references.Reference;

public abstract class DeeSourceElementProvider_BaseVisitor extends ASTAbstractVisitor {
	
	@Override
	public final boolean preVisit(ASTNode elem) {
		return true;
	}
	
	@Override
	public final void postVisit(ASTNode elem) {
	}
	
	@Override
	public final boolean visit(ASTNode elem) {
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
	@Override public boolean visit(AttribLinkage node) { return true; }
	@Override public boolean visit(AttribAlign node) { return true; }
	@Override public boolean visit(AttribPragma node) { return true; }
	@Override public boolean visit(AttribProtection node) { return true; }
	@Override public boolean visit(AttribBasic node) { return true; }
	
	
	/* -----------------  Aggregates  ----------------- */
	
	@Override public boolean visit(DefVarFragment elem) { return true; }
	
	/* ---------------------------------- */
	
	@Override
	public final boolean visit(Resolvable elem) {
		return true;
	}
	
	@Override
	public final boolean visit(Reference elem) {
		return true;
	}
	
	public abstract boolean visit(NamedReference elem);
	
	@Override public boolean visit(RefIdentifier elem) { return visit((NamedReference) elem); }
	@Override public boolean visit(RefImportSelection elem) { return visit((NamedReference) elem); }
	@Override public boolean visit(RefModuleQualified elem) { return visit((NamedReference) elem); }
	@Override public boolean visit(RefQualified elem) { return visit((NamedReference) elem); }
	@Override public boolean visit(RefPrimitive elem) { return visit((NamedReference) elem); }
	@Override public boolean visit(RefModule elem) { return visit((NamedReference) elem); }
	
	@Override public boolean visit(RefTypeDynArray elem) { return true; }
	@Override public boolean visit(RefTypePointer elem) { return true; }
	@Override public boolean visit(RefTypeFunction elem) { return true; }
	@Override public boolean visit(RefIndexing elem) { return true; }
	
	@Override public final boolean visit(RefTypeof elem) { return true; }
	@Override public final boolean visit(RefTemplateInstance elem) { return true; }
	
	/* ---------------------------------- */
	
	@Override public boolean visit(ExpThis elem) { return true; }
	@Override public boolean visit(ExpSuper elem) { return true; }
	@Override public boolean visit(ExpNull elem) { return true; }
	@Override public boolean visit(ExpArrayLength elem) { return true; }
	@Override public boolean visit(ExpLiteralBool elem) { return true; }
	@Override public boolean visit(ExpLiteralInteger elem) { return true; }
	@Override public boolean visit(ExpLiteralString elem) { return true; }
	@Override public boolean visit(ExpLiteralFloat elem) { return true; }
	@Override public boolean visit(ExpLiteralChar elem) { return true; }
	
	@Override public boolean visit(ExpReference elem) { return true; }
	
	@Override public boolean visit(ExpPrefix elem) { return true; }
	@Override public boolean visit(ExpPostfixOperator elem) { return true; }
	@Override public boolean visit(ExpInfix elem) { return true; }
	@Override public boolean visit(ExpConditional elem) { return true; }
	
	@Override
	public final boolean visit(ExpFunctionLiteral elem) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpNewAnonClass elem) {
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
	public final boolean visit(AbstractConditionalDeclaration elem) {
		return true;
	}
	
}