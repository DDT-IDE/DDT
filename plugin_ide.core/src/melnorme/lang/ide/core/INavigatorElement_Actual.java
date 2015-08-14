package melnorme.lang.ide.core;

import melnorme.lang.tooling.LANG_SPECIFIC;
import mmrnmhrm.core.workspace.viewmodel.IDeeNavigatorElement;

/***
 * This class should never be referred to directly in a plugin.xml.
 * This is to avoid conflict with other LangEclipseIDE IDEs that have would a class with identical name 
 * 
 * Rather, this interface should solely be an alias to an specific IDE interface
 * contained in the specific package namespace of the IDE (ie, the plugin name). 
 */
@LANG_SPECIFIC
public interface INavigatorElement_Actual extends IDeeNavigatorElement {
	
}