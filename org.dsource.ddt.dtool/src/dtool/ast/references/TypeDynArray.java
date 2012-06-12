package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class TypeDynArray extends CommonRefNative {
	
	public final Reference elemtype;
	
	public TypeDynArray(Reference elemType, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.elemtype = parentize(elemType);
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
		public Iterator<ASTNeoNode> getMembersIterator() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
}