package dtool;

import descent.core.JavaConventions_Common;

/**
 * XXX: keep an eye on {@link JavaConventions_Common}
 * Also, lots of stuff here breaks on UTF32 supplementary characters
 */
public class DeeNamingRules {
	
	private static final String DEE_FILE_EXTENSION = ".d";
	private static final String DEE_HEADERFILE_EXTENSION = ".di";
	public static final String[] VALID_EXTENSIONS = new String[] {
		".d", ".di"
	};
	
	public static boolean isValidDIdentifier(CharSequence name) {
		return name.length() > 0 && validDIdentifierLength(name) == name.length();
	}
	
	public static int validDIdentifierLength(CharSequence name) {
		int pos = 0;
		int length = name.length();
		if(length == 0)
			return pos;
		
		if(!(Character.isLetter(name.charAt(0)) || name.charAt(0) == '_'))
			return pos;
		
		for(pos = 1; pos < length; ++pos){
			if(!Character.isLetterOrDigit(name.charAt(pos)) && !(name.charAt(pos) == '_'))
				break;
		}
		
		return pos;
	}
	
	
	public static boolean isValidCompilationUnitName(CharSequence name) {
		int offset = validDIdentifierLength(name);
		if(offset == 0) 
			return false;
		
		CharSequence fileExt = name.subSequence(offset, name.length());
		return isValidDFileExtension(fileExt);
	}
	
	private static boolean isValidDFileExtension(CharSequence fileExt) {
		return DEE_FILE_EXTENSION.contentEquals(fileExt) || DEE_HEADERFILE_EXTENSION.contentEquals(fileExt);
	}
	
	public static boolean isValidPackagePathName(String pathname) {
		if(pathname.equals(""))
			return true;
		
		String[] parts = pathname.split("/");
		for (int i = 0; i < parts.length; i++) {
			if(!isValidPackageNamePart(parts[i]))
				return false;
		}
		return true;
	}
	
	public static boolean isValidPackageNamePart(CharSequence partname) {
		return isValidDIdentifier(partname);
	}
	
}
