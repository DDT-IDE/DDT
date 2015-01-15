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

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.misc.ArrayUtil;

/**
 * Utility class for lists of nodes.
 * Has additional info saying if parsing encountered an endingseparator or not;
 */
public class NodeVector<E extends IASTNode> extends ArrayView<E> {
	
	public final boolean hasEndingSeparator;

	public NodeVector(E[] array) {
		this(array, false);
	}
	
	public NodeVector(E[] array, boolean hasEndingSeparator) {
		super(array);
		this.hasEndingSeparator = hasEndingSeparator;
	}
	
	public static <T extends IASTNode> NodeVector<T> castTypeParameter(NodeVector<?> nodeVector) {
		return CoreUtil.<NodeVector<T>>blindCast(nodeVector);
	}
	
	public NodeVector<E> cloneTree() {
		E[] newArray = ArrayUtil.createWithSameComponentType(array.length, array); 
		
		for (int i = 0; i < array.length; i++) {
			newArray[i] = cloneNode(get(i));
		}
		
		return new NodeVector<>(newArray, hasEndingSeparator);
	}
	
	@SuppressWarnings("unchecked")
	protected E cloneNode(E node) {
		return node == null ? null : (E) node.cloneTree();
	}
	
}