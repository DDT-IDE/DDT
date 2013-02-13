package dtool.ast.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

public class DefinitionEnum extends Definition implements IScopeNode, IStatement {
	
	public final ArrayView<EnumMember> members;
	public final Reference type;
	
	public DefinitionEnum(DefUnitTuple defunitInfo, PROT prot, ArrayView<EnumMember> members, Reference reference) {
		super(defunitInfo, prot);
		this.members = parentize(members);
		this.type = parentize(reference);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
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
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
	@Override
	public Iterator<EnumMember> getMembersIterator(IModuleResolver moduleResolver) {
		return members.iterator();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
}