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
package melnorme.utilbox.core;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.text.MessageFormat;

import melnorme.utilbox.status.Severity;
import melnorme.utilbox.status.StatusException;

/**
 * A generic status exception.
 * Has an associated message, and optionally an associated Exception cause.
 */
public class CommonException extends Exception {
	
	public static CommonException fromMsgFormat(String pattern, Object... arguments) {
		return new CommonException(MessageFormat.format(pattern, arguments));
	}
	
	/* -----------------  ----------------- */
	
	private static final long serialVersionUID = -7324639626503261646L;
	
	public CommonException(String message) {
		this(assertNotNull(message), null);
	}
	
	public CommonException(String message, Throwable cause) {
		super(assertNotNull(message), cause);
	}
	
	public StatusException toStatusException() {
		return toStatusException(Severity.ERROR);
	}
	
	public StatusException toStatusException(Severity severity) {
		if(this instanceof StatusException) {
			return (StatusException) this;
		} else {
			return new StatusException(severity, getMessage(), getCause());
		}
	}
	
	/**
	 * Render this exception and all the chained exception into a user-readable string.
	 * The string will be one line only, assuming all the exceptions don't have newlines in their messages.  
	 */
	public String getSingleLineRender() {
		return new ExceptionStringRenderer(false).getLineRender(this);
	}
	
	public String getMultiLineRender() {
		return new ExceptionStringRenderer(true).getLineRender(this);
	}
	
	public static class ExceptionStringRenderer {
		
		public final boolean separateWithNewLines;
		
		public ExceptionStringRenderer(boolean separateWithNewLines) {
			this.separateWithNewLines = separateWithNewLines;
		}

		public String getLineRender(Throwable exception) {
			StringBuilder sb = new StringBuilder();
			renderToString(sb, exception);
			return sb.toString();
		}
		
		public void renderToString(StringBuilder sb, Throwable exception) {
			String message = exception.getMessage();
			if(message != null) {
				sb.append(message);
			} else {
				sb.append("["+exception.getClass().getSimpleName()+"]");
			}
			
			Throwable cause = exception.getCause();
			if(cause != null) {
				renderCause(sb, message, cause);
			}
		}
		
		public void renderCause(StringBuilder sb, String message, Throwable cause) {
			if(message == null) {
				sb.append(" ");
			} else if(!message.endsWith(":") && !message.endsWith(": ")) {
				sb.append(": ");
			}
			if(separateWithNewLines) {
				sb.append("\n");
			}
			
			renderToString(sb, cause);
		}
	}
	
}