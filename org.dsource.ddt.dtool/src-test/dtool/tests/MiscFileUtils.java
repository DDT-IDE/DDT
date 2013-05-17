package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import melnorme.utilbox.core.Function;
import melnorme.utilbox.core.VoidFunction;
import melnorme.utilbox.misc.StreamUtil;

/**
 * Miscellaneous utils relating to {@link File}'s.
 * The semantics of these util methods are likely not strong/good enough to be used outside of test code.
 */
public class MiscFileUtils {
	
	public static void deleteDir(File dir) {
		if(!dir.exists()) 
			return;
		
		File[] listFiles = dir.listFiles();
		for(File childFile : listFiles) {
			if(childFile.isFile()) {
				childFile.delete();
			} else {
				deleteDir(childFile);
			}
		}
		if(dir.delete() == false) {
			throw new RuntimeException("Failed to delete dir");
		}
	}
	
	public static void traverseFiles(File folder, boolean recurseDirs, Function<File, Void> fileVisitor) {
		traverseFiles(folder, recurseDirs, fileVisitor, null);
	}
	
	public static void traverseFiles(File folder, boolean recurseDirs, Function<File, Void> fileVisitor,
			FilenameFilter filter) {
		assertTrue(folder.exists() && folder.isDirectory());
		File[] children = folder.listFiles(filter);
		assertNotNull(children);
		
		for (File file : children) {
			if(file.isDirectory() && recurseDirs) {
				fileVisitor.evaluate(file);
				traverseFiles(file, recurseDirs, fileVisitor, filter);
			} else {
				fileVisitor.evaluate(file);
			}
		}
	}
	
	public static ArrayList<File> collectZipFiles(File folder) throws IOException {
		final ArrayList<File> fileList = new ArrayList<>();
		VoidFunction<File> fileVisitor = new VoidFunction<File>() {
			@Override
			public Void evaluate(File file) {
				if(file.isFile() && file.getName().endsWith(".zip")) {
					fileList.add(file);
				}
				return null;
			}
		};
		MiscFileUtils.traverseFiles(folder, false, fileVisitor);
		return fileList;
	}
	
	public static void unzipFile(File zipFile, File parentDir) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		try {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			
			while(entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				
				File entryFile = new File(parentDir, entry.getName());
				
				if(entry.isDirectory()) {
					entryFile.mkdirs();
					continue;
				}
				
				entryFile.getParentFile().mkdirs();
				
				StreamUtil.copyStream(zip.getInputStream(entry), 
						new BufferedOutputStream(new FileOutputStream(entryFile)), true);
			}
			
		} finally {
			zip.close();
		}
	}
	
}
