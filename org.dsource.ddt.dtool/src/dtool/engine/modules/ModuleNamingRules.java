package dtool.engine.modules;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.array;

import java.nio.file.Path;

import melnorme.utilbox.misc.StringUtil;
import dtool.parser.LexingUtil;

/**
 * Naming rules code for compilation units and packages.
 * 
 */
public class ModuleNamingRules {
	
	private static final String DEE_FILE_EXTENSION = ".d";
	private static final String DEE_HEADERFILE_EXTENSION = ".di";
	
	public static final String[] VALID_EXTENSIONS = array(DEE_FILE_EXTENSION, DEE_HEADERFILE_EXTENSION);
	
	
	/* ----------------- ----------------- */
	
	public static ModuleFullName getValidModuleFullNameOrNull(Path filePath) {
		String fileName = filePath.getFileName().toString();
		String fileExtension = StringUtil.substringFromMatch(".", fileName);
		
		// TODO: test this path
		if(!isValidDFileExtension(fileExtension)) {
			return null;
		}
		
		ModuleFullName moduleFullName = getModuleFullName(filePath);
		if(moduleFullName == null || !moduleFullName.isValid()) {
			return null;
		}
		return moduleFullName;
	}
	
	protected static ModuleFullName getModuleFullName(Path filePath) {
		if(filePath.getNameCount() == 0)
			return null;
		
		int count = filePath.getNameCount();
		// TODO: "package.d" rule /*BUG here*/
		
		String[] segments = new String[count];
		
		for (int i = 0; i < count - 1; i++) {
			segments[i] = filePath.getName(i).toString();
		}
		
		String fileName = filePath.getFileName().toString();
		segments[count - 1] = getDefaultModuleNameFromFileName(fileName);
		return new ModuleFullName(segments);
	}
	
	public static String getDefaultModuleNameFromFileName(String fileName) {
		return StringUtil.substringUntilMatch(fileName, ".");
	}
	
	public static String getDefaultModuleName(Path filePath) {
		Path fileName = filePath.getFileName();
		assertNotNull(fileName);
		return getDefaultModuleNameFromFileName(fileName.toString());
	}
	
	/* ----------------- ----------------- */
	
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
			if(!ModuleNamingRules.isValidCompilationUnitName(fileName)) {
				return null;
			}
			String moduleName = getDefaultModuleNameFromFileName(fileName);
			return packageName.isEmpty() ? moduleName : packageName + "." + moduleName;
		}
		
	}
	
	/** @return Whether given fileName is a strictly valid compilationUnitName. 
	 * For a compilation unit name to be strictly valid, one must be able to import it in some way. */
	public static boolean isValidCompilationUnitName(String fileName) {
		return isValidCompilationUnitName(fileName, true);
	}
	
	public static boolean isValidCompilationUnitName(String fileName, boolean strict) {
		String fileNameWithoutExtension = getDefaultModuleNameFromFileName(fileName);
		String fileExtension = fileName.substring(fileNameWithoutExtension.length());
		
		return strict ?
				(isValidDFileExtension(fileExtension) && LexingUtil.isValidDIdentifier(fileNameWithoutExtension)) :
				(isValidDFileExtension(fileExtension) && LexingUtil.isValidDAlphaNumeric(fileNameWithoutExtension));
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
		for (String part : parts) {
			if(!isValidPackageNamePart(part, strict))
				return false;
		}
		return true;
	}
	
	public static boolean isValidPackagePath(Path relPath) {
		for (Path part : relPath) {
			if(!isValidPackageNamePart(part.getFileName().toString(), true))
				return false;
		}
		return true;
	}
	
	public static boolean isValidPackageNamePart(String partname, boolean strict) {
		return strict ? LexingUtil.isValidDIdentifier(partname) : LexingUtil.isValidDAlphaNumeric(partname);
	}
	
}