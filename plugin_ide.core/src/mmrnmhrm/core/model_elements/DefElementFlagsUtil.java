package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.lang.tooling.symbols.INamedElement;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.Flags;

import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.declarations.AttribProtection.EProtection;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.ITemplatableElement;

public class DefElementFlagsUtil {
	
	public static int elementFlagsForArchetype(EArcheType archeType) {
		switch (archeType) {
		case Module: return DefElementFlagConstants.FLAG_KIND_MODULE; 
		case Package: return DefElementFlagConstants.FLAG_KIND_PACKAGE; 
		case Variable: return DefElementFlagConstants.FLAG_KIND_VARIABLE; 
		case Function: return DefElementFlagConstants.FLAG_KIND_FUNCTION;
		case Constructor: return DefElementFlagConstants.FLAG_KIND_CONSTRUCTOR;
		
		case Struct: return DefElementFlagConstants.FLAG_KIND_STRUCT;
		case Union: return DefElementFlagConstants.FLAG_KIND_UNION;
		case Class: return DefElementFlagConstants.FLAG_KIND_CLASS;
		case Interface: return DefElementFlagConstants.FLAG_KIND_INTERFACE;
		
		case Template: return DefElementFlagConstants.FLAG_KIND_TEMPLATE;
		case TypeParameter: return DefElementFlagConstants.FLAG_KIND_TYPE_PARAM;
		case Mixin: return DefElementFlagConstants.FLAG_KIND_MIXIN;
		case Tuple: return DefElementFlagConstants.FLAG_KIND_TUPLE;
		
		case Enum: return DefElementFlagConstants.FLAG_KIND_ENUM;
		case EnumMember: return DefElementFlagConstants.FLAG_KIND_ENUM_MEMBER;
		case Alias: return DefElementFlagConstants.FLAG_KIND_ALIAS;
		// XXX: Not entirely correct, but errors should come up in content assist only:
		case Error: return DefElementFlagConstants.FLAG_KIND_ALIAS; 
		}
		
		throw assertUnreachable();
	}
	
	public static EArcheType elementFlagsToArcheType(int elementFlags) {
		
		int archetypeFlag = elementFlags & DefElementFlagConstants.FLAGMASK_KIND;
		switch (archetypeFlag) {
		case DefElementFlagConstants.FLAG_KIND_MODULE: return EArcheType.Module;
		case DefElementFlagConstants.FLAG_KIND_PACKAGE: return EArcheType.Package;
		
		case DefElementFlagConstants.FLAG_KIND_STRUCT: return EArcheType.Struct;
		case DefElementFlagConstants.FLAG_KIND_UNION: return EArcheType.Union;
		case DefElementFlagConstants.FLAG_KIND_CLASS: return EArcheType.Class;
		case DefElementFlagConstants.FLAG_KIND_INTERFACE: return EArcheType.Interface;
		case DefElementFlagConstants.FLAG_KIND_TEMPLATE: return EArcheType.Template;
		case DefElementFlagConstants.FLAG_KIND_MIXIN: return EArcheType.Mixin;
		case DefElementFlagConstants.FLAG_KIND_ENUM: return EArcheType.Enum;
		case DefElementFlagConstants.FLAG_KIND_ALIAS: return EArcheType.Alias;
		case DefElementFlagConstants.FLAG_KIND_FUNCTION: return EArcheType.Function;
		case DefElementFlagConstants.FLAG_KIND_CONSTRUCTOR: return EArcheType.Constructor;
		case DefElementFlagConstants.FLAG_KIND_ENUM_MEMBER: return EArcheType.EnumMember;
		case DefElementFlagConstants.FLAG_KIND_TUPLE: return EArcheType.Tuple;
		case DefElementFlagConstants.FLAG_KIND_TYPE_PARAM: return EArcheType.TypeParameter;
		case DefElementFlagConstants.FLAG_KIND_VARIABLE: return EArcheType.Variable;
		
		default:
			return null;
		}
	}
	
	public static int elementFlagsForNamedElement(INamedElement defElement) {
		EArcheType archeType = defElement.getArcheType();
		int modifiers = elementFlagsForArchetype(archeType);
		
		if(defElement instanceof CommonDefinition) {
			CommonDefinition commonDefinition = (CommonDefinition) defElement;
			modifiers |= getCommonDefinitionModifiersInfo(commonDefinition);
		}
		
		if(defElement.isLanguageIntrinsic()) {
			modifiers |= DefElementFlagConstants.FLAG_NATIVE;
		}
		
		return modifiers;
	}
	
	public static EProtection elementFlagsToProtection(int elementFlags, EProtection undefined) {
		if((elementFlags & DefElementFlagConstants.FLAG_ALT_PROTECTION) != 0) {
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
		case PACKAGE: return DefElementFlagConstants.FLAG_PROTECTION_PACKAGE;
		case EXPORT: return DefElementFlagConstants.FLAG_PROTECTION_EXPORT;
		
		default: return Modifiers.AccPublic;
		}
	}
	
	public static int getCommonDefinitionModifiersInfo(CommonDefinition commonDef) {
		EProtection protection = commonDef.getEffectiveProtection();
		return getDeclarationModifierFlags(commonDef) | protectionFlagsFromProtection(protection);
	}
	
	protected static int getDeclarationModifierFlags(CommonDefinition elem) {
		int modifiers = 0;
		
		modifiers |= bitFlagForAttribute(elem, AttributeKinds.STATIC, DefElementFlagConstants.FLAG_STATIC);
		modifiers |= bitFlagForAttribute(elem, AttributeKinds.FINAL, DefElementFlagConstants.FLAG_FINAL);
		
		// Report these for variable only
		if(elem.getArcheType() == EArcheType.Variable) {
			modifiers |= bitFlagForAttribute(elem, AttributeKinds.CONST, DefElementFlagConstants.FLAG_CONST); 
			modifiers |= bitFlagForAttribute(elem, AttributeKinds.IMMUTABLE, DefElementFlagConstants.FLAG_IMMUTABLE);
		}
		
		modifiers |= bitFlagForAttribute(elem, AttributeKinds.ABSTRACT, DefElementFlagConstants.FLAG_ABSTRACT);
		
		// Report these for Function only
		if(elem.getArcheType() == EArcheType.Function) {
			modifiers |= bitFlagForAttribute(elem, AttributeKinds.OVERRIDE, DefElementFlagConstants.FLAG_OVERRIDE);
		}
		
		if(elem instanceof ITemplatableElement) {
			ITemplatableElement templatableElement = (ITemplatableElement) elem;
			if(templatableElement.isTemplated()) {
				modifiers |= DefElementFlagConstants.FLAG_TEMPLATED;
			}
		}
		
		return modifiers;
	}
	
	public static int bitFlagForAttribute(CommonDefinition def, AttributeKinds attrib, int modifierFlag) {
		return def.hasAttribute(attrib) ? modifierFlag : 0;
	}
	
}