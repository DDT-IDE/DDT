package mmrnmhrm.ui.text.color;


/**
 * Color preference constants used in the Dee preference store. 
 */
public interface IDeeColorConstants {
	

	/** Prefix for D preference keys. */
	String PREFIX = "dee.coloring."; 

	// XXX: DLTK: use DLTKColorConstants constants?
	String DEE_SPECIAL = PREFIX + "special";
	String DEE_DEFAULT = PREFIX + "default";
	String DEE_COMMENT = PREFIX + "comment";
	String DEE_DOCCOMMENT = PREFIX + "doccomment";
	String DEE_STRING = PREFIX + "string";
	String DEE_RAW_STRING = PREFIX + "rawstring";
	String DEE_DELIM_STRING = PREFIX + "delimstring";
	String DEE_CHARACTER_LITERALS = PREFIX + "character";
	String DEE_LITERALS = PREFIX + "literals";
	String DEE_OPERATORS = PREFIX + "operators";
	String DEE_BASICTYPES = PREFIX + "basictypes";
	String DEE_KEYWORD = PREFIX + "keyword";
	// XXX: DLTK: use DLTK_SINGLE_LINE_COMMENT constants?

}
