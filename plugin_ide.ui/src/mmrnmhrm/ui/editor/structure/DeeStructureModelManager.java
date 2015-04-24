/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.structure;

import melnorme.lang.ide.ui.editor.structure.StructureModelManager;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.lang.utils.M_WorkerThread;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.engine_client.DToolClient;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.parser.structure.DeeStructureCreator;

public class DeeStructureModelManager extends StructureModelManager {
	
	protected final DToolClient dtoolClient = DToolClient.getDefault();
	
	public DeeStructureModelManager() {
	}
	
	@Override
	public void rebuild(Location location, String source, M_WorkerThread reconcilerWorkerThread) {
		ParsedModule parsedModule = setWorkingCopyAndParse(location, source);
		
		// Note: there should not be multiple reconciler threads for the same location
		
		SourceFileStructure moduleStructure = new DeeStructureCreator().createStructure(location, parsedModule);
		addNewStructure(location, moduleStructure);
	}
	
	protected ParsedModule setWorkingCopyAndParse(Location location, String source) {
		return dtoolClient.getServerSemanticManager().setWorkingCopyAndParse(location.toPath(), source);
	}
	
}