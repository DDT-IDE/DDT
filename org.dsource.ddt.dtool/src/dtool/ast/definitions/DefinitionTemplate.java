package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList2;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * Note, ATM only valid as a statement in the shorthand syntax for an eponymous template, like class(T) { ...
 */
public class DefinitionTemplate extends Definition implements IScopeNode, IStatement {
	
	public final boolean isMixin;
	public final ArrayView<TemplateParameter> tplParams;
	public final Expression tplConstraint;
	public final NodeList2 decls;
	
	public final boolean wrapper;
	
	public DefinitionTemplate(boolean isMixin, DefUnitTuple dudt, ArrayView<TemplateParameter> tplParams, 
		Expression tplConstraint, NodeList2 decls) {
		super(dudt);
		this.isMixin = isMixin;
		this.tplParams = parentize(tplParams);
		this.tplConstraint = parentize(tplConstraint);
		this.decls = parentize(decls);
		
		this.wrapper = false; // TODO: determine this
		if(wrapper) {
			assertTrue(this.decls.nodes.size() == 1);
			assertTrue(decls.nodes.get(0) instanceof DefUnit || decls.nodes.get(0) instanceof DefinitionCtor);
			// BUG here, need to fix for DefinitionCtor case
		}
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_TEMPLATE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, tplParams);
			TreeVisitor.acceptChildren(visitor, tplConstraint);
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isMixin, "mixin ");
		cp.append("template ");
		cp.appendNode(defname);
		cp.appendNodeList("(", tplParams, ",", ") ");
		tplConstraintToStringAsCode(cp, tplConstraint);
		cp.appendNode("{\n", decls, "}");
	}
	
	public static void tplConstraintToStringAsCode(ASTCodePrinter cp, Expression tplConstraint) {
		if(tplConstraint instanceof MissingParenthesesExpression) {
			cp.appendNode("if", tplConstraint);
		} else {
			cp.appendNode("if(", tplConstraint, ")");
		}
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Template;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		// TODO: template super scope
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	@Override
	public Iterator<? extends IASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
		// TODO: check if in a template invocation
		// TODO: test this more, redo
		if(wrapper) {
			// Go straight to members of the inner decl
			IScopeNode scope = ((DefUnit)decls.nodes.get(0)).getMembersScope(moduleResolver);
			Iterator<? extends IASTNeoNode> tplIter = tplParams.iterator();
			return ChainedIterator.create(tplIter, scope.getMembersIterator(moduleResolver));
		}
		return ChainedIterator.create(tplParams.iterator(), decls.nodes.iterator());
	}
	
}