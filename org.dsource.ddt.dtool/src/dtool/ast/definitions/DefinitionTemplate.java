package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DeclList;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * Definition of a template.
 * http://dlang.org/template.html#TemplateDeclaration
 * 
 * (Technically not allowed as statement, but parse so anyways.)
 */
public class DefinitionTemplate extends Definition implements IScopeNode, IDeclaration, IStatement {
	
	public final boolean isMixin;
	public final ArrayView<TemplateParameter> tplParams;
	public final Expression tplConstraint;
	public final DeclList decls;
	
	public final boolean wrapper;
	
	public DefinitionTemplate(boolean isMixin, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams, 
		Expression tplConstraint, DeclList decls) {
		super(defId);
		this.isMixin = isMixin;
		this.tplParams = parentize(tplParams);
		this.tplConstraint = parentize(tplConstraint);
		this.decls = parentize(decls);
		
		this.wrapper = false; // TODO: determine this
		if(wrapper) {
			assertTrue(this.decls.nodes.size() == 1);
			assertTrue(decls.nodes.get(0) instanceof DefUnit);
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
		cp.append(defname, " ");
		cp.appendList("(", tplParams, ",", ") ");
		tplConstraintToStringAsCode(cp, tplConstraint);
		cp.append("{\n", decls, "}");
	}
	
	public static void tplConstraintToStringAsCode(ASTCodePrinter cp, Expression tplConstraint) {
		if(tplConstraint instanceof MissingParenthesesExpression) {
			cp.append("if", tplConstraint);
		} else {
			cp.append("if(", tplConstraint, ")");
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
		// BUG in accessing decls
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