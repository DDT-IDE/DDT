/*******************************************************************************
 * Copyright (c) 2006, 2010 Wind River Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Wind River Systems - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.debug.ui.viewmodel;

import org.eclipse.cdt.dsf.concurrent.ThreadSafe;
import org.eclipse.cdt.dsf.debug.ui.IDsfDebugUIConstants;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.AbstractDebugVMAdapter;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.SteppingController;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.modules.ModulesVMProvider;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.register.RegisterVMProvider;
import org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.breakpoints.GdbBreakpointVMProvider;
import org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch.LaunchVMProvider;
import org.eclipse.cdt.dsf.gdb.ui.viewmodel.GdbExpressionVMProvider;
import org.eclipse.cdt.dsf.gdb.ui.viewmodel.GdbVariableVMProvider;
import org.eclipse.cdt.dsf.gdb.ui.viewmodel.GdbViewModelAdapter;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IColumnPresentationFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.ui.IDebugUIConstants;

/*
 * 
 */
@ThreadSafe
public class LangGdbViewModelAdapter extends GdbViewModelAdapter
{
    public LangGdbViewModelAdapter(DsfSession session, SteppingController controller) {
        super(session, controller);
    }
    
    @Override
    protected GdbVariableVMProvider createGdbVariableVMProvider(IPresentationContext context) {
		return new Lang_GdbVariableVMProvider(this, context, getSession());
	}
    
    @Override
    protected GdbExpressionVMProvider createGdbExpressionVMProvider(IPresentationContext context) {
		return new Lang_GdbExpressionVMProvider(this, context, getSession());
	}
    
}