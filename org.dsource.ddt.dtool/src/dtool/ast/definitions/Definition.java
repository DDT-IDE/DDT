package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import dtool.ast.declarations.AttribBasic;
import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.declarations.AttribProtection.Protection;
import dtool.parser.Token;

/**
 * Abstract class for all declaration-based DefUnits. 
 */
public abstract class Definition extends DefUnit {
	
//	public final ArrayView<Attribute> attributes; // TODO
	protected int defAttributesBitMask;
	
	public Definition(Token[] comments, ProtoDefSymbol defId) {
		super(defId, comments);
		this.defAttributesBitMask = 0;
	}
	
	/** Sets protection attribute. Can only set once. */
	public void setProtection(Protection protection) {
		assertTrue(getProtectionFromAttributesBitMask(defAttributesBitMask) == null);
		defAttributesBitMask |= getBitMaskForProtectionAttribute(protection) ;
	}
	
	public Protection getProtection() {
		return getProtectionFromAttributesBitMask(defAttributesBitMask);
	}
	
	public Protection getEffectiveProtection() {
		Protection protection = getProtection();
		return protection == null ? Protection.PUBLIC : protection;
	}
	
	public void setAttribute(AttribBasic declBasicAttrib) {
		defAttributesBitMask |= getBitMaskForBasicAttribute(declBasicAttrib.declAttrib);
	}
	
	public boolean hasAttribute(AttributeKinds attrib) {
		return (defAttributesBitMask & getBitMaskForBasicAttribute(attrib)) != 0;
	}
	
	
	public static int getBitMaskForProtectionAttribute(Protection protection) {
		switch (protection) {
		case PRIVATE: return 0x1;
		case PACKAGE: return 0x2;
		case PROTECTED: return 0x3;
		case PUBLIC: return 0x4;
		case EXPORT: return 0x5;
		}
		throw assertUnreachable();
	}
	
	public static int getBitMaskForBasicAttribute(AttributeKinds basicAttrib) {
		//Shift by 3 spaces first, first 3 bits are for prot attributes
		return (1 << 3) << basicAttrib.ordinal();
	}
	
	public static Protection getProtectionFromAttributesBitMask(int attributesBitMask) {
		switch (attributesBitMask & 0x7) {
		case 0x0: return null;
		case 0x1: return Protection.PRIVATE;
		case 0x2: return Protection.PACKAGE;
		case 0x3: return Protection.PROTECTED;
		case 0x4: return Protection.PUBLIC;
		case 0x5: return Protection.EXPORT;
		default: throw assertFail();
		}
	}
	
}