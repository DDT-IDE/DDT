package mmrnmhrm.ui.text.color;

import melnorme.lang.ide.ui.text.coloring.LangColoringPreferencesHelper;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;

/**
 * Note: its not guaranteed that these methods are called from the UI thread 
 */
public abstract class DeeColorPreferenceInitializer extends LangColoringPreferencesHelper 
	implements IDeeColorConstants {
	
	private static RGB COLOR_BLACK_RGB       = new RGB(0x00, 0x00, 0x00);
	private static RGB COLOR_CYAN_RGB        = new RGB(0x00, 0xFF, 0xFF); 
	private static RGB COLOR_DARK_YELLOW_RGB = new RGB(0x80, 0x80, 0x00); 
	
	/** Sets the defaults for the color preferences. */
	public static void initializeDefaults(IPreferenceStore store) {
		setColoringStyle(store, DEE_DEFAULT, true, COLOR_BLACK_RGB, false, false, false);
		
		setColoringStyle(store, DEE_KEYWORDS, true, new RGB(0, 0, 127), true, false, false);
		
		setColoringStyle(store, DEE_BASICTYPES, true, new RGB(0, 0, 127), false, false, false);
		setColoringStyle(store, DEE_ANNOTATIONS, true, new RGB(100, 100, 100), false, false, false);
		
		setColoringStyle(store, DEE_OPERATORS, true, COLOR_BLACK_RGB, false, false, false);
		
		setColoringStyle(store, DEE_STRING, true, COLOR_DARK_YELLOW_RGB, false, false, false);
		setColoringStyle(store, DEE_DELIM_STRING, true, COLOR_DARK_YELLOW_RGB, false, false, false);
		setColoringStyle(store, DEE_CHARACTER_LITERALS, true, COLOR_DARK_YELLOW_RGB, false, false, false);
		
		setColoringStyle(store, DEE_LITERALS, true, new RGB(127, 64, 64), false, false, false);
		
		//RGB javaDocOthers = new RGB(63, 95, 191);
		//RGB javaDocTag = new RGB(63, 127, 95);
		setColoringStyle(store, DEE_DOCCOMMENT, true, new RGB(63, 95, 191), false, false, false);
		
		setColoringStyle(store, DEE_COMMENT, true, new RGB(63, 127, 95), false, false, false);
		
		setColoringStyle(store, DEE_SPECIAL, false, COLOR_CYAN_RGB, false, false, true);
	}
	
}