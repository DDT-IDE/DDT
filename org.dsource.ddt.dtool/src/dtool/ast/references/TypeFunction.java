package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.LINK;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
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
 * A function pointer type
 */
public class TypeFunction extends CommonRefNative {
	
	public final Reference retType;
	public final ArrayView<IFunctionParameter> params;
	public final int varargs;
	public final LINK linkage;
	
	public TypeFunction(Reference retType, ArrayView<IFunctionParameter> params, int varArgs, LINK linkage,
			SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.retType = parentize(retType);
		this.params = parentizeI(params);
		this.varargs = varArgs;
		this.linkage = linkage;
	}
	
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, retType);
			TreeVisitor.acceptChildren(visitor, params);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicFunction.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return Reference.maybeNullReference(retType).toStringAsElement() 
				+ " function"  
				+ DefinitionFunction.toStringParametersForSignature(params, varargs);
	}
	
	
	public static class IntrinsicFunction extends NativeDefUnit {
		public IntrinsicFunction() {
			super("<funtion>");
		}
		
		public static final IntrinsicFunction instance = new IntrinsicFunction();
		
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