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
package melnorme.lang.ide.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for an event manager that fires update events to registered listeners.
 * This class is designed to be thread safe. 
 */
public class EventManager<SOURCE, EVENT_OBJ, T extends ICommonEventListener<SOURCE, EVENT_OBJ>> {
	
	protected final Object listenersLock = new Object();
	protected List<T> listeners = Collections.unmodifiableList(new ArrayList<T>());
	
	public void addListener(T listener) {
		ArrayList<T> newListeners = new ArrayList<>(listeners);
		newListeners.add(listener);
		
		setNewListeners(newListeners);
	}
	
	public void removeListener(T listener) {
		ArrayList<T> newListeners = new ArrayList<>(listeners);
		for (Iterator<T> iter = newListeners.iterator(); iter.hasNext(); ) {
			T iterElem = iter.next();
			if(iterElem == listener) {
				iter.remove();
				break;
			}
		}
		
		setNewListeners(newListeners);
		
	}
	
	private void setNewListeners(ArrayList<T> newListeners) {
		synchronized (listenersLock) {
			listeners = newListeners;
		}
	}
	
	protected List<T> getListeners() {
		return listeners;
	}
	
	protected void fireUpdateEvent(SOURCE source, EVENT_OBJ object) {
		List<T> listenersToIterate = getListeners();
		for (T listener : listenersToIterate) {
			listener.notifyUpdateEvent(source, object);
		}
	}
	
}