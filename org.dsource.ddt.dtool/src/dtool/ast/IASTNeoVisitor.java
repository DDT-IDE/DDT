package dtool.ast;

import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
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
	public boolean visit(Symbol elem);

	public boolean visit(DefUnit elem);
	public void endVisit(DefUnit node);
	
	public boolean visit(Module elem);
	public void endVisit(Module node);

	public boolean visit(DefinitionAggregate elem);
	public void endVisit(DefinitionAggregate node);
	//--
	public boolean visit(DefinitionClass elem);
	public void endVisit(DefinitionClass node);
	
	
	public boolean visit(DefinitionTemplate elem);
	public void endVisit(DefinitionTemplate node);
	
	
	public boolean visit(DefinitionVariable elem);
	public void endVisit(DefinitionVariable elem);
		
	public boolean visit(DefinitionEnum elem);
	public void endVisit(DefinitionEnum elem);
		
	public boolean visit(DefinitionTypedef elem);
	public void endVisit(DefinitionTypedef elem);
		
	public boolean visit(DefinitionAlias elem);
	public void endVisit(DefinitionAlias elem);
		
	public boolean visit(DefinitionFunction elem);
	public void endVisit(DefinitionFunction elem);

	/* ---------------------------------- */

	public boolean visit(Resolvable elem);
	public void endVisit(Resolvable node);

	public boolean visit(Reference elem);
	public void endVisit(Reference node);
	
	public boolean visit(CommonRefNative elem);

	public boolean visit(NamedReference elem);
	
	public boolean visit(CommonRefQualified elem);
	
	public boolean visit(RefIdentifier elem);

	public boolean visit(RefTemplateInstance elem);

	/* ---------------------------------- */
	public boolean visit(DeclarationImport elem);
	public void endVisit(DeclarationImport elem);
	
	public boolean visit(DeclarationInvariant elem);
	public void endVisit(DeclarationInvariant elem);

	
	public boolean visit(DeclarationUnitTest elem);
	public void endVisit(DeclarationUnitTest elem);
	
	public boolean visit(DeclarationConditional elem);
	public void endVisit(DeclarationConditional elem);
	
}