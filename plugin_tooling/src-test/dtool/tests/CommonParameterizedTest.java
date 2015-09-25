/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.tests;

import org.junit.Test;

public abstract class CommonParameterizedTest extends CommonDToolTest {
	
	protected final Runnable testRunnable;
	
	public CommonParameterizedTest(@SuppressWarnings("unused") String testUIDescription, Runnable testRunnable) {
		this.testRunnable = testRunnable;
	}
	
	@Test
	public void testname() throws Exception {
		testRunnable.run();
	}
	
}
