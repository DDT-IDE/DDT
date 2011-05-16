package dtool.ast;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.tree.IVisitable;



/**
 * Uses a Visitor to collect a node's children.
 */
public class ASTNeoChildrenCollector extends ASTNeoHomogenousVisitor {
	
	private boolean visitingParent = true;
	private List<ASTNeoNode> childrenLst;
	
	public static ASTNeoNode[] getChildrenArray(ASTNeoNode elem){
		return getChildrenList(elem).toArray(ASTNeoNode.NO_ELEMENTS);
	}
	
	public static List<ASTNeoNode> getChildrenList(IVisitable<? super IASTNeoVisitor> elem){
		ASTNeoChildrenCollector collector = new ASTNeoChildrenCollector();
		collector.childrenLst = new ArrayList<ASTNeoNode>();
		elem.accept(collector);
		return collector.childrenLst;
	}
	
	
	@Override
	public boolean preVisit(ASTNeoNode node) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}
		
		// visiting children
		childrenLst.add(node);
		return false;
	}
	
	@Override
	public void postVisit(ASTNeoNode node) {
		// Do nothing
	}
}
