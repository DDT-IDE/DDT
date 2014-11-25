package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.search.SourceModuleFinder;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.definitions.EArcheType;
import dtool.util.NewUtils;

/**
 * This class manages how to do mapping between definition nodes and ModelElements. 
 *
 */
public class DeeModelEngine {
	
	public static IMember findCorrespondingModelElement(INamedElement targetDefUnit, IScriptProject scriptProject)
		throws ModelException {
		if(targetDefUnit == null) 
			return null;
		
		ModuleFullName moduleFullName = targetDefUnit.getModuleFullName();
		// TODO: would be nice to have test for module == null path
		if(moduleFullName == null) {
			return null;
		}
		
		ISourceModule targetSrcModule = SourceModuleFinder.findModuleUnit(scriptProject, moduleFullName); 
		// TODO: would be nice to have test for targetSrcModule == null path
		// TODO consider out of buildpath scenario
		if(targetSrcModule != null) {
			return findCorrespondingModelElement(targetDefUnit, targetSrcModule);
		}
		return null;
	}
	
	public static IMember findCorrespondingModelElement(INamedElement defUnit, ISourceModule sourceModule)
			throws ModelException {
		return searchForModelElement(defUnit, sourceModule, false);
	}
	
	public static IMember searchForModelElement(INamedElement defUnit, ISourceModule sourceModule, 
			boolean returnNonExisting)
			throws ModelException {
		assertNotNull(sourceModule);
		
		INamedElement parentDefUnit = defUnit.getParentElement();
		
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
					INamedElementNode otherNode = defUnit.resolveUnderlyingNode();
					SourceRange otherNodeSR = otherNode == null ? null : otherNode.getNameSourceRangeOrNull();
					if(nameRange != null && otherNodeSR != null && nameRange.getOffset() == otherNodeSR.getOffset()) {
						return member; // We found a perfect match
					}
					bestMatch = member;
				}
			}
			
			if(bestMatch == null && returnNonExisting) {
				if(isTypeElement(defUnit)) {
					return parentElement.getType(defUnit.getName(), 1);
				} else if(isFieldElement(defUnit) && parentElement instanceof IType) {
					// TODO: specific test case for this /*BUG here make - need to tes this path. */
					return ((IType) parentElement).getField(defUnit.getName());
				} else if(isMethodElement(defUnit) && parentElement instanceof IType) {
					// TODO: specific test case for this /*BUG here make - need to tes this path. */
					return ((IType) parentElement).getMethod(defUnit.getName());
				} else {
					return parentElement.getType(defUnit.getName(), 1);
				}
			}
			return bestMatch;
		}
	}
	
	private static boolean isFieldElement(INamedElement defUnit) {
		EArcheType archeType = defUnit.getArcheType();
		return archeType == EArcheType.Variable || archeType == EArcheType.EnumMember; 
	}
	
	private static boolean isMethodElement(INamedElement defUnit) {
		return defUnit.getArcheType() == EArcheType.Function;
	}
	
	private static boolean isTypeElement(INamedElement defUnit) {
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
	public static String[] getQualification(final INamedElement defUnit) {
		String fqName = defUnit.getFullyQualifiedName();
		String qualification = StringUtil.segmentUntilLastMatch(fqName, ".");
		if(qualification == null) {
			return NewUtils.EMPTY_STRING_ARRAY;
		}
		return qualification.split("\\.");
	}
	
	public static String getPackageName(ISourceModule sourceModule) {
		return sourceModule.getParent().getElementName().replaceAll("/", ".");
	}
	
}