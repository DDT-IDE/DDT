package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;
import dtool.util.ArrayView;

public class EnumContainer extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public final ArrayView<EnumMember> members;
	public final Reference type;
	
	public EnumContainer(ArrayView<EnumMember> members, Reference type) {
		this.members = parentize(members);
		this.type = parentize(type);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);	
	}
	
	@Override
	public Iterator<EnumMember> getMembersIterator() {
		return members.iterator();
	}
	
}