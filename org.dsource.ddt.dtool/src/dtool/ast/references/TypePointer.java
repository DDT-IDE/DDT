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

public class TypePointer extends CommonRefNative {
	
	public final Reference elemType;
	
	public TypePointer(Reference elemType, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.elemType = parentize(elemType);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemType);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicPointer.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return elemType.toStringAsElement() + "*";
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
		public Iterator<ASTNeoNode> getMembersIterator() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
}