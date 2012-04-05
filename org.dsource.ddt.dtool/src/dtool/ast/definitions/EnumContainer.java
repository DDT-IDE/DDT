package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.refmodel.INonScopedBlock;

public class EnumContainer extends ASTNeoNode implements IDeclaration, INonScopedBlock {
	
	public final ArrayView<EnumMember> members;
	public final Reference type;
	
	public EnumContainer(EnumMember[] members, Reference type, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.members = new ArrayView<EnumMember>(members); parentize(this.members);
		this.type = type; parentize(this.type);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
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
