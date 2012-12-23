package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

/**
 * A definition of a aggregate. 
 */
public abstract class DefinitionAggregate extends Definition implements IScopeNode, IStatement {
	
	public final ArrayView<TemplateParameter> templateParams; 
	public final ArrayView<ASTNeoNode> members; // can be null. (bodyless aggregates)
	
	public DefinitionAggregate(DefUnitTuple defunit, PROT prot, ArrayView<TemplateParameter> templateParams,
			ArrayView<ASTNeoNode> members) {
		super(defunit, prot);
		this.templateParams = parentize(templateParams);
		this.members = parentize(members);
	}
	
	protected void acceptNodeChildren(IASTNeoVisitor visitor, boolean children) {
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, members);
		}
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return this;
	}
	
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
		return NewUtils.getChainedIterator(members, templateParams); 
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	@Override
	public String toStringForHoverSignature() {
		ASTCodePrinter cp = new ASTCodePrinter();
		cp.append(getModuleScope().toStringAsElement(), ".", getName());
		cp.append(ASTCodePrinter.toStringParamListAsElements(templateParams));
		return cp.toString();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
}