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
package dtool.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import melnorme.utilbox.misc.HashcodeUtil;
import melnorme.utilbox.misc.Location;
import dtool.engine.compiler_installs.CompilerInstall;

public final class ResolutionKey {
	
	protected final BundleKey bundleKey;
	protected final CompilerInstall compilerInstall;
	
	public ResolutionKey(BundleKey bundleKey, CompilerInstall compilerInstall) {
		this.bundleKey = assertNotNull(bundleKey);
		this.compilerInstall = assertNotNull(compilerInstall);
	}
	
	public Location getBundleLocation() {
		return bundleKey.getBundleLocation().getLocation();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof ResolutionKey)) return false;
		
		ResolutionKey other = (ResolutionKey) obj;
		
		return areEqual(bundleKey, other.bundleKey) && areEqual(compilerInstall, other.compilerInstall) ;
	}
	
	@Override
	public int hashCode() {
		return HashcodeUtil.combinedHashCode(bundleKey, compilerInstall);
	}
	
	@Override
	public String toString() {
		return bundleKey.toString() + " {" + compilerInstall.getCompilerPath() + "}";
	}
	
}