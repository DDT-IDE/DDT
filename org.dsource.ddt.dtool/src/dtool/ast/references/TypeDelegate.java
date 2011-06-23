package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.ArrayView;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.NativeDefUnit;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A delegate type;
 */
public class TypeDelegate extends CommonRefNative {

	public Reference rettype;
	public ArrayView<IFunctionParameter> params;
	public int varargs;
	
	public TypeDelegate(descent.internal.compiler.parser.TypeDelegate elem, ASTConversionContext convContext) {
		setSourceRange(elem);
		this.rettype = (Reference) DescentASTConverter.convertElem(elem.rto, convContext);
		TypeFunction typeFunction = ((TypeFunction) elem.next);
		this.varargs = DefinitionFunction.convertVarArgs(typeFunction.varargs);
		this.params = DescentASTConverter.convertManyToView(typeFunction.parameters, IFunctionParameter.class, convContext); 
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
