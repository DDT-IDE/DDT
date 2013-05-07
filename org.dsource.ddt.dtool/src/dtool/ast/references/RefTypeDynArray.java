package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class RefTypeDynArray extends CommonRefNative {
	
	public final Reference elemtype;
	
	public RefTypeDynArray(Reference elemType) {
		this.elemtype = parentize(elemType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPE_DYN_ARRAY;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemtype);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(elemtype, "[]");
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicDynArray.instance);
	}
	
	
	public static class IntrinsicDynArray extends NativeDefUnit {
		public IntrinsicDynArray() {
			super("<dynamic-array>");
		}
		
		public static final IntrinsicDynArray instance = new IntrinsicDynArray();
		
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
		public Iterator<ASTNode> getMembersIterator(IModuleResolver moduleResolver) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
}