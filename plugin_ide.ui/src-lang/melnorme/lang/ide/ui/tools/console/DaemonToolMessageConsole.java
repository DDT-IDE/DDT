/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.tools.console;

import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.lang.ide.ui.LangUIPlugin_Actual;
import melnorme.lang.ide.ui.utils.ConsoleUtils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class DaemonToolMessageConsole extends ToolsConsole {
	
	public final IOConsoleOutputStream serverStdOut;
	public final IOConsoleOutputStream serverStdErr;
	
	public DaemonToolMessageConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor, false);

		serverStdOut = newOutputStream();
		serverStdErr = newOutputStream();
		
		postToUI_initOutputStreamColors();
	}
	
	@Override
	protected void ui_initStreamColors() {
		super.ui_initStreamColors();
		
		serverStdOut.setColor(getColorManager().getColor(new RGB(128, 0, 128)));
		serverStdErr.setColor(getColorManager().getColor(new RGB(255, 0, 200)));
		serverStdErr.setFontStyle(SWT.BOLD);
		
		stdErr.setActivateOnWrite(false);
	}
	
	public static DaemonToolMessageConsole getConsole() {
		DaemonToolMessageConsole console = ConsoleUtils.findConsole(
			LangUIPlugin_Actual.DAEMON_TOOL_ConsoleName, DaemonToolMessageConsole.class);
		if(console != null) {
			return console;
		}
		// no console, so create a new one
		DaemonToolMessageConsole msgConsole = new DaemonToolMessageConsole(
			LangUIPlugin_Actual.DAEMON_TOOL_ConsoleName, null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(array(msgConsole));
		return msgConsole;
	}
	
}