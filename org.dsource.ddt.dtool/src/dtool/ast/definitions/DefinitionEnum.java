package dtool.ast.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class DefinitionEnum extends Definition implements IScopeNode, IStatement {

	public final ArrayView<EnumMember> members;
	public final Reference type;
	
	public DefinitionEnum(DefUnitDataTuple defunitInfo, PROT prot, EnumMember[] members, Reference reference,
			SourceRange sourceRange) {
		super(defunitInfo, prot);
		this.members = new ArrayView<EnumMember>(members); parentize(this.members);
		this.type = reference; parentize(this.type);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);

	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Enum;
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
	@Override
	public Iterator<EnumMember> getMembersIterator() {
		return members.iterator();
	}

	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}

}
