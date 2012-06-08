package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import melnorme.utilbox.core.Function;
import melnorme.utilbox.core.VoidFunction;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

import dtool.DeeNamingRules_Test;
import dtool.tests.MiscFileUtils;


public abstract class Parser__FileParseTest extends Parser__CommonTest {
	
	protected final File file;
	
	public Parser__FileParseTest(File file) {
		this.file = file;
		assertTrue(file.isFile());
	}
	
	@Test
	public void testParseFile() throws IOException {
		parseFile(file, failOnSyntaxErrors(), checkSourceRanges());
	}
	
	protected boolean failOnSyntaxErrors() {
		return true;
	}
	
	protected boolean checkSourceRanges() {
		return false;
	}
	
	protected static void parseFile(File file, boolean failOnSyntaxErrors, boolean checkSourceRanges) {
		String source = readStringFromFileUnchecked(file);
		testParse(source, failOnSyntaxErrors ? false : null, checkSourceRanges);
	}
	
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs) throws IOException {
		return getDeeModuleList(folder, recurseDirs, false);
	}
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs, final boolean validCUsOnly)
			throws IOException {
		
		final boolean addInAnyFileName = !validCUsOnly;
		final ArrayList<File> fileList = new ArrayList<File>();
		
		VoidFunction<File> fileVisitor = new VoidFunction<File>() {
			@Override
			public Void evaluate(File file) {
				if(file.isFile()) {
					fileList.add(file);
				}
				return null;
			}
		};
		
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File parent, String childName) {
				System.out.println("dir:" + parent +" " + childName);
				File childFile = new File(parent, childName);
				if(childFile.isDirectory()) {
					// exclude team private folder, like .svn, and other crap
					return !childName.startsWith(".");
				} else {
					return addInAnyFileName || DeeNamingRules_Test.isValidCompilationUnitName(childName);
				}
			}
		};
		MiscFileUtils.traverseFiles(folder, recurseDirs, fileVisitor, filter);
		return fileList;
	}
	
	public static Collection<Object[]> getTestFilesFromFolderAsParameterList(File folder) throws IOException {
		assertTrue(folder.exists() && folder.isDirectory());
		ArrayList<File> deeModuleList = getDeeModuleList(folder, true);
		
		Function<Object, Object[]> arrayWrap = new Function<Object, Object[]>() {
			@Override
			public Object[] evaluate(Object obj) {
				return new Object[] { obj };
			};
		};
		
		return Arrays.asList(ArrayUtil.map(deeModuleList, arrayWrap, Object[].class));
	}
	
}