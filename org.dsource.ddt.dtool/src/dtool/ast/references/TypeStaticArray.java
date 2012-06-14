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
import dtool.ast.expressions.Resolvable;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TypeStaticArray extends CommonRefNative {
	
	public final Reference elemType;
	public final Resolvable sizeExp;
	
	public TypeStaticArray(Reference elemType, Resolvable sizeExp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.elemType = parentize(elemType);
		this.sizeExp = parentize(sizeExp);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemType);
			TreeVisitor.acceptChildren(visitor, sizeExp);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicStaticArray.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return elemType.toStringAsElement() + "["+sizeExp.toStringAsElement()+"]";
	}
	
	public static class IntrinsicStaticArray extends NativeDefUnit {
		public IntrinsicStaticArray() {
			super("<static-array>");
		}
		
		public static final IntrinsicStaticArray instance = new IntrinsicStaticArray();
		
		
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