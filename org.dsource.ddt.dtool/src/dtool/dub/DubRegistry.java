/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.dub;

import static melnorme.utilbox.core.CoreUtil.array;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class DubRegistry {
	
	protected Path dubLocation; 
	protected final HashMap<String, DubBundle> bundles = new HashMap<>();
	
	public DubRegistry(Path dubLocation) {
		this();
		this.dubLocation = dubLocation;
	}
	
	public DubRegistry() {
		// for now, create a mock registry
		new DubBundle("XptoLib", "master",
				Paths.get("D:/devel/tools.D\\D.libs\\D-ext"),
				array(Paths.get("Kramer"), Paths.get("stdext")),
				null,
				null
				);
	}
	
	public DubBundle getBundle(String string) {
		return bundles.get(string);
	}
	
}