package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.util.ArrayView;

/**
 * A delegate type;
 */
public class TypeDelegate extends CommonRefNative {
	
	public final Reference rettype;
	public final ArrayView<IFunctionParameter> params;
	public final int varargs;
	
	public TypeDelegate(Reference rettype, ArrayView<IFunctionParameter> params, int varargs, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.rettype = rettype; parentize(this.rettype);
		this.varargs = varargs;
		this.params = params; parentizeI(this.params);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, params);
		}
		visitor.endVisit(this);
	}
	
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicDelegate.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return Reference.maybeNullReference(rettype).toStringAsElement() 
				+ " delegate"  
				+ DefinitionFunction.toStringParametersForSignature(params, varargs);
	}
	
	public static class IntrinsicDelegate extends NativeDefUnit {
		public IntrinsicDelegate() {
			super("<delegate>");
		}
		
		public static final IntrinsicDelegate instance = new IntrinsicDelegate();
		
		
		@Override
		public IScopeNode getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public List<IScope> getSuperScopes() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Iterator<? extends ASTNode> getMembersIterator() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
