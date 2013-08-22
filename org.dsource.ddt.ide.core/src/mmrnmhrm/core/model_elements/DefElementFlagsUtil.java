package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.Flags;

import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.declarations.AttribProtection.EProtection;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.IntrinsicDefUnit;

public class DefElementFlagsUtil {
	
	public static int elementFlagsForArchetype(EArcheType archeType) {
		switch (archeType) {
		case Module: return DeeModelConstants.FLAG_KIND_MODULE; 
		case Package: return DeeModelConstants.FLAG_KIND_PACKAGE; 
		case Variable: return DeeModelConstants.FLAG_KIND_VARIABLE; 
		case Function: return DeeModelConstants.FLAG_KIND_FUNCTION;
		case Constructor: return DeeModelConstants.FLAG_KIND_CONSTRUCTOR;
		
		case Struct: return DeeModelConstants.FLAG_KIND_STRUCT;
		case Union: return DeeModelConstants.FLAG_KIND_UNION;
		case Class: return DeeModelConstants.FLAG_KIND_CLASS;
		case Interface: return DeeModelConstants.FLAG_KIND_INTERFACE;
		
		case Template: return DeeModelConstants.FLAG_KIND_TEMPLATE;
		case TypeParameter: return DeeModelConstants.FLAG_KIND_TYPE_PARAM;
		case Mixin: return DeeModelConstants.FLAG_KIND_MIXIN;
		case Tuple: return DeeModelConstants.FLAG_KIND_TUPLE;
		
		case Enum: return DeeModelConstants.FLAG_KIND_ENUM;
		case EnumMember: return DeeModelConstants.FLAG_KIND_ENUM_MEMBER;
		case Alias: return DeeModelConstants.FLAG_KIND_ALIAS;
		}
		
		throw assertUnreachable();
	}
	
	public static EArcheType elementFlagsToArcheType(int elementFlags) {
		
		int archetypeFlag = elementFlags & DeeModelConstants.FLAGMASK_KIND;
		switch (archetypeFlag) {
		case DeeModelConstants.FLAG_KIND_MODULE: return EArcheType.Module;
		case DeeModelConstants.FLAG_KIND_PACKAGE: return EArcheType.Package;
		
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
		case DeeModelConstants.FLAG_KIND_ENUM_MEMBER: return EArcheType.EnumMember;
		case DeeModelConstants.FLAG_KIND_TUPLE: return EArcheType.Tuple;
		case DeeModelConstants.FLAG_KIND_TYPE_PARAM: return EArcheType.TypeParameter;
		case DeeModelConstants.FLAG_KIND_VARIABLE: return EArcheType.Variable;
		
		default:
			return null;
		}
	}
	
	public static int elementFlagsFromDefUnit(DefUnit defUnit) {
		EArcheType archeType = defUnit.getArcheType();
		int modifiers = elementFlagsForArchetype(archeType);
		
		if(defUnit instanceof CommonDefinition) {
			CommonDefinition commonDefinition = (CommonDefinition) defUnit;
			modifiers |= getCommonDefinitionModifiersInfo(commonDefinition);
		}
		
		if(defUnit instanceof IntrinsicDefUnit) {
			modifiers |= DeeModelConstants.FLAG_NATIVE;
		}
		
		return modifiers;
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
	
	public static int protectionFlagsFromProtection(EProtection protection) {
		switch(protection) {
		case PRIVATE: return Modifiers.AccPrivate;
		case PUBLIC: return Modifiers.AccPublic;
		case PROTECTED: return Modifiers.AccProtected;
		case PACKAGE: return DeeModelConstants.FLAG_PROTECTION_PACKAGE;
		case EXPORT: return DeeModelConstants.FLAG_PROTECTION_EXPORT;
		
		default: return Modifiers.AccPublic;
		}
	}
	
	public static int getCommonDefinitionModifiersInfo(CommonDefinition commonDef) {
		EProtection protection = commonDef.getEffectiveProtection();
		return getDeclarationModifierFlags(commonDef) | protectionFlagsFromProtection(protection);
	}
	
	protected static int getDeclarationModifierFlags(CommonDefinition elem) {
		int modifiers = 0;
		
		modifiers |= bitFlagForAttribute(elem, AttributeKinds.STATIC, DeeModelConstants.FLAG_STATIC);
		modifiers |= bitFlagForAttribute(elem, AttributeKinds.FINAL, DeeModelConstants.FLAG_FINAL);
		
		// Report these for variable only
		if(elem.getArcheType() == EArcheType.Variable) {
			modifiers |= bitFlagForAttribute(elem, AttributeKinds.CONST, DeeModelConstants.FLAG_CONST); 
			modifiers |= bitFlagForAttribute(elem, AttributeKinds.IMMUTABLE, DeeModelConstants.FLAG_IMMUTABLE);
		}
		
		modifiers |= bitFlagForAttribute(elem, AttributeKinds.ABSTRACT, DeeModelConstants.FLAG_ABSTRACT);
		
		// Report these for Function only
		if(elem.getArcheType() == EArcheType.Function) {
			modifiers |= bitFlagForAttribute(elem, AttributeKinds.OVERRIDE, DeeModelConstants.FLAG_OVERRIDE);
		}
		
		return modifiers;
	}
	
	public static int bitFlagForAttribute(CommonDefinition def, AttributeKinds attrib, int modifierFlag) {
		return def.hasAttribute(attrib) ? modifierFlag : 0;
	}
	
}