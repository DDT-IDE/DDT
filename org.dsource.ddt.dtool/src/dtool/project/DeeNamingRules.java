package dtool.project;

import static melnorme.utilbox.core.CoreUtil.array;
import static melnorme.utilbox.misc.IteratorUtil.iterable;

import java.nio.file.Path;

import melnorme.utilbox.misc.StringUtil;
import dtool.model.ModuleFullName;
import dtool.parser.DeeLexerKeywordHelper;
import dtool.parser.DeeTokens;

/**
 * Naming rules code for compilation units and packages.
 * 
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
	
	public static String getModuleNameFromFilePath(String filePath) {
		// Note: we dont convert filePath to a java.nio.file.Path so that 
		// this method can handle OS-invalid path names as well.
		return getModuleNameFromFileName(StringUtil.substringAfterMatch(filePath, "/"));
	}
	
	public static String getModuleNameFromFileName(String fileName) {
		return StringUtil.substringUntilMatch(fileName, ".");
	}
	
	public static String getModuleNameFromFilePath(Path filePath) {
		if(filePath.getNameCount() == 0)
			return null;
		return getModuleNameFromFileName(filePath.getFileName().toString());
	}
	
	/**
	 * @return The fully qualified name of the module with given packagePath and given fileName,
	 * or null if this compilation has a name or path that cannot be imported.
	 * The fully qualified name is the name by which the module can be imported.
	 * @param packagePath package path separated by "/"
	 * @param fileName the compilation unit file name
	 */
	public static String getModuleFQNameFromFilePath(String packagePath, String fileName) {
		packagePath = StringUtil.trimEnding(packagePath, "/");
		String packageName = packagePath.replace("/", ".");
		
		if(fileName.equals("package.d") && !packageName.isEmpty()) {
			return packageName;
		} else {
			if(!DeeNamingRules.isValidCompilationUnitName(fileName)) {
				return null;
			}
			String moduleName = getModuleNameFromFileName(fileName);
			return packageName.isEmpty() ? moduleName : packageName + "." + moduleName;
		}
		
	}
	
	
	/* ----------------- ----------------- */
	
	public static ModuleFullName getModuleFullNameFromPath(Path filePath) {
		if(filePath.getNameCount() == 0)
			return null;
		
		StringBuilder moduleNameSB = new StringBuilder();
		
		for (Path packagePath : iterable(filePath.getParent())) {
			String packageName = packagePath.getFileName().toString();
			if(!isValidPackagePathName(packageName))
				return null;
			moduleNameSB.append(packageName);
			moduleNameSB.append(".");
		}
		
		String fileName = filePath.getFileName().toString();
		if(!isValidCompilationUnitName(fileName))
			return null;
		moduleNameSB.append(getModuleNameFromFileName(fileName));
		return new ModuleFullName(moduleNameSB.toString());
	}
	
}