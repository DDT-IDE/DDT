/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.data;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.core.CommonException;

@SuppressWarnings("serial")
public class StatusException extends CommonException implements IStatusMessage {
	
	protected final StatusLevel statusLevel;
	
	public StatusException(StatusLevel statusLevel, String message) {
		this(statusLevel, message, null);
	}
	
	public StatusException(StatusLevel statusLevel, String message, Throwable cause) {
		super(message, cause);
		this.statusLevel = assertNotNull(statusLevel);
	}
	
	@Override
	public StatusLevel getStatusLevel() {
		return statusLevel;
	}
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}
	
	public static StatusException toStatusException(IStatusMessage status) {
		if(status instanceof StatusException) {
			return (StatusException) status;
		}
		return new StatusException(status.getStatusLevel(), status.getMessage());
	}
	
}