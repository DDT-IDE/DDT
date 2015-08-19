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
package melnorme.lang.ide.core.tests;

import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubManifestParserTest;
import dtool.tests.MockCompilerInstalls;
import melnorme.lang.ide.core.BundleInfo;
import melnorme.lang.tooling.LANG_SPECIFIC;

@LANG_SPECIFIC
public class BuildTestsHelper {
	
	public static BundleInfo createSampleBundleInfoA(String name, String version) {
		return new BundleInfo(
			MockCompilerInstalls.DMD_CompilerInstall, 
			new DubBundleDescription(new DubBundle(DubManifestParserTest.SAMPLE_BUNDLE_PATH, name, null, version, 
				null, null, null, null, "sampleConfig", null, null))
		);
	}
	
}