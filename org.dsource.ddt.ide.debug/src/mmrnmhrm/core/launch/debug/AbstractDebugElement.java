/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.launch.debug;


import mmrnmhrm.core.DeeCore;

import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

public abstract class AbstractDebugElement extends DebugElement implements IDebugElement {
	
	public static enum DebugExecutionState { 
		RUNNING, SUSPENDED, TERMINATED;
		
		public boolean canResume() {
			return this == SUSPENDED;
		}
		
		public boolean canSuspend() {
			return this == RUNNING;
		}
		
		public boolean isSuspended() {
			return this == SUSPENDED;
		}
		
		public boolean isTerminated() {
			return this == TERMINATED;
		} 
	};
	
	@Override
	public String getModelIdentifier() {
		return DeeCore.PLUGIN_ID;
	}
	
	public AbstractDebugElement(IDebugTarget target) {
		super(target);
	}
	
}