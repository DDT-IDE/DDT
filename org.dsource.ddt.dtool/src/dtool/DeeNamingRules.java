package dtool;

import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.utilbox.misc.StringUtil;

/**
 * Some stuff here breaks on UTF32 supplementary characters (we don't care much)
 */
public class DeeNamingRules {
	
	private static final String DEE_FILE_EXTENSION = ".d";
	private static final String DEE_HEADERFILE_EXTENSION = ".di";
	public static final String[] VALID_EXTENSIONS = array(DEE_FILE_EXTENSION, DEE_HEADERFILE_EXTENSION);
	
	public static boolean isValidDIdentifier(String name) {
		return name.length() > 0 && validDIdentifierLength(name) == name.length();
	}
	
	public static int validDIdentifierLength(String name) {
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
	
	
	public static boolean isValidCompilationUnitName(String name) {
		int offset = validDIdentifierLength(name);
		if(offset == 0) 
			return false;
		
		String fileExt = name.substring(offset, name.length());
		return isValidDFileExtension(fileExt);
	}
	
	private static boolean isValidDFileExtension(String fileExt) {
		return DEE_FILE_EXTENSION.equals(fileExt) || DEE_HEADERFILE_EXTENSION.equals(fileExt);
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
	
	public static boolean isValidPackageNamePart(String partname) {
		return isValidDIdentifier(partname);
	}
	
	public static String getModuleNameFromFileName(String fileName) {
		// Hum, should we validate the identifier?
		return StringUtil.upUntil(fileName, ".");
	}
	
}