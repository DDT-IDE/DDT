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
package melnorme.lang.tests;

import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubManifestParserTest;
import dtool.tests.MockCompilerInstalls;
import melnorme.lang.tooling.LANG_SPECIFIC;
import melnorme.lang.tooling.bundle.BundleInfo;
import melnorme.utilbox.misc.Location;

@LANG_SPECIFIC
public class ToolingTests_Actual {
	
	public static Location SAMPLE_SDK_PATH = LangToolingTestResources.getTestResourceLoc("mock_sdk")
			.resolve_fromValid("bin/dub");
	
	public static BundleInfo createSampleBundleInfoA(String name, String version) {
		return new BundleInfo(
			MockCompilerInstalls.DMD_CompilerInstall, 
			new DubBundleDescription(new DubBundle(DubManifestParserTest.SAMPLE_BUNDLE_PATH, name, null, version, 
				null, null, null, null, "sampleConfig", null, null))
		);
	}
	
}