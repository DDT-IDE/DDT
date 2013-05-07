package dtool.ast;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.tree.IVisitable;

/**
 * Uses a Visitor to collect a node's children.
 */
public class ASTChildrenCollector extends ASTHomogenousVisitor {
	
	private boolean visitingParent = true;
	private List<ASTNode> childrenLst;
	
	public static ASTNode[] getChildrenArray(ASTNode elem){
		return ArrayUtil.createFrom(getChildrenList(elem), ASTNode.class);
	}
	
	public static List<ASTNode> getChildrenList(IVisitable<? super IASTVisitor> elem){
		ASTChildrenCollector collector = new ASTChildrenCollector();
		collector.childrenLst = new ArrayList<ASTNode>();
		elem.accept(collector);
		return collector.childrenLst;
	}
	
	
	@Override
	public boolean preVisit(ASTNode node) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}
		
		// visiting children
		childrenLst.add(node);
		return false;
	}
	
	@Override
	public void postVisit(ASTNode node) {
		// Do nothing
	}
}
