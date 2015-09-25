/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.context;

import melnorme.utilbox.core.CommonException;

/**
 * An exception representing a failure to retrieve the source of a module.
 * TODO: make this {@link CommonException}?
 */
public class ModuleSourceException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public ModuleSourceException(Throwable cause) {
		super(cause);
	}
	
}