/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling;

import static melnorme.utilbox.core.CoreUtil.list;

import java.nio.file.Path;

import melnorme.lang.tooling.bundle.AbstractBundlePath;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.PathUtil;

public class BundlePath extends AbstractBundlePath {
	
	public static final String DUB_MANIFEST_NAME_JSON = "dub.json";
	
	public BundlePath(Location location) {
		super(location);
	}
	
	@Override
	public Location getManifestLocation(boolean provideDefault) {
		Path manifest_json = PathUtil.createValidPath(DUB_MANIFEST_NAME_JSON);
		Path manifest_sdl = PathUtil.createValidPath("dub.sdl");
		Path manifest_default = provideDefault ? manifest_json : null;
		return getManifest(getLocation(), list(manifest_json, manifest_sdl), manifest_default);
	}
	
}