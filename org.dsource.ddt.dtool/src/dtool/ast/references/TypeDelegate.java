package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A delegate type;
 */
public class TypeDelegate extends CommonRefNative {
	
	public final Reference retType;
	public final ArrayView<IFunctionParameter> params;
	public final int varargs;
	
	public TypeDelegate(Reference retType, ArrayView<IFunctionParameter> params, int varargs, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.retType = parentize(retType);
		this.varargs = varargs;
		this.params = parentizeI(params);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, retType);
			TreeVisitor.acceptChildren(visitor, params);
		}
		visitor.endVisit(this);
	}
	
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicDelegate.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return Reference.maybeNullReference(retType).toStringAsElement() 
				+ " delegate"  
				+ DefinitionFunction.toStringParametersForSignature(params, varargs);
	}
	
	public static class IntrinsicDelegate extends NativeDefUnit {
		public IntrinsicDelegate() {
			super("<delegate>");
		}
		
		public static final IntrinsicDelegate instance = new IntrinsicDelegate();
		
		
		@Override
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Iterator<ASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
}