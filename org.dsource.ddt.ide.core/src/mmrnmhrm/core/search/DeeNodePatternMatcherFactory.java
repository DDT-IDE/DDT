package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.utilbox.misc.ReflectionUtils;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.IUsesReflectionToAccessInternalAPI;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.internal.core.search.matching.FieldPattern;
import org.eclipse.dltk.internal.core.search.matching.MethodDeclarationPattern;
import org.eclipse.dltk.internal.core.search.matching.MethodPattern;
import org.eclipse.dltk.internal.core.search.matching.OrPattern;
import org.eclipse.dltk.internal.core.search.matching.QualifiedTypeDeclarationPattern;
import org.eclipse.dltk.internal.core.search.matching.TypeReferencePattern;
import org.eclipse.dltk.internal.core.search.matching.VariablePattern;

@SuppressWarnings("restriction")
/**
 * BM: This is a separate class pretty much so that we can ignore warnings.
 */
public class DeeNodePatternMatcherFactory implements IUsesReflectionToAccessInternalAPI{
	
	public static AbstractNodePatternMatcher createPatternMatcher(DeeMatchLocator deeMatchLocator, SearchPattern pattern) {
		
		if (DeeDefPatternLocator.GLOBAL_param_defunit != null) {
			DeeDefPatternLocator defMatcher = new DeeDefPatternLocator(deeMatchLocator);
			DeeDefPatternLocator.GLOBAL_param_defunit = null;
			return defMatcher;
		}
		
		boolean findDeclarations;
		
		if (pattern.focus != null) {
			findDeclarations = true;
			boolean findReferences = false;
			
			switch (pattern.kind) {
			case IIndexConstants.TYPE_REF_PATTERN:
				findDeclarations = false;
				findReferences = true;
				break;
			case IIndexConstants.FIELD_PATTERN:
			case IIndexConstants.LOCAL_VAR_PATTERN:
				findDeclarations = ((VariablePattern) pattern).findDeclarations;
				findReferences = ((VariablePattern) pattern).findReferences;
				break;
			case IIndexConstants.METHOD_PATTERN:
				findDeclarations = ((MethodPattern) pattern).findDeclarations;
				findReferences = ((MethodPattern) pattern).findReferences;
				break;
			case IIndexConstants.TYPE_DECL_PATTERN:
			case IIndexConstants.METHOD_DECL_PATTERN:
				break;
			case IIndexConstants.OR_PATTERN:
				findDeclarations = true;
				findReferences = true;
				break;
			default: 
				assertFail();
			}
			return new DeeFocusedNodeMatcher(deeMatchLocator, pattern.focus, findDeclarations, findReferences);
		}
		
		if(pattern.kind == IIndexConstants.OR_PATTERN) {
			OrPattern orPattern = (OrPattern) pattern;
			// Lets assume the first pattern has all information we need
			pattern = orPattern.getPatterns()[0];
		}
		
		switch (pattern.kind) {
		case IIndexConstants.TYPE_REF_PATTERN: {
			TypeReferencePattern typeRefPattern = (TypeReferencePattern) pattern;
			char[] simpleName = typeRefPattern.simpleName;
			char[] qualification = readInternalField(typeRefPattern, "qualification", null);
			return new DeeNameNodeMatcher(deeMatchLocator, pattern, false, simpleName, qualification);
		}
		case IIndexConstants.TYPE_DECL_PATTERN: {
			if (pattern instanceof QualifiedTypeDeclarationPattern) {
				QualifiedTypeDeclarationPattern qualTypeDeclPatter = (QualifiedTypeDeclarationPattern) pattern;
				
				return new DeeNameNodeMatcher(deeMatchLocator, pattern, true,
						qualTypeDeclPatter.simpleName, qualTypeDeclPatter.qualification);
			}
			//TypeDeclarationPattern only occurs if it is a focus element... at least as of current DLTK 3.0 version
//			TypeDeclarationPattern typeDeclPatter = (TypeDeclarationPattern) pattern;
			assertFail();
		}
		case IIndexConstants.FIELD_PATTERN: {
			FieldPattern fieldPattern = (FieldPattern) pattern;
			char[] simpleName = fieldPattern.name;
			// XXX: ugly hack here
			char[] qualification = readInternalField(fieldPattern, "declaringQualification", null);
			return new DeeNameNodeMatcher(deeMatchLocator, pattern,
					fieldPattern.findDeclarations, simpleName, qualification);
		}
//		case IIndexConstants.LOCAL_VAR_PATTERN: {
//			LocalVariablePattern varPattern = (LocalVariablePattern) pattern;
//			return new DeeNodePatternMatcher2Extension(deeMatchLocator, pattern, varPattern.name, null, null);
//		}
		case IIndexConstants.METHOD_PATTERN: {
			MethodPattern methodPattern = (MethodPattern) pattern;
			char[] simpleName = methodPattern.selector;
			char[] qualification = CharOperation.concat(methodPattern.declaringQualificationName, 
					methodPattern.declaringSimpleName, '$'); 
			return new DeeNameNodeMatcher(deeMatchLocator, pattern, methodPattern.findDeclarations,
					simpleName, qualification);
		}
		case IIndexConstants.METHOD_DECL_PATTERN: {
			MethodDeclarationPattern methodPattern = (MethodDeclarationPattern) pattern;
			char[] simpleName = methodPattern.simpleName;
			char[] qualification = null;
			if(methodPattern.enclosingTypeNames != null) {
				qualification = CharOperation.concatWith(methodPattern.enclosingTypeNames, '$') ;
			}
			return new DeeNameNodeMatcher(deeMatchLocator, pattern, true, 
					simpleName, qualification);
		}
		default:
			return null;
		}
	}
	
	protected static char[] readInternalField(Object obj, String fieldName, char[] defaultValue) {
		try {
			Object value = ReflectionUtils.readField(obj, fieldName);
			if (value == null) {
				return null;
			} else if (value instanceof char[]) {
				return (char[]) value;
			} else if (value instanceof String) {
				return ((String) value).toCharArray();
			} else {
				DeeCore.logError("DLTK API error using reflection, field changed type");
			}
		} catch (NoSuchFieldException e) {
			DeeCore.logError("DLTK API error using reflection", e);
		}
		return defaultValue;
	}
	
}
