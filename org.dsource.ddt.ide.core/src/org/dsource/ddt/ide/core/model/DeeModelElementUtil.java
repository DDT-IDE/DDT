package org.dsource.ddt.ide.core.model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;

import dtool.ast.definitions.EArcheType;

public class DeeModelElementUtil {
	
	public static EArcheType elementFlagsToArcheType(int elementFlags) {
		if((elementFlags & Modifiers.AccModule) != 0) {
			return EArcheType.Module;
		}
		if((elementFlags & Modifiers.AccInterface) != 0) {
			return EArcheType.Interface;
		}
		
		int archetypeFlag = elementFlags & DeeModelConstants.FLAGMASK_KIND;
		switch (archetypeFlag) {
		case DeeModelConstants.FLAG_KIND_FUNCTION:
			return EArcheType.Function;
		case DeeModelConstants.FLAG_KIND_VARIABLE:
			return EArcheType.Variable;
		case DeeModelConstants.FLAG_KIND_CLASS:
			return EArcheType.Class;
		case DeeModelConstants.FLAG_KIND_INTERFACE:
			return EArcheType.Interface;
		case DeeModelConstants.FLAG_KIND_STRUCT:
			return EArcheType.Struct;
		case DeeModelConstants.FLAG_KIND_UNION:
			return EArcheType.Union;
		case DeeModelConstants.FLAG_KIND_ENUM:
			return EArcheType.Enum;
		case DeeModelConstants.FLAG_KIND_TEMPLATE:
			return EArcheType.Template;
		case DeeModelConstants.FLAG_KIND_ALIAS:
			return EArcheType.Alias;
		case DeeModelConstants.FLAG_KIND_TYPEDEF:
			return EArcheType.Typedef;
		default:
			return null;
		}
	}
	
	public static boolean isConstructor(int elementFlags) {
		return (elementFlags & DeeModelConstants.FLAG_CONSTRUCTOR) != 0;
	}
	
	public static ProtectionAttribute elementFlagsToProtection(int elementFlags, ProtectionAttribute undefined) {
		if((elementFlags & DeeModelConstants.FLAG_ALT_PROTECTION) != 0) {
			if(Flags.isPrivate(elementFlags)) {
				return ProtectionAttribute.PACKAGE;
			} else if(Flags.isPublic(elementFlags)) {
				return ProtectionAttribute.EXPORT;
			}
			return undefined;
		}
		if(Flags.isPrivate(elementFlags)) {
			return ProtectionAttribute.PRIVATE;
		} else if(Flags.isProtected(elementFlags)) {
			return ProtectionAttribute.PROTECTED;
		} else if(Flags.isPublic(elementFlags)) {
			return ProtectionAttribute.PUBLIC;
		}
		return undefined;
	}
	
	public static int getCorrectedElementFlags(IMember member, int flags) {
		int elementType = member.getElementType();
		switch (elementType) {
		case IModelElement.FIELD:
			flags = (flags & ~DeeModelConstants.FLAGMASK_KIND) | DeeModelConstants.FLAG_KIND_VARIABLE; break;
		case IModelElement.METHOD:
			flags = (flags & ~DeeModelConstants.FLAGMASK_KIND) | DeeModelConstants.FLAG_KIND_FUNCTION; break;
		case IModelElement.TYPE:
			break;
		default:
			throw assertFail();
		}
		return flags;
	}
	
}
