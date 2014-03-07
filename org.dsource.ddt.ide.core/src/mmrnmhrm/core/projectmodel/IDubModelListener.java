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
package mmrnmhrm.core.projectmodel;

import mmrnmhrm.core.projectmodel.DubModel.IDubModel;
import dtool.dub.DubBundleDescription;

public interface IDubModelListener {
	
	/** 
	 * Note, several locks are held in the scope of this method (DubModel, and potentially Workspace Root).
	 * Do run long running or locking code in the implementation, 
	 * just post the event to another thread/agent/dispatcher to handle.
	 */
	void notifyUpdateEvent(IDubModel source, DubBundleDescription object);
	
}