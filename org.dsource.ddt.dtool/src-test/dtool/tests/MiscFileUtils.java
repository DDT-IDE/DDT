package dtool.tests;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import melnorme.utilbox.core.Function;
import melnorme.utilbox.misc.StreamUtil;

/**
 * Miscellaneous utils relating to {@link File}'s.
 */
public class MiscFileUtils {
	
	
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
	
	public static void traverseFiles(File folder, boolean recurseDirs, Function<File, Void> fileVisitor,
			FilenameFilter filter)
			throws IOException {
		File[] children = folder.listFiles(filter);
		
		if(children == null)
			throw new IOException("Failed to listFiles for folder: " + folder);
		
		for (File file : children) {
			if(file.isDirectory() && recurseDirs) {
				fileVisitor.evaluate(file);
				traverseFiles(file, recurseDirs, fileVisitor, filter);
			} else {
				fileVisitor.evaluate(file);
			}
		}
	}
	
}
