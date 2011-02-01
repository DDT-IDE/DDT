package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class TypeDynArray extends CommonRefNative {
	public Reference elemtype;

	public TypeDynArray(TypeDArray elem, ASTConversionContext convContext) {
		setSourceRange(elem);
		this.elemtype = ReferenceConverter.convertType(elem.next, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemtype);
		}
		visitor.endVisit(this);
	}

	@Override
	public String toStringAsElement() {
		return elemtype.toStringAsElement() + "[]";
	}

	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicDynArray.instance);
	}
	
	
	public static class IntrinsicDynArray extends NativeDefUnit {
		public IntrinsicDynArray() {
			super("<dynamic-array>");
		}
		
		public static final IntrinsicDynArray instance = new IntrinsicDynArray();

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