/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package dtool;


public class SimpleLogger {

	public static boolean masterLoggEnabled = true;

	protected boolean enabled = true;
	
	public SimpleLogger(boolean enabled) {
		this.enabled = enabled;
	}
	public SimpleLogger() {
		this(true);
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}

	public void println(Object... objs) {
		for(Object obj : objs)
			print(obj);
		println();
	}

	public void print(Object string) {
		if (masterLoggEnabled && enabled)
			System.out.print(string);
	}

	public void println() {
		if (masterLoggEnabled && enabled)
			System.out.println();
	}

}
