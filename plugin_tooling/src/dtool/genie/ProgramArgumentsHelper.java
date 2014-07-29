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
package dtool.genie;

import java.util.Iterator;
import java.util.LinkedHashSet;

public abstract class ProgramArgumentsHelper {
	
	protected final LinkedHashSet<String> parsedArgs = new LinkedHashSet<>();
	
	protected void parseArgs(String[] rawArgs) {
		for (String arg : rawArgs) {
			if(parsedArgs.contains(arg)) {
				handleArgumentsError("Duplicate argument: " + arg);
			}
			parsedArgs.add(arg);
		}
	}
	
	protected abstract RuntimeException handleArgumentsError(String message);
	
	protected boolean getFlag(String argName) {
		return parsedArgs.remove(argName);
	}
	
	protected String retrieveFirstUnparsedArgument() {
		Iterator<String> iterator = parsedArgs.iterator();
		if(iterator.hasNext()) {
			String next = iterator.next();
			iterator.remove();
			return next;
		}
		return null;
	}
	
	protected void validateNoMoreArguments() {
		String firstUnparsedArgument = retrieveFirstUnparsedArgument();
		if(firstUnparsedArgument != null) {
			handleArgumentsError("Unknown argument: " + firstUnparsedArgument);
		}
	}
	
	protected int validatePositiveInt(String stringArgument) {
		int intValue = parseInt(stringArgument);
		if(intValue < 0) {
			handleArgumentsError("Argument not positive integer: " + stringArgument);
		}
		return intValue;
	}
	
	protected int parseInt(String stringArg) {
		try {
			return Integer.parseInt(stringArg);
		} catch (NumberFormatException e) {
			throw handleArgumentsError("Argument not integer: " + stringArg);
		}
	}
	
}