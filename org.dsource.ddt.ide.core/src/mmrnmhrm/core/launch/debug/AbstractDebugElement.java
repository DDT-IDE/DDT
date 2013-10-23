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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import mmrnmhrm.core.DeeCore;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;

public abstract class AbstractDebugElement implements IDebugElement {
	
	public static enum DebugExecutionStatus { 
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

	@Override
	public abstract DeeDebugTarget getDebugTarget();

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public static class ChildDebugElement extends AbstractDebugElement {
		
		protected final DeeDebugTarget debugTarget;

		public ChildDebugElement(DeeDebugTarget debugTarget) {
			super();
			this.debugTarget = assertNotNull(debugTarget);
		}
		
		@Override
		public DeeDebugTarget getDebugTarget() {
			return debugTarget;
		}
		
		@Override
		public ILaunch getLaunch() {
			return debugTarget.getLaunch();
		}
		
	}
	
}