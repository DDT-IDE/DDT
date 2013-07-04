package org.dsource.ddt.ide.core.model;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.Flags;

import dtool.ast.declarations.AttribProtection.EProtection;
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
		case DeeModelConstants.FLAG_KIND_STRUCT: return EArcheType.Struct;
		case DeeModelConstants.FLAG_KIND_UNION: return EArcheType.Union;
		case DeeModelConstants.FLAG_KIND_CLASS: return EArcheType.Class;
		case DeeModelConstants.FLAG_KIND_INTERFACE: return EArcheType.Interface;
		case DeeModelConstants.FLAG_KIND_TEMPLATE: return EArcheType.Template;
		case DeeModelConstants.FLAG_KIND_MIXIN: return EArcheType.Mixin;
		case DeeModelConstants.FLAG_KIND_ENUM: return EArcheType.Enum;
		case DeeModelConstants.FLAG_KIND_ALIAS: return EArcheType.Alias;
		case DeeModelConstants.FLAG_KIND_FUNCTION: return EArcheType.Function;
		case DeeModelConstants.FLAG_KIND_CONSTRUCTOR: return EArcheType.Constructor;
		case DeeModelConstants.FLAG_KIND_VARIABLE: return EArcheType.Variable;
		default:
			return null;
		}
	}
	
	public static boolean isConstructor(int elementFlags) {
		return elementFlagsToArcheType(elementFlags) == EArcheType.Constructor;
	}
	
	public static EProtection elementFlagsToProtection(int elementFlags, EProtection undefined) {
		if((elementFlags & DeeModelConstants.FLAG_ALT_PROTECTION) != 0) {
			if(Flags.isPrivate(elementFlags)) {
				return EProtection.PACKAGE;
			} else if(Flags.isPublic(elementFlags)) {
				return EProtection.EXPORT;
			}
			return undefined;
		}
		if(Flags.isPrivate(elementFlags)) {
			return EProtection.PRIVATE;
		} else if(Flags.isProtected(elementFlags)) {
			return EProtection.PROTECTED;
		} else if(Flags.isPublic(elementFlags)) {
			return EProtection.PUBLIC;
		}
		return undefined;
	}
	
}