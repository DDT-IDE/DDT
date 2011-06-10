package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationUnitTest;
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
public abstract class ASTNeoUpTreeVisitor implements IASTNeoVisitor {
	
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
	
	static { assertTrue(Symbol.class.getSuperclass().equals(ASTNeoNode.class)); }
	@Override
	public boolean visit(Symbol elem) {
		return visit((ASTNeoNode) elem);
	}
	
	static { assertTrue(DefUnit.class.getSuperclass().equals(ASTNeoNode.class)); }
	@Override
	public boolean visit(DefUnit elem) {
		return visit((ASTNeoNode) elem);
	}
	@Override
	public void endVisit(DefUnit elem) {
		endVisit((ASTNeoNode) elem);
	}
	
	static { assertTrue(Module.class.getSuperclass().equals(DefUnit.class)); }
	@Override
	public boolean visit(Module elem) {
		return visit((DefUnit) elem);
	}
	@Override
	public void endVisit(Module elem) {
		endVisit((DefUnit) elem);
	}
	
	static { assertTrue(Definition.class.getSuperclass().equals(DefUnit.class)); }
	public boolean visit(Definition elem) {
		return visit((DefUnit) elem);
	}
	
	public void endVisit(Definition elem) {
		endVisit((DefUnit) elem);
	}	
	
	static { assertTrue(DefinitionAggregate.class.getSuperclass().equals(Definition.class)); }
	@Override
	public boolean visit(DefinitionAggregate elem) {
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionAggregate elem) {
		endVisit((Definition) elem);
	}
	
	static { assertTrue(DefinitionTemplate.class.getSuperclass().equals(Definition.class)); }
	@Override
	public boolean visit(DefinitionTemplate elem) {
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionTemplate elem) {
		endVisit((Definition) elem);
	}		
	
	static { assertTrue(DefinitionClass.class.getSuperclass().equals(DefinitionAggregate.class)); }
	@Override
	public boolean visit(DefinitionClass elem) {
		return visit((DefinitionAggregate) elem);
	}
	@Override
	public void endVisit(DefinitionClass elem) {
		endVisit((DefinitionAggregate) elem);
	}	
	
	
	static { assertTrue(DefinitionVariable.class.getSuperclass().equals(Definition.class)); }
	@Override
	public boolean visit(DefinitionVariable elem) {
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionVariable elem) {
		endVisit((Definition) elem);
	}	
	
	static { assertTrue(DefinitionEnum.class.getSuperclass().equals(Definition.class)); }
	@Override
	public boolean visit(DefinitionEnum elem) {
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionEnum elem) {
		endVisit((Definition) elem);
	}	
	
	static { assertTrue(DefinitionTypedef.class.getSuperclass().equals(Definition.class)); }
	@Override
	public boolean visit(DefinitionTypedef elem) {
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionTypedef elem) {
		endVisit((Definition) elem);
	}	
	
	static { assertTrue(DefinitionAlias.class.getSuperclass().equals(Definition.class)); }
	@Override
	public boolean visit(DefinitionAlias elem) {
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionAlias elem) {
		endVisit((Definition) elem);
	}	
	
	static { assertTrue(DefinitionFunction.class.getSuperclass().equals(Definition.class)); }
	@Override
	public boolean visit(DefinitionFunction elem) {
		return visit((Definition) elem);
	}
	@Override
	public void endVisit(DefinitionFunction elem) {
		endVisit((Definition) elem);
	}	
	/* ---------------------------------- */
	
	static { assertTrue(Resolvable.class.getSuperclass().equals(ASTNeoNode.class)); }
	@Override
	public boolean visit(Resolvable elem) {
		return visit((ASTNeoNode) elem);
	}
	@Override
	public void endVisit(Resolvable elem) {
		endVisit((ASTNeoNode) elem);
	}
	
	
	static { assertTrue(Reference.class.getSuperclass().equals(Resolvable.class)); }
	@Override
	public boolean visit(Reference elem) {
		return visit((Resolvable) elem);
	}
	@Override
	public void endVisit(Reference elem) {
		endVisit((Resolvable) elem);
	}
	
	static { assertTrue(CommonRefNative.class.getSuperclass().equals(Reference.class)); }
	@Override
	public boolean visit(CommonRefNative elem) {
		return visit((Reference) elem);
	}
	
	
	static { assertTrue(NamedReference.class.getSuperclass().equals(Reference.class)); }
	@Override
	public boolean visit(NamedReference elem) {
		return visit((Reference) elem);
	}
	
	static { assertTrue(CommonRefQualified.class.getSuperclass().equals(NamedReference.class)); }
	@Override
	public boolean visit(CommonRefQualified elem) {
		return visit((NamedReference) elem);
	}
	
	static { assertTrue(RefIdentifier.class.getSuperclass() == NamedReference.class); }
	@Override
	public boolean visit(RefIdentifier elem) {
		return visit((NamedReference) elem);
	}
	
	static { assertTrue(RefTemplateInstance.class.getSuperclass() == Reference.class); }
	@Override
	public boolean visit(RefTemplateInstance elem) {
		return visit((Reference) elem);
	}
	
	
	/* ---------------------------------- */
	
	static { assertTrue(DeclarationImport.class.getSuperclass().equals(ASTNeoNode.class)); }
	@Override
	public boolean visit(DeclarationImport elem) {
		return visit((ASTNeoNode) elem);
	}
	@Override
	public void endVisit(DeclarationImport elem) {
		endVisit((ASTNeoNode) elem);
	}
	
	static { assertTrue(DeclarationInvariant.class.getSuperclass().equals(ASTNeoNode.class)); }
	@Override
	public boolean visit(DeclarationInvariant elem) {
		return visit((ASTNeoNode) elem);
	}
	@Override
	public void endVisit(DeclarationInvariant elem) {
		endVisit((ASTNeoNode) elem);
	}
	
	static { assertTrue(DeclarationUnitTest.class.getSuperclass().equals(ASTNeoNode.class)); }
	@Override
	public boolean visit(DeclarationUnitTest elem) {
		return visit((ASTNeoNode) elem);
	}
	@Override
	public void endVisit(DeclarationUnitTest elem) {
		endVisit((ASTNeoNode) elem);
	}
	
	static { assertTrue(DeclarationConditional.class.getSuperclass().equals(ASTNeoNode.class)); }
	@Override
	public boolean visit(DeclarationConditional elem) {
		return visit((ASTNeoNode) elem);
	}
	@Override
	public void endVisit(DeclarationConditional elem) {
		endVisit((ASTNeoNode) elem);
	}
	
	/* ============================================= */
	
}
