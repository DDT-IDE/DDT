/*******************************************************************************
 * Copyright (c) 2007 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.utilbox.misc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.fntypes.Getter;

/**
 * Miscellaneous file utilities.
 * See also: {@link Files} (Some code has been superceded by it).
 */
public final class FileUtil {
	
	/** Read all bytes of the given file. 
	 * @return the bytes that where read in a {@link ByteArrayOutputStream}. */
	public static IByteSequence readBytesFromFile(File file) throws IOException {
		long fileLength = file.length();
		/*
		 * You cannot create an array using a long type. It needs to be an
		 * int type. Before converting to an int type, check to ensure
		 * that file is not larger than Integer.MAX_VALUE.
		 */
		if (fileLength > Integer.MAX_VALUE) 
			throw new IOException("File is too large, size is bigger than " + Integer.MAX_VALUE);
		
		return StreamUtil.readAllBytesFromStream(new FileInputStream(file), (int) fileLength);
	}
	
	/** Read all bytes from the given file.
	 * @return a String created from those bytes, with given charsetName. */
	public static String readStringFromFile(File file, String charsetName) throws IOException {
		return readBytesFromFile(file).toString(Charset.forName(charsetName));
	}
	
	/** Read all bytes from the given file.
	 * @return a String created from those bytes, with given charset. */
	public static String readStringFromFile(File file, Charset charset) throws IOException {
		return readBytesFromFile(file).toString(charset);
	}
	
	/** Read all bytes from the given file.
	 * @return a String created from those bytes, with given charset. */
	public static String readStringFromFile(Path file, Charset charset) throws IOException {
		return readBytesFromFile(file.toFile()).toString(charset);
	}
	
	public static String readStringFromFile(Location loc, Charset charset, Getter<String, RuntimeException> errorMsg) 
			throws CommonException {
		try {
			return readStringFromFile(loc.toFile(), charset);
		} catch(IOException e) {
			throw new CommonException(errorMsg.get(), e);
		}
	}
	
	
	/** Write the given array of bytes to given file */
	public static void writeBytesToFile(File file, byte[] bytes) throws IOException {
		FileOutputStream fileOS = new FileOutputStream(file);
		StreamUtil.writeBytesToStream(bytes, fileOS);
	}
	
	/** Writes given chars array to given writer. 
	 * Close writer afterwards. */
	public static void writeCharsToFile(File file, char[] chars, Charset charset) 
			throws IOException, FileNotFoundException {
		FileOutputStream fileOS = new FileOutputStream(file);
		OutputStreamWriter osWriter = new OutputStreamWriter(fileOS, charset);
		StreamUtil.writeCharsToWriter(chars, osWriter);
	}
	
	/** Writes given string to given writer. 
	 * Close writer afterwards. */
	public static void writeStringToFile(File file, String string, Charset charset) 
			throws IOException, FileNotFoundException {
		FileOutputStream fileOS = new FileOutputStream(file);
		OutputStreamWriter osWriter = new OutputStreamWriter(fileOS, charset);
		StreamUtil.writeStringToWriter(string, osWriter);
	}
	
	/* -----------------  ----------------- */
	
	public static void copyToDir(Path source, Path targetDir, CopyOption... options) throws IOException {
		Files.copy(source, targetDir.resolve(source.getFileName()), options);
	}
	
	public static boolean deleteIfExists(Path path) throws IOException {
		return Files.deleteIfExists(path);
	}
	
	
	public static void deleteDirContents(Path dir) throws IOException {
		deleteDirContents(Location.create_fromValid(dir), false);
	}
	
	/* ----------------- TODO: convert the utils above to Location ----------------- */
	
	@Deprecated
	public static void deleteDir(File dir) throws IOException {
		deleteDirContents(Location.create_fromValid(dir.toPath()));
	}
	@Deprecated
	public static void deleteDir(Path dir) throws IOException {
		deleteDirContents(Location.create_fromValid(dir));
	}
	public static void deleteDir(Location dir) throws IOException {
		deleteDirContents(dir, true);
	}
	
	public static void deleteDirContents(Location directory) throws IOException {
		deleteDirContents(directory, false);
	}
	protected static void deleteDirContents(final Location directory, final boolean deleteDirectory) 
			throws IOException {
		if(!directory.toFile().exists()) {
			return;
		}
		
		Files.walkFileTree(directory.toPath(),
			EnumSet.noneOf(FileVisitOption.class),
			Integer.MAX_VALUE,
			new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.deleteIfExists(file);
					return FileVisitResult.CONTINUE;
				}
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if(!dir.equals(directory) || deleteDirectory) {
						Files.deleteIfExists(dir);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		
	}
	
}