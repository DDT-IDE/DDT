/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StreamUtil;
import melnorme.utilbox.misc.StringUtil;

/**
 * Some extended functionality to {@link CommonTestUtils}. 
 * May be not be well designed or of interest to most tests. 
 */
public class CommonTestExt extends CommonTest {
	
	public CommonTestExt() {
		super();
	}
	
	/* -------------  Resources stuff   ------------ */
	
	public static final Charset DEFAULT_TESTDATA_ENCODING = StringUtil.UTF8;
	
	public static String readStringFromFile(Path path) {
		return readStringFromFile(path.toFile());
	}
	public static String readStringFromFile(File file) {
		try {
			return FileUtil.readStringFromFile(file, DEFAULT_TESTDATA_ENCODING);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public static void writeStringToFile(Path file, String string) {
		writeStringToFile(file.toFile(), string);
	}
	public static void writeStringToFile(File file, String string) {
		try {
			StreamUtil.writeStringToStream(string, new FileOutputStream(file), DEFAULT_TESTDATA_ENCODING);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public static void appendStringToFile(File file, String string) {
		try {
			StreamUtil.writeStringToStream(string, new FileOutputStream(file, true), DEFAULT_TESTDATA_ENCODING);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	/* -------- iteration/checkers -------- */
	
	public static interface Visitor<T> {
		void visit(T obj);
	}
	
	@SafeVarargs
	public static <T, PRED extends Visitor<T>> void visitContainer(Collection<T> coll, PRED... predicates) {
		Iterator<T> iterator = coll.iterator();
		assertTrue(coll.size() == predicates.length);
		for (int i = 0; iterator.hasNext(); i++) {
			T next = iterator.next();
			predicates[i].visit(next);
		}
	}
	
	@SafeVarargs
	public static <T, PRED extends Visitor<T>> void visitContainer(T[] coll, PRED... predicates) {
		assertTrue(coll.length == predicates.length);
		for (int i = 0; i < coll.length; i++) {
			T next = coll[i];
			predicates[i].visit(next);
		}
	}
	
	public static final Path IGNORE_PATH = Paths.get("###NO_CHECK###");
	public static final String IGNORE_STR = "###NO_CHECK###";
	public static final Object[] IGNORE_ARR = new Object[0];
	public static final String[] IGNORE_ARR_STR = new String[0];
	
	public interface Checker<T> {
		
		void check(T obj);
		
	}
	
	/** Helper class to check result values against expected ones. */
	public static abstract class CommonChecker {
		
		public void checkAreEqual(Object obj, Object expected) {
			if(expected == IGNORE_PATH || expected == IGNORE_STR)
				return;
			assertTrue(CoreUtil.areEqual(obj, expected));
		}
		
		public void checkAreEqualArray(Object[] obj, Object[] expected) {
			if(isIgnoreArray(expected))
				return;
			assertTrue(CoreUtil.areEqualArrays(obj, expected));
		}
		
		protected boolean isIgnoreArray(Object[] expected){
			return expected == IGNORE_ARR || expected == IGNORE_ARR_STR;
		}
		
		protected Object[] ignoreIfNull(Object[] expected) {
			return expected == null ? IGNORE_ARR : expected;
		}
		
	}
	
	public static class MapChecker<K, V> implements Runnable {
		
		public final Map<K, V> map;
		
		protected final ArrayList<MapEntryChecker> entryChecks = new ArrayList<>();
		
		public MapChecker(Map<K, V> map) {
			this.map = new HashMap<>(map);
		}
		
		@Override
		public void run() {
			runEntryChecks();
			assertTrue(map.size() == 0);
		}
		
		protected void runEntryChecks() {
			for (MapEntryChecker check : entryChecks) {
				check.run();
			}
		}
		
		public abstract class MapEntryChecker implements Runnable {
			
			public V getExpectedEntry(K key) {
				assertTrue(map.containsKey(key));
				return map.remove(key);
			}
			
		}
		
	}
	
}