package mmrnmhrm.tests;

import static dtool.util.NewUtils.assertCast;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.ModelException;

@Deprecated
public class ModelElementTestUtils {
	
	public static ArrayList<IMember> getChildren(IParent element, String childName) throws ModelException {
		assertCast(null, IMember.class);
		ArrayList<IMember> matchedChildren = new ArrayList<IMember>();
		
		for (IModelElement child : element.getChildren()) {
			IMember member = assertCast(child, IMember.class);
			if(child.getElementName().equals(childName)) {
				matchedChildren.add(member);
			}
		}
		return matchedChildren;
	}
	
	public static IMember getChild(IParent element, String childName) throws ModelException {
		ArrayList<IMember> children = getChildren(element, childName);
		if(children.isEmpty()) {
			return null;
		}
		assertTrue(children.size() == 1);
		return children.get(0);
	}
	
}
