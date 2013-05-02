package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

public class DefinitionCtor extends ASTNeoNode implements IScopeNode, ICallableElement {
	
	public final ArrayView<IFunctionParameter> params;
	public final int varargs;
	public final IStatement fbody;
	public final int nameStart;
	
	public DefinitionCtor(ArrayView<IFunctionParameter> params, int varargs,
			IStatement fbody, int thisStart) {
		assertNotNull(params);
		this.params = parentizeI(params);
		this.varargs = varargs;
		this.fbody = parentizeI(fbody);
		this.nameStart = thisStart;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, fbody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public ArrayView<IFunctionParameter> getParameters() {
		return params;
	}
	
	@Override
	public Iterator<IFunctionParameter> getMembersIterator(IModuleResolver moduleResolver) {
		return params.iterator();
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
}