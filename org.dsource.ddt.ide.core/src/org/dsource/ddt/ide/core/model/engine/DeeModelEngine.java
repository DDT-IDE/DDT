package org.dsource.ddt.ide.core.model.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Arrays;
import java.util.LinkedList;

import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.NodeUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;

/**
 * This class manages how to do mapping between definition nodes and ModelElements. 
 *
 */
public class DeeModelEngine {
	
	public static IMember findCorrespondingModelElement(DefUnit defUnit, ISourceModule sourceModule)
			throws ModelException {
		return searchForModelElement(defUnit, sourceModule, false);
	}
	
	public static IMember searchForModelElement(DefUnit defUnit, ISourceModule sourceModule, boolean returnNonExisting)
			throws ModelException {
		assertNotNull(sourceModule);
		
		DefUnit parentDefUnit = NodeUtil.getOuterDefUnit(defUnit);
		
		if(parentDefUnit == null) {
			return sourceModule.getType(defUnit.getName());
		} else {
			IMember parentElement = searchForModelElement(parentDefUnit, sourceModule, returnNonExisting);
			if(parentElement == null) {
				return null;
			}
			IMember bestMatch = null;
			
			if(parentElement.exists()) {
				final IModelElement[] children = (IModelElement[]) parentElement.getChildren();
				for (int i = 0; i < children.length; i++) {
					IModelElement modelElement = children[i];
					if(!modelElement.getElementName().equals(defUnit.getName()))
						continue;
					
					switch (modelElement.getElementType()) {
					case IModelElement.FIELD:
						if(!isFieldElement(defUnit)) continue;
						break;
					case IModelElement.METHOD:
						if(!isMethodElement(defUnit)) continue;
						break;
					case IModelElement.TYPE:
						if(!isTypeElement(defUnit)) continue;
						break;
					default:
						assertFail();
					}
					
					IMember member = (IMember) modelElement;
					ISourceRange nameRange = member.getNameRange();
					if(nameRange != null && nameRange.getOffset() == defUnit.defname.getStartPos()) {
						return member; // We found a perfect match
					}
					bestMatch = member;
				}
			}
			
			if(bestMatch == null && returnNonExisting) {
				if(isTypeElement(defUnit)) {
					return parentElement.getType(defUnit.getName(), 1);
				} else if(isFieldElement(defUnit) && parentElement instanceof IMethod) {
					// TODO: specific test case for this /*BUG here*/
					return ((IType) parentElement).getField(defUnit.getName());
				} else if(isMethodElement(defUnit) && parentElement instanceof IMethod) {
					// TODO: specific test case for this /*BUG here*/
					return ((IType) parentElement).getMethod(defUnit.getName());
				} else {
					return parentElement.getType(defUnit.getName(), 1);
				}
			}
			return bestMatch;
		}
	}
	
	private static boolean isFieldElement(DefUnit defUnit) {
		return defUnit instanceof DefinitionVariable;
	}
	
	private static boolean isMethodElement(DefUnit defUnit) {
		return defUnit instanceof DefinitionFunction /*|| defUnit instanceof DefinitionCtor*/;
	}
	
	private static boolean isTypeElement(DefUnit defUnit) {
		return !isFieldElement(defUnit) && !isMethodElement(defUnit);
	}
	
	public static IMember findMember(IMember parent, int elementKind, String name, int occurrenceCount) 
			throws ModelException {
		IModelElement[] children = parent.getChildren();
		
		int occurrenceIx = 0;
		for (int i = 0; i < children.length; i++) {
			IModelElement modelElement = children[i];
			if(!modelElement.getElementName().equals(name))
				continue;
			
			if(modelElement.getElementType() != elementKind) {
				continue;
			}
			
			occurrenceIx++;
			
			if(occurrenceIx == occurrenceCount) {
				return (IMember) modelElement;
			}
		}
		return null;
	}
	
	/**
	 * Returns the fully qualified name for given defUnit.
	 * TODO think more about the naming of local elements 
	 */
	public static String[] getQualification(final DefUnit defUnit) {
		LinkedList<String> qualification = getQualificationList(defUnit);
		return ArrayUtil.createFrom(qualification, String.class);
	}
	
	public static LinkedList<String> getQualificationList(final DefUnit defUnit) {
		LinkedList<String> qualications = new LinkedList<String>();
		
		DefUnit defUnitIter = defUnit;
		
		while(true) {
			DefUnit parentDefUnit = NodeUtil.getOuterDefUnit(defUnitIter);
			
			if(parentDefUnit == null) {
				if((defUnitIter instanceof Module)) {
					Module module = ((Module) defUnitIter);
					
					String[] packageNames = module.getDeclaredPackages();
					qualications.addAll(0, Arrays.asList(packageNames));
				}
				
				return qualications;
			} else {
				qualications.add(0, parentDefUnit.getName());
				defUnitIter = parentDefUnit;
			}
		}
	}
	
	public static String getPackageName(ISourceModule sourceModule) {
		return sourceModule.getParent().getElementName().replaceAll("/", ".");
	}
	
}