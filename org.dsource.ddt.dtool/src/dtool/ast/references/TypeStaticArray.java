package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class TypeStaticArray extends CommonRefNative {
	public Reference elemtype;
	public Resolvable sizeexp;

	public TypeStaticArray(TypeSArray elem, ASTConversionContext convContext) {
		setSourceRange(elem);
		this.elemtype = ReferenceConverter.convertType(elem.next, convContext);
		this.sizeexp = ExpressionConverter.convert(elem.dim, convContext); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemtype);
			TreeVisitor.acceptChildren(visitor, sizeexp);
		}
		visitor.endVisit(this);
	}

	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicStaticArray.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return elemtype.toStringAsElement() + "["+sizeexp.toStringAsElement()+"]";
	}

	
	public static class IntrinsicStaticArray extends NativeDefUnit {
		public IntrinsicStaticArray() {
			super("<static-array>");
		}
		
		public static final IntrinsicStaticArray instance = new IntrinsicStaticArray();


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