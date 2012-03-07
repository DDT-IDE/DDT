package org.dsource.ddt.ide.core.model;

import org.eclipse.dltk.ast.Modifiers;

public interface DeeModelConstants {
	
	// Archetype flags for IModelElement (use 3 bits in total)
	public static final int MODIFIERS_ARCHETYPE_MASK = 0x7 << Modifiers.USER_MODIFIER + 0;
	public static final int TYPE_CLASS = 0x0 << Modifiers.USER_MODIFIER;
	public static final int TYPE_INTERFACE = 0x1 << Modifiers.USER_MODIFIER;
	public static final int TYPE_STRUCT = 0x2 << Modifiers.USER_MODIFIER;
	public static final int TYPE_UNION = 0x3 << Modifiers.USER_MODIFIER;
	public static final int TYPE_ENUM = 0x4 << Modifiers.USER_MODIFIER;
	public static final int TYPE_TEMPLATE = 0x5 << Modifiers.USER_MODIFIER;
	public static final int TYPE_ALIAS = 0x6 << Modifiers.USER_MODIFIER;
	public static final int TYPE_TYPEDEF = 0x7 << Modifiers.USER_MODIFIER;
	
	public static final int FLAG_TEMPLATED = 1 << Modifiers.USER_MODIFIER + 3; // Not used yet
	// Indicates an export or package protection attribute 
	public static final int FLAG_ALT_PROTECTION = 1 << Modifiers.USER_MODIFIER + 4; 
	public static final int FLAG_PROTECTION_PACKAGE =	FLAG_ALT_PROTECTION | Modifiers.AccPrivate;
	public static final int FLAG_PROTECTION_EXPORT =	FLAG_ALT_PROTECTION | Modifiers.AccPublic;
	
}
