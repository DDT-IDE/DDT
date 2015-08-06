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
package mmrnmhrm.ui.actions;

import melnorme.lang.ide.ui.utils.UIOperationsStatusHandler;
import melnorme.utilbox.core.CommonException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class DeeOpenTypeHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			throw new CommonException("Not supported");
		} catch(CommonException ce) {
			UIOperationsStatusHandler.handleOperationError("Open Type", ce);
			return null;
		}
	}
	
}