package dtool.ast.util;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;

public class NamedElementUtil {
	
	/**
	 * Return a name identifying this defUnit in the projects source code.
	 * It's similar to a fully qualified name, but has some more information on the name about
	 * the containing defunits.
	 * (the name is not enough to uniquely locate a defUnit in a project. That's the goal anyways)
	 */
	public static String getElementTypedQualification(INamedElement namedElement) {
		switch(namedElement.getArcheType()) {
		case Package:
			return namedElement.getFullyQualifiedName() + "/";
		default:
		}
		return getElementTypeQualificationBase(namedElement);
	}
	
	public static String getElementTypeQualificationBase(INamedElement namedElement) {
		if(namedElement.getArcheType() == EArcheType.Module) {
			return namedElement.getModuleFullyQualifiedName() + "/";
		}
		
		if(namedElement.isLanguageIntrinsic()) { 
			return NATIVES_ROOT + namedElement.getName();
		}
		
		INamedElement parentNamespace = namedElement.getParentElement();
		assertNotNull(parentNamespace);
		String sep = parentNamespace.getArcheType() == EArcheType.Module  ? "" : ".";
		String parentQualifedName = getElementTypeQualificationBase(parentNamespace);
		String qualification = parentQualifedName + sep;
		return qualification + namedElement.getName();
	}
	
	public static String NATIVES_ROOT = "/";
	
}
