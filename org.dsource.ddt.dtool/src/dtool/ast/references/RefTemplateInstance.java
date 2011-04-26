package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTPrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;

public class RefTemplateInstance extends Reference {
	
	public final Reference refRawTemplate;
	public final ASTNeoNode[] tiargs;
	
	protected RefTemplateInstance(Reference refRawTemplate, ASTNeoNode[] tiargs, SourceRange sourceRange) {
		assertNotNull(refRawTemplate);
		assertNotNull(tiargs);
		this.refRawTemplate = refRawTemplate; 
		this.tiargs = tiargs;
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, refRawTemplate);
			TreeVisitor.acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public final boolean canMatch(DefUnit defunit) {
		return refRawTemplate.canMatch(defunit);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		// Not accurate, this will ignore the template parameters:
		return refRawTemplate.findTargetDefUnits(findOneOnly);
	}
	
	@Override
	public String toStringAsElement() {
		return refRawTemplate.toStringAsElement()  + "!" + ASTPrinter.toStringParamListAsElements(tiargs);
	}
	
}
