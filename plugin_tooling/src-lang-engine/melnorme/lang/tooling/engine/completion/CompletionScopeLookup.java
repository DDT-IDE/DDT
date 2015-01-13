/*******************************************************************************
 * Copyright (c) 2010, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.completion;

import java.util.Set;

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;

/** 
 * A scope name lookup that matches only symbols/names that start with a given prefix name. 
 */
public class CompletionScopeLookup extends CommonScopeLookup {
	
	public final String searchPrefix;
	
	public CompletionScopeLookup(int refOffset, ISemanticContext context, String searchPrefix) {
		super(refOffset, context);
		this.searchPrefix = searchPrefix;
	}
	
	@Override
	public Set<String> findMatchingModules() {
		return context.findModules(searchPrefix);
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
	
	@Override
	public boolean matchesName(String defName) {
		if(searchPrefix.length() > defName.length()) {
			return false;
		}
		String defNamePrefix = defName.substring(0, searchPrefix.length());
		return defNamePrefix.equalsIgnoreCase(searchPrefix);
	}
	
	@Override
	public String toString() {
		String str = getClass().getName() + " ---\n";
		str += "searchPrefix: " + searchPrefix +"\n";
		str += "----- Results: -----\n";
		str += toString_matches();
		return str;
	}
	
}