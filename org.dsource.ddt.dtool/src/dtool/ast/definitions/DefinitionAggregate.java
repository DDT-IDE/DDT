package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTPrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScopeNode;

/**
 * A definition of a aggregate. 
 */
public abstract class DefinitionAggregate extends Definition implements IScopeNode, IStatement {
	
	public TemplateParameter[] templateParams; 
	public final ArrayView<ASTNeoNode> members; // can be null. (bodyless aggregates)
	
	public DefinitionAggregate(DefUnitDataTuple defunit, PROT prot, ArrayView<ASTNeoNode> members) {
		super(defunit, prot);
		this.members = members;
	}
	
	protected void acceptNodeChildren(IASTNeoVisitor visitor, boolean children) {
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, members);
		}
	}
	
	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(members == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		return members.iterator();
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	@Override
	public String toStringForHoverSignature() {
		String str = getModuleScope().toStringAsElement() +"."+ getName()
				+ ASTPrinter.toStringParamListAsElements(templateParams);
		return str;
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
}
