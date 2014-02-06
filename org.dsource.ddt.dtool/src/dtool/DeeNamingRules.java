package dtool;

import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.utilbox.misc.StringUtil;
import dtool.parser.DeeLexerKeywordHelper;
import dtool.parser.DeeTokens;

/**
 * Some stuff here breaks on UTF32 supplementary characters (we don't care much)
 */
public class DeeNamingRules {
	
	private static final String DEE_FILE_EXTENSION = ".d";
	private static final String DEE_HEADERFILE_EXTENSION = ".di";
	public static final String[] VALID_EXTENSIONS = array(DEE_FILE_EXTENSION, DEE_HEADERFILE_EXTENSION);
	
	public static boolean isValidDIdentifier(String text) {
		if(!isValidDAlphaNumeric(text))
			return false;
		
		// Check for keywords
		DeeTokens keywordToken = DeeLexerKeywordHelper.getKeywordToken(text);
		if(keywordToken != null) 
			return false;
		
		return true;
	}
	
	public static boolean isValidDAlphaNumeric(String text) {
		if(text.length() == 0) 
			return false;
		
		if(!(Character.isLetter(text.charAt(0)) || text.charAt(0) == '_'))
			return false;
		
		int pos = 0;
		int length = text.length();
		for(pos = 1; pos < length; ++pos){
			if(!Character.isLetterOrDigit(text.charAt(pos)) && !(text.charAt(pos) == '_'))
				return false;
		}
		
		return true;	
	}
	
	/** @return Whether given fileName is a strictly valid compilationUnitName. 
	 * For a compilation unit name to be strictly valid, one must be able to import it in some way. */
	public static boolean isValidCompilationUnitName(String fileName) {
		return isValidCompilationUnitName(fileName, true);
	}
	
	public static boolean isValidCompilationUnitName(String fileName, boolean strict) {
		String fileNameWithoutExtension = getModuleNameFromFileName(fileName);
		String fileExtension = fileName.substring(fileNameWithoutExtension.length());
		
		return strict ?
				(isValidDFileExtension(fileExtension) && isValidDIdentifier(fileNameWithoutExtension)) :
				(isValidDFileExtension(fileExtension) && isValidDAlphaNumeric(fileNameWithoutExtension));
	}
	
	private static boolean isValidDFileExtension(String fileExt) {
		return DEE_FILE_EXTENSION.equals(fileExt) || DEE_HEADERFILE_EXTENSION.equals(fileExt);
	}
	
	
	public static boolean isValidPackagePathName(String packagePath) {
		return isValidPackagePathName(packagePath, true);
	}
	
	public static boolean isValidPackagePathName(String packagePath, boolean strict) {
		if(packagePath.equals(""))
			return true;
		
		String[] parts = packagePath.split("/");
		for (int i = 0; i < parts.length; i++) {
			if(!isValidPackageNamePart(parts[i], strict))
				return false;
		}
		return true;
	}
	
	public static boolean isValidPackageNamePart(String partname, boolean strict) {
		return strict ? isValidDIdentifier(partname) : isValidDAlphaNumeric(partname);
	}
	
	public static String getModuleNameFromFileName(String fileName) {
		// Hum, should we validate the identifier?
		return StringUtil.substringUntilMatch(fileName, ".");
	}
	
}