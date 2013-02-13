package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

public class RefTemplateInstance extends Reference {
	
	public final Reference refRawTemplate;
	public final ArrayView<ASTNeoNode> tiargs; //TODO: redo API for args
	
	public RefTemplateInstance(Reference refRawTemplate, ArrayView<ASTNeoNode> tiargs, SourceRange sourceRange) {
		assertNotNull(refRawTemplate);
		assertNotNull(tiargs);
		initSourceRange(sourceRange);
		this.refRawTemplate = parentize(refRawTemplate);
		this.tiargs = parentize(tiargs);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, refRawTemplate);
			TreeVisitor.acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public final boolean canMatch(DefUnitDescriptor defunit) {
		return refRawTemplate.canMatch(defunit);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		// Not accurate, this will ignore the template parameters:
		return refRawTemplate.findTargetDefUnits(moduleResolver, findOneOnly);
	}
	
	@Override
	public String toStringAsElement() {
		ASTCodePrinter cp = new ASTCodePrinter();
		cp.append(refRawTemplate.toStringAsElement(), "!(");
		cp.appendNodesAsElements(tiargs, ", ");
		cp.append(")");
		return cp.toString();
	}
	
}