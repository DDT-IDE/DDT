package org.dsource.ddt.ide.core.model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.core.DeeCore;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.definitions.EArcheType;

public class DeeModelElementUtil {
	
	public static EArcheType elementFlagsToArcheType(IMember member) {
		int flags;
		try {
			flags = member.getFlags();
		} catch (ModelException e) {
			// TODO throw instead?
			DeeCore.log(e);
			flags = 0; // Ignore, use empty flags
		}
		return elementFlagsToArcheType(member, flags);
	}
	
	public static EArcheType elementFlagsToArcheType(IMember member, int flags) {
		switch (member.getElementType()) {
		case IModelElement.FIELD:
			return EArcheType.Variable;
		case IModelElement.METHOD:
			return EArcheType.Function;
		case IModelElement.TYPE:
			return typeElementFlagsToArcheType(flags);
		default:
			return null;
		}
	}
	
	public static EArcheType typeElementFlagsToArcheType(int elementFlags) {
		if((elementFlags & Modifiers.AccModule) != 0) {
			return EArcheType.Module;
		}
		if((elementFlags & Modifiers.AccInterface) != 0) {
			return EArcheType.Interface;
		}
		
		int archetypeFlag = elementFlags & DeeModelConstants.MODIFIERS_ARCHETYPE_MASK;
		switch (archetypeFlag) {
		case DeeModelConstants.TYPE_CLASS:
			return EArcheType.Class;
		case DeeModelConstants.TYPE_INTERFACE:
			return EArcheType.Interface;
		case DeeModelConstants.TYPE_STRUCT:
			return EArcheType.Struct;
		case DeeModelConstants.TYPE_UNION:
			return EArcheType.Union;
		case DeeModelConstants.TYPE_ENUM:
			return EArcheType.Enum;
		case DeeModelConstants.TYPE_TEMPLATE:
			return EArcheType.Template;
		case DeeModelConstants.TYPE_ALIAS:
			return EArcheType.Alias;
		case DeeModelConstants.TYPE_TYPEDEF:
			return EArcheType.Typedef;
		default:
			throw assertFail();
		}
	}
	
}
