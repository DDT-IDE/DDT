package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class TypePointer extends CommonRefNative {
	
	public static ASTNeoNode convertTypePointer(descent.internal.compiler.parser.TypePointer elem
			, ASTConversionContext convContext) {
		if(elem.next instanceof descent.internal.compiler.parser.TypeFunction) {
			ASTNeoNode node= new TypeFunction((descent.internal.compiler.parser.TypeFunction)elem.next, convContext);
			node.setSourceRange(elem);
			return node;
		}
		else
			return new TypePointer(elem, convContext);
	}


	public Reference elemtype;
	
	private TypePointer(descent.internal.compiler.parser.TypePointer elem
			, ASTConversionContext convContext) {
		setSourceRange(elem);
		this.elemtype = ReferenceConverter.convertType(elem.next, convContext);
	}
	
	public TypePointer(Reference elemtype) {
		this.elemtype = elemtype;
		if (this.elemtype != null)
			this.elemtype.setParent(this);
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
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicPointer.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return elemtype.toStringAsElement() + "*";
	}

	
	public static class IntrinsicPointer extends NativeDefUnit {
		public IntrinsicPointer() {
			super("<pointer>");
		}
		
		public static final IntrinsicPointer instance = new IntrinsicPointer();


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