package org.dsource.ddt.ide.core.model.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.refmodel.NodeUtil;

public class DeeModelEngine {
	
	public static IMember findCorrespondingModelElement(DefUnit defUnit, ISourceModule sourceModule)
			throws ModelException {
		DefUnit parentDefUnit = NodeUtil.getOuterDefUnit(defUnit);
		
		if(parentDefUnit == null) {
			return sourceModule.getType(defUnit.getName());
		} else {
			IMember parentElement = findCorrespondingModelElement(parentDefUnit, sourceModule);
			if(parentElement == null) {
				return null;
			}
			final IModelElement[] children = (IModelElement[]) parentElement.getChildren();
			
			IMember bestMatch = null;
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
	
	public static IMember findMember(IMember parent, int elementKind, String name, int occurrenceCount) throws ModelException {
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
	
//	private static void asdfdffdfd(DefUnit defUnit, IMember parentElement, IMember bestMatch) {
//		if(bestMatch == null) {
//			// If no matches, return
//			
//			switch(defUnit.getArcheType()) {
//			case Variable:
//				if(parentElement instanceof IType) {
//					((IType) parentElement).getField(defUnit.getName());
//				}
//				break;
//			case Function:
//				if(parentElement instanceof IType) {
//					((IType) parentElement).getMethod(defUnit.getName());
//				}
//				break;
//			default:
//				parentElement.getType(defUnit.getName(), 1);
//			}
//			// Bug above
//			return parentElement.getType(defUnit.getName(), 1);
//		}
//	}
	
}