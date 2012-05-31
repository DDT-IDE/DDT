package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public class EnumContainer extends ASTNeoNode implements IDeclaration, INonScopedBlock {
	
	public final ArrayView<EnumMember> members;
	public final Reference type;
	
	
	public EnumContainer(EnumMember[] members, Reference type, SourceRange sourceRange) {
		this.members = new ArrayView<EnumMember>(members);
		if (this.members != null) {
			for (EnumMember em : members) {
				em.setParent(this);
			}
		}

		this.type = type;
		if (type != null)
			type.setParent(this);

		initSourceRange(sourceRange);
	}

	public EnumContainer(ArrayView<EnumMember> members, Reference type, SourceRange sourceRange) {
		this.members = members;
		this.type = type;
		initSourceRange(sourceRange);
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

	@Override
	public void setAttributes(int effectiveModifiers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAttributes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setProtection(PROT prot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PROT getEffectiveProtection() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
