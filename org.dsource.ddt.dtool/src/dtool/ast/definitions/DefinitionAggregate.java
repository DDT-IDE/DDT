package dtool.ast.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.ASTPrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScopeNode;

/**
 * A definition of a aggregate. 
 */
public abstract class DefinitionAggregate extends Definition implements IScopeNode, IStatement {

	public TemplateParameter[] templateParams; 
	public List<ASTNode> members; // can be null. (bodyless aggregates)

	public DefinitionAggregate(Dsymbol elem, ASTConversionContext convContext) {
		super(elem, convContext);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
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
	public Iterator<ASTNode> getMembersIterator() {
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
		String str = getArcheType().toString() 
		+ "  " + getModuleScope().toStringAsElement() +"."+ getName()
		+ ASTPrinter.toStringParamListAsElements(templateParams);
		return str;
	}

	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
}
