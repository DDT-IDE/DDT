package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
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
 * A function pointer type
 */
public class TypeFunction extends CommonRefNative {
	
	public Reference rettype;
	public List<IFunctionParameter> params;
	public int varargs;
	public LINK linkage;

	public TypeFunction(descent.internal.compiler.parser.TypeFunction elem
			, ASTConversionContext convContext) {
		setSourceRange(elem);
		this.rettype = (Reference) DescentASTConverter.convertElem(elem.next, convContext);
		this.params = DescentASTConverter.convertManyL(elem.parameters, this.params, convContext);
		this.varargs = DefinitionFunction.convertVarArgs(elem.varargs);
		this.linkage = elem.linkage;
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
		return DefUnitSearch.wrapResult(IntrinsicFunction.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return Reference.maybeNullReference(rettype).toStringAsElement() 
		+ " function"  
		+ DefinitionFunction.toStringParametersForSignature(params, varargs);
	}

	
	public static class IntrinsicFunction extends NativeDefUnit {
		public IntrinsicFunction() {
			super("<funtion>");
		}

		public static final IntrinsicFunction instance = new IntrinsicFunction();

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