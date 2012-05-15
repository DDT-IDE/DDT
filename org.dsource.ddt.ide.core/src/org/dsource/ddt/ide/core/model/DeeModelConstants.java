package org.dsource.ddt.ide.core.model;

import org.eclipse.dltk.ast.Modifiers;

public interface DeeModelConstants {
	
	// Archetype flags for IMember's IModelElement
	public static final int TYPE_CLASS =		0x01 << Modifiers.USER_MODIFIER;
	public static final int TYPE_INTERFACE =	0x02 << Modifiers.USER_MODIFIER;
	public static final int TYPE_STRUCT =		0x03 << Modifiers.USER_MODIFIER;
	public static final int TYPE_UNION =		0x04 << Modifiers.USER_MODIFIER;
	public static final int TYPE_ENUM =			0x05 << Modifiers.USER_MODIFIER;
	public static final int TYPE_TEMPLATE =		0x06 << Modifiers.USER_MODIFIER;
	public static final int TYPE_ALIAS =		0x07 << Modifiers.USER_MODIFIER;
	public static final int TYPE_TYPEDEF =		0x08 << Modifiers.USER_MODIFIER;
	// The following are somewhat redundant with org.eclipse.dltk.core.IModelElement.getElementType()
	public static final int TYPE_FUNCTION =		0x0D << Modifiers.USER_MODIFIER; 
	public static final int TYPE_VARIABLE =		0x0E << Modifiers.USER_MODIFIER;
	// we used 4 bits in total
	public static final int MODIFIERS_ARCHETYPE_MASK = 0x0F << Modifiers.USER_MODIFIER;
	
	public static final int FLAG_TEMPLATED = 1 << Modifiers.USER_MODIFIER + 4; // Not used yet
	// Indicates an export or package protection attribute 
	public static final int FLAG_ALT_PROTECTION = 1 << Modifiers.USER_MODIFIER + 5; 
	public static final int FLAG_PROTECTION_PRIVATE =	Modifiers.AccPrivate;
	public static final int FLAG_PROTECTION_PACKAGE =	Modifiers.AccPrivate | FLAG_ALT_PROTECTION;
	public static final int FLAG_PROTECTION_PROTECTED =	Modifiers.AccProtected;
	public static final int FLAG_PROTECTION_PUBLIC =	Modifiers.AccPublic;
	public static final int FLAG_PROTECTION_EXPORT =	Modifiers.AccPublic | FLAG_ALT_PROTECTION;
	
}
