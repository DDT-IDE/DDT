package mmrnmhrm.core.model_elements;

import org.eclipse.dltk.ast.Modifiers;

public interface DeeModelConstants {
	
	public static final int FLAG_KIND_MODULE =		Modifiers.AccModule;
	public static final int FLAG_KIND_PACKAGE =		Modifiers.AccNameSpace;
	
	// Archetype flags for IMember's IModelElement
	public static final int FLAG_KIND_STRUCT =		0x00 << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_UNION =		0x01 << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_CLASS =		0x02 << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_INTERFACE =	0x03 << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_TEMPLATE =	0x04 << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_MIXIN =		0x05 << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_ENUM =		0x06 << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_ALIAS =		0x07 << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_FUNCTION =	0x0A << Modifiers.USER_MODIFIER; 
	public static final int FLAG_KIND_CONSTRUCTOR =	0x0B << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_ENUM_MEMBER =	0x0C << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_TUPLE =		0x0D << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_TYPE_PARAM =	0x0E << Modifiers.USER_MODIFIER;
	public static final int FLAG_KIND_VARIABLE =	0x0F << Modifiers.USER_MODIFIER;
	// we used 4 bits in total, that is 0x0F as a mask
	static final int FLAGMASK_KIND_BASE =  0x0F << Modifiers.USER_MODIFIER;
	
	public static final int FLAGMASK_KIND = FLAGMASK_KIND_BASE | Modifiers.AccNameSpace | Modifiers.AccModule;
	
	
	public static final int FLAG_NATIVE = 1 << Modifiers.USER_MODIFIER + 6;
	public static final int FLAG_TEMPLATED = 1 << Modifiers.USER_MODIFIER + 7; // Not used yet
	
	// Modifier flag for protection flag that indicates an export or package protection attribute: 
	static final int FLAG_ALT_PROTECTION = 1 << Modifiers.USER_MODIFIER + 8;
	
	public static final int FLAG_PROTECTION_PRIVATE =	Modifiers.AccPrivate;
	public static final int FLAG_PROTECTION_PACKAGE =	Modifiers.AccPrivate | FLAG_ALT_PROTECTION;
	public static final int FLAG_PROTECTION_PROTECTED =	Modifiers.AccProtected;
	public static final int FLAG_PROTECTION_PUBLIC =	Modifiers.AccPublic;
	public static final int FLAG_PROTECTION_EXPORT =	Modifiers.AccPublic | FLAG_ALT_PROTECTION;
	
	public static final int FLAG_OVERRIDE = 1 << Modifiers.USER_MODIFIER + 9;
	
	public static final int FLAG_STATIC = Modifiers.AccStatic;
	public static final int FLAG_FINAL = Modifiers.AccFinal;
	public static final int FLAG_ABSTRACT = Modifiers.AccAbstract;
	public static final int FLAG_CONST =  Modifiers.AccConst;
	public static final int FLAG_IMMUTABLE = 1 << Modifiers.USER_MODIFIER + 11;
	
}