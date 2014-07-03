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
package mmrnmhrm.core.model_elements;

import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;

/**
 * Prints element delta - for debugging purposes only 
 */
public class ScriptModelDeltaPrinter implements IElementChangedListener {
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		
		int eventType = event.getType();
		System.out.println("---- element delta: " + eventType);
		System.out.println(event);
		if(eventType != ElementChangedEvent.POST_CHANGE)
			return;
	}
	
}