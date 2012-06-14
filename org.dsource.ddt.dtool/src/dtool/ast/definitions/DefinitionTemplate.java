package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * Note, ATM only valid as a statement in the shorthand syntax for an eponymous template, like class(T) { ...
 */
public class DefinitionTemplate extends Definition implements IScopeNode, IStatement {
	
	public final ArrayView<TemplateParameter> templateParams; 
	public final ArrayView<ASTNeoNode> decls;
	public final boolean wrapper;
	
	public DefinitionTemplate(DefUnitDataTuple dudt, PROT prot, ArrayView<ASTNeoNode> decls,
			ArrayView<TemplateParameter> params, boolean wrapper) {
		super(dudt, prot);
		this.templateParams = parentize(params);
		this.decls = parentize(decls);
		this.wrapper = wrapper;
		if(wrapper) {
			assertTrue(this.decls.size() == 1);
			assertTrue(decls.get(0) instanceof DefUnit || decls.get(0) instanceof DefinitionCtor);
			// BUG here, need to fix for DefinitionCtor case
		}
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
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
			IScopeNode scope = ((DefUnit)decls.get(0)).getMembersScope(moduleResolver);
			Iterator<? extends IASTNeoNode> tplIter = templateParams.iterator();
			return ChainedIterator.create(tplIter, scope.getMembersIterator(moduleResolver));
		}
		return ChainedIterator.create(templateParams.iterator(), decls.iterator());
	}
	
}