package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.Iterator;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.IASTNode;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

/**
 * A definition of a aggregate. 
 */
public abstract class DefinitionAggregate extends CommonDefinition implements IScopeNode, IStatement {
	
	public interface IAggregateBody extends IASTNode {
	}
	
	public final ArrayView<TemplateParameter> tplParams;
	public final Expression tplConstraint;
	public final IAggregateBody aggrBody;
	
	public DefinitionAggregate(Token[] comments, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, IAggregateBody aggrBody) {
		super(comments, defId);
		this.tplParams = parentize(tplParams);
		this.tplConstraint = parentize(tplConstraint);
		this.aggrBody = parentizeI(aggrBody);
	}
	
	protected void acceptNodeChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, tplParams);
		acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, aggrBody);
	}
	
	public void aggregateToStringAsCode(ASTCodePrinter cp, String keyword, boolean printDecls) {
		cp.append(keyword);
		cp.append(defname, " ");
		cp.appendList("(", tplParams, ",", ") ");
		DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		if(printDecls) {
			cp.append(aggrBody, "\n");
		}
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return this;
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator(IModuleResolver moduleResolver) {
		if(aggrBody instanceof DeclarationEmpty) {
			assertFail();
			return null; /*NPE BUG here*/
		} else {
			return NewUtils.getChainedIterator(((DeclBlock) aggrBody).nodes /*NPE BUG here*/, tplParams);
		}
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
}