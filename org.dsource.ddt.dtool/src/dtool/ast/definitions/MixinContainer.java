package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public class MixinContainer extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public final RefTemplateInstance type;
	
	public MixinContainer(RefTemplateInstance typeref, SourceRange neoSourceRange) {
		initSourceRange(neoSourceRange);
		this.type = parentize(typeref);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		return IteratorUtil.getEMPTY_ITERATOR();
		// TODO: mixin container
		/*
		DefUnit defunit = type.findTargetDefUnit();
		if(defunit == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		return (Iterator) defunit.getMembersScope().getMembersIterator();
		 */
	}
}