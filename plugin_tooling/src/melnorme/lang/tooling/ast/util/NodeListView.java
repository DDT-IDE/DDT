/*******************************************************************************
 * Copyright (c) 2013, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.ast.util;

import java.util.Iterator;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.misc.IteratorUtil;

/**
 * Utility class for lists of nodes.
 * Has additional info saying if parsing encountered an endingseparator or not;
 */
public class NodeListView<T extends IASTNode> extends ArrayView<T> {
	
	public final boolean hasEndingSeparator;
	
	public NodeListView(T[] array, boolean hasEndingSeparator) {
		super(array);
		this.hasEndingSeparator = hasEndingSeparator;
	}
	
	public static <T> Iterator<T> getIteratorSafe(Iterable<T> nodeList) {
		return nodeList == null ? IteratorUtil.<T>emptyIterator() : nodeList.iterator();
	}
	
}
