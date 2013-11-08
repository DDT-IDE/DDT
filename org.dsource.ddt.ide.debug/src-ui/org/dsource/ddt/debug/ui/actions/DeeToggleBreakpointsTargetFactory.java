/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.debug.ui.actions;

import static melnorme.utilbox.core.CoreUtil.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dsource.ddt.debug.ui.DebugMessages;
import org.dsource.ddt.ui.DeeUIPlugin;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

public class DeeToggleBreakpointsTargetFactory implements IToggleBreakpointsTargetFactory {
	
	public DeeToggleBreakpointsTargetFactory() {
	}
	
	public static String DEE_BREAKPOINT_FACTORY_ID = DeeUIPlugin.PLUGIN_ID + "DeeBreakpointFactory";
	
	@Override
	public Set<?> getToggleTargets(IWorkbenchPart part, ISelection selection) {
		return new HashSet<>(Arrays.asList(array(DEE_BREAKPOINT_FACTORY_ID)));
	}
	
	@Override
	public String getDefaultToggleTarget(IWorkbenchPart part, ISelection selection) {
		return DEE_BREAKPOINT_FACTORY_ID;
	}
	
	@Override
	public IToggleBreakpointsTarget createToggleTarget(String targetID) {
		if(targetID.equals(DEE_BREAKPOINT_FACTORY_ID)) {
			return new DeeToggleBreakpointAdapter();
		}
		return null;
	}
	
	@Override
	public String getToggleTargetName(String targetID) {
		return DebugMessages.DDT_BREAKPOINT_TARGET_NAME;
	}
	
	@Override
	public String getToggleTargetDescription(String targetID) {
		return DebugMessages.DDT_BREAKPOINT_TARGET_DESCRIPTION;
	}
	
}
