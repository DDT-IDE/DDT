package dtool.ast.references;

import java.util.Collection;

import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;

/**
 * Common class for entity references.
 */
public abstract class Reference extends Resolvable {
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	public static IDefUnitReference maybeNullReference(Reference ref) {
		if(ref != null)
			return ref;
		return NativeDefUnit.nullReference;
	}
	
	public static class InvalidSyntaxReference extends Reference {
		
		@Override
		public String toStringAsElement() {
			return "<InvalidSyntax>";
		}
		
		@Override
		public boolean canMatch(DefUnitDescriptor defunit) {
			return false;
		}
		
		@Override
		public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
			return null;
		}
		
		@Override
		public void accept0(IASTNeoVisitor visitor) {
			visitor.visit(this);
			visitor.endVisit(this);	 			
		}
		
	}
	
	public EReferenceConstraint refConstraint = null;
	
	public IScopeNode getTargetScope() {
		DefUnit defunit = findTargetDefUnit(); 
		if(defunit == null)
			return null;
		return defunit.getMembersScope();
	}
	
	/*public void performSearch(CommonDefUnitSearch search) {
		Collection<DefUnit> defunits = findLookupDefUnits();
		ANeoResolve.doSearchInDefUnits(defunits, search);
	}
	
	public abstract Collection<DefUnit> findLookupDefUnits();
	*/
	
	@Override
	public abstract String toStringAsElement();
	
	public abstract boolean canMatch(DefUnitDescriptor defunit);
	
}

