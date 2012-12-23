package dtool.ast;

import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionCtor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
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

/**
 * The classes that this visitor dispatches to are mostly abstract classes, not concrete ones: 
 * it doesn't dispatch only to the leaves of the AST hierarchy. This is because it is not necessary to do so, 
 * at the moment.
 */
public interface IASTNeoVisitor {
	
	/** Returns whether to proceed with type-specific dispatch visit. 
	 * Note: if false, it implies children will not be visited. */
	public boolean preVisit(ASTNeoNode node);
	public void postVisit(ASTNeoNode node);
	
	public boolean visit(ASTNeoNode node);
	public void endVisit(ASTNeoNode node);
	
	/* ---------------------------------- */
	public boolean visit(Symbol node);
	
	public boolean visit(DefUnit node);
	public void endVisit(DefUnit node);
	
	public boolean visit(Module node);
	public void endVisit(Module node);
	
	public boolean visit(DeclarationModule node);
	public void endVisit(DeclarationModule node);
	
	public boolean visit(DeclarationImport node);
	public void endVisit(DeclarationImport node);
	
	public boolean visit(ImportContent node);
	public boolean visit(ImportAlias node);
	public boolean visit(ImportSelective node);
	public boolean visit(ImportSelectiveAlias node);
	
	//-- Aggregates
	public boolean visit(DefinitionStruct node);
	public void endVisit(DefinitionStruct node);
	
	public boolean visit(DefinitionUnion node);
	public void endVisit(DefinitionUnion node);
	
	public boolean visit(DefinitionClass node);
	public void endVisit(DefinitionClass node);
	
	public boolean visit(DefinitionInterface node);
	public void endVisit(DefinitionInterface node);
	//--
	
	
	public boolean visit(DefinitionTemplate node);
	public void endVisit(DefinitionTemplate node);
	
	
	public boolean visit(DefinitionVariable node);
	public void endVisit(DefinitionVariable node);
	
	public boolean visit(DefinitionEnum node);
	public void endVisit(DefinitionEnum node);
	
	public boolean visit(DefinitionTypedef node);
	public void endVisit(DefinitionTypedef node);
	
	public boolean visit(DefinitionAlias node);
	public void endVisit(DefinitionAlias node);
	
	public boolean visit(DefinitionFunction node);
	public void endVisit(DefinitionFunction node);
	
	//--
	public boolean visit(DefinitionCtor node);
	public void endVisit(DefinitionCtor node);
	
	
	/* ---------------------------------- */
	
	public boolean visit(Resolvable node);
	public void endVisit(Resolvable node);
	
	public boolean visit(Reference node);
	public void endVisit(Reference node);
	
	public boolean visit(CommonRefNative node);
	
	public boolean visit(NamedReference node);
	
	public boolean visit(CommonRefQualified node);
	
	public boolean visit(RefIdentifier node);
	
	public boolean visit(RefTemplateInstance node);
	
	/* ---------------------------------- */
	
	public boolean visit(DeclarationInvariant node);
	public void endVisit(DeclarationInvariant node);
	
	
	public boolean visit(DeclarationUnitTest node);
	public void endVisit(DeclarationUnitTest node);
	
	public boolean visit(DeclarationConditional node);
	public void endVisit(DeclarationConditional node);
	
	public boolean visit(ExpLiteralFunc node);
	public boolean visit(ExpLiteralNewAnonClass node);
	
}