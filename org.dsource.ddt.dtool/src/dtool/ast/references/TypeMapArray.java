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
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TypeMapArray extends CommonRefNative {
	
	public final Reference keyType;
	public final Reference valueType;
	
	public TypeMapArray(Reference keyType, Reference valueType, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.keyType = parentize(keyType);
		this.valueType = parentize(valueType);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keyType);
			TreeVisitor.acceptChildren(visitor, valueType);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicMapArray.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return valueType.toStringAsElement() + "["+keyType.toStringAsElement()+"]";
	}
	
	public static class IntrinsicMapArray extends NativeDefUnit {
		public IntrinsicMapArray() {
			super("<map-array>");
		}
		
		public static final IntrinsicMapArray instance = new IntrinsicMapArray();
		
		
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