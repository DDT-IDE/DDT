/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.workspace;

import melnorme.lang.ide.core.project_model.IProjectModelListener;
import melnorme.lang.ide.core.project_model.UpdateEvent;

public interface IDubModelListener extends IProjectModelListener<DubProjectInfo> {
	
	/** 
	 * Note, several locks are held in the scope of this method (DubModel, and potentially Workspace Root).
	 * Do NOT run long running or locking code in the implementation, 
	 * just post the event to another thread/agent/dispatcher to handle.
	 */
	@Override
	void notifyUpdateEvent(UpdateEvent<DubProjectInfo> updateEvent);
	
}