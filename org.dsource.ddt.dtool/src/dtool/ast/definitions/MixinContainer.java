package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NeoSourceRange;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public class MixinContainer extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public final RefTemplateInstance type;
	
	public MixinContainer(RefTemplateInstance typeref, NeoSourceRange neoSourceRange) {
		this.type = typeref;
		setSourceRange(neoSourceRange);
	}
	
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<ASTNode> getMembersIterator() {
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
