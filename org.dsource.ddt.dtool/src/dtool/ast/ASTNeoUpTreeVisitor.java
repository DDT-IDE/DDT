package dtool.ast;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.tree.TreeVisitor;
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
 * This visitor has default implementation methods where each defers to the method with the parameter's superclass.
 * This is a cute idea, but it can be a performance issue. 
 */
public abstract class ASTNeoUpTreeVisitor extends TreeVisitor implements IASTNeoVisitor {

	@Override
	public boolean preVisit(ASTNeoNode elem) {
		return true; // Default implementation: do nothing
	}
	
	@Override
	public void postVisit(ASTNeoNode elem) {
		// Default implementation: do nothing
	}
	
	@Override
	public boolean visit(ASTNeoNode elem) {
		// Default implementation: do nothing
		return true;
	}
	
	@Override
	public void endVisit(ASTNeoNode elem) {
		return;
	}


	@Override
	public boolean visit(Symbol elem) {
		Assert.isTrue(Symbol.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
	}
	
	@Override
	public boolean visit(DefUnit elem) {
		Assert.isTrue(DefUnit.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
	}
	@Override
	public void endVisit(DefUnit elem) {
		Assert.isTrue(DefUnit.class.getSuperclass().equals(ASTNeoNode.class));
		endVisit((ASTNeoNode) elem);
	}

	@Override
	public boolean visit(Module elem) {
		Assert.isTrue(Module.class.getSuperclass().equals(DefUnit.class));
		return visit((DefUnit) elem);
	}
	@Override
	public void endVisit(Module elem) {
		Assert.isTrue(Module.class.getSuperclass().equals(DefUnit.class));
		endVisit((DefUnit) elem);
	}
	
	@Override
	public boolean visit(Definition elem) {
		Assert.isTrue(Definition.class.getSuperclass().equals(DefUnit.class));
		return visit((DefUnit) elem);
	}
	@Override
	public void endVisit(Definition elem) {
		Assert.isTrue(Definition.class.getSuperclass().equals(DefUnit.class));
		endVisit((DefUnit) elem);
	}	
	
	@Override
	public boolean visit(DefinitionAggregate elem) {
		Assert.isTrue(DefinitionAggregate.class.getSuperclass().equals(Definition.class));
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionAggregate elem) {
		Assert.isTrue(DefinitionAggregate.class.getSuperclass().equals(Definition.class));
		endVisit((Definition) elem);
	}
	
	@Override
	public boolean visit(DefinitionTemplate elem) {
		Assert.isTrue(DefinitionTemplate.class.getSuperclass().equals(Definition.class));
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionTemplate elem) {
		Assert.isTrue(DefinitionTemplate.class.getSuperclass().equals(Definition.class));
		endVisit((Definition) elem);
	}		
	
	@Override
	public boolean visit(DefinitionClass elem) {
		Assert.isTrue(DefinitionClass.class.getSuperclass().equals(DefinitionAggregate.class));
		return visit((DefinitionAggregate) elem);
	}
	@Override
	public void endVisit(DefinitionClass elem) {
		Assert.isTrue(DefinitionClass.class.getSuperclass().equals(DefinitionAggregate.class));
		endVisit((DefinitionAggregate) elem);
	}	
	
	
	@Override
	public boolean visit(DefinitionVariable elem) {
		Assert.isTrue(DefinitionVariable.class.getSuperclass().equals(Definition.class));
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionVariable elem) {
		Assert.isTrue(DefinitionVariable.class.getSuperclass().equals(Definition.class));
		endVisit((Definition) elem);
	}	
	
	@Override
	public boolean visit(DefinitionEnum elem) {
		Assert.isTrue(DefinitionEnum.class.getSuperclass().equals(Definition.class));
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionEnum elem) {
		Assert.isTrue(DefinitionEnum.class.getSuperclass().equals(Definition.class));
		endVisit((Definition) elem);
	}	
	
	@Override
	public boolean visit(DefinitionTypedef elem) {
		Assert.isTrue(DefinitionTypedef.class.getSuperclass().equals(Definition.class));
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionTypedef elem) {
		Assert.isTrue(DefinitionTypedef.class.getSuperclass().equals(Definition.class));
		endVisit((Definition) elem);
	}	
	
	@Override
	public boolean visit(DefinitionAlias elem) {
		Assert.isTrue(DefinitionAlias.class.getSuperclass().equals(Definition.class));
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionAlias elem) {
		Assert.isTrue(DefinitionAlias.class.getSuperclass().equals(Definition.class));
		endVisit((Definition) elem);
	}	
	
	@Override
	public boolean visit(DefinitionFunction elem) {
		Assert.isTrue(DefinitionFunction.class.getSuperclass().equals(Definition.class));
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionFunction elem) {
		Assert.isTrue(DefinitionFunction.class.getSuperclass().equals(Definition.class));
		endVisit((Definition) elem);
	}	
	/* ---------------------------------- */

	@Override
	public boolean visit(Resolvable elem) {
		Assert.isTrue(Resolvable.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
	}
	@Override
	public void endVisit(Resolvable elem) {
		Assert.isTrue(Resolvable.class.getSuperclass().equals(ASTNeoNode.class));
		endVisit((ASTNeoNode) elem);
	}


	@Override
	public boolean visit(Reference elem) {
		Assert.isTrue(Reference.class.getSuperclass().equals(Resolvable.class));
		return visit((Resolvable) elem);
	}
	@Override
	public void endVisit(Reference elem) {
		Assert.isTrue(Reference.class.getSuperclass().equals(Resolvable.class));
		endVisit((Resolvable) elem);
	}
	
	@Override
	public boolean visit(CommonRefNative elem) {
		Assert.isTrue(CommonRefNative.class.getSuperclass().equals(Reference.class));
		return visit((Reference) elem);
	}
	
	
	@Override
	public boolean visit(NamedReference elem) {
		Assert.isTrue(NamedReference.class.getSuperclass().equals(Reference.class));
		return visit((Reference) elem);
	}

/*	public void endVisit(NamedReference elem) {
		Assert.isTrue(NamedReference.class.getSuperclass().equals(Reference.class));
		endVisit((Reference) elem);
	}
*/
	
	@Override
	public boolean visit(CommonRefQualified elem) {
		Assert.isTrue(CommonRefQualified.class.getSuperclass().equals(NamedReference.class));
		return visit((NamedReference) elem);
	}

	@Override
	public boolean visit(RefIdentifier elem) {
		Assert.isTrue(RefIdentifier.class.getSuperclass() == NamedReference.class);
		return visit((NamedReference) elem);
	}

	@Override
	public boolean visit(RefTemplateInstance elem) {
		Assert.isTrue(RefTemplateInstance.class.getSuperclass() == Reference.class);
		return visit((Reference) elem);
	}


	/* ---------------------------------- */

	@Override
	public boolean visit(DeclarationImport elem) {
		Assert.isTrue(DeclarationImport.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
	}
	
	
	/* ============================================= */

}
