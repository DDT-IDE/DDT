package dtool.ast;

import dtool.ast.declarations.DeclarationImport;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Definition;
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
	boolean preVisit(ASTNeoNode node);
	void postVisit(ASTNeoNode node);
	
	boolean visit(ASTNeoNode node);
	void endVisit(ASTNeoNode node);

	/* ---------------------------------- */
	boolean visit(Symbol elem);

	boolean visit(DefUnit elem);
	void endVisit(DefUnit node);
	
	boolean visit(Module elem);
	void endVisit(Module node);

	boolean visit(Definition elem);
	void endVisit(Definition node);

	boolean visit(DefinitionAggregate elem);
	void endVisit(DefinitionAggregate node);

	boolean visit(DefinitionTemplate elem);
	void endVisit(DefinitionTemplate node);

	boolean visit(DefinitionClass elem);
	void endVisit(DefinitionClass node);
	
	
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

	boolean visit(Resolvable elem);
	void endVisit(Resolvable node);

	boolean visit(Reference elem);
	void endVisit(Reference node);
	
	boolean visit(CommonRefNative elem);

	boolean visit(NamedReference elem);
	
	boolean visit(CommonRefQualified elem);
	
	boolean visit(RefIdentifier elem);

	boolean visit(RefTemplateInstance elem);

	/* ---------------------------------- */
	boolean visit(DeclarationImport elem);

}