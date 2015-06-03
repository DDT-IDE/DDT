/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.folding;


/**
 * Contributors to the <code>org.eclipse.dltk.ui.foldingStructureProvider</code>
 * extension point must specify an implementation of this interface which will
 * create and maintain
 * {@link org.eclipse.jface.text.source.projection.ProjectionAnnotation} objects
 * that define folded regions in the
 * {@link org.eclipse.jface.text.source.projection.ProjectionViewer}.
 * <p>
 * Clients may implement this interface.
 * </p>
 */
public interface IFoldingStructureProvider {

	/**
	 * Uninstalls this structure provider. Any references to editors or viewers
	 * should be cleared.
	 */
	public abstract void uninstall();

	/**
	 * Initializes the structure provided by the receiver.
	 */
	public abstract void initialize();

	/**
	 * Initializes the structure provided by the receiver. If
	 * <code>isReinit</code> is true, the structure will be reinitialized.
	 * 
	 * @param isReinit
	 *            <code>true</code> if a reinitialization is required,
	 *            <code>false</code> otherwise.
	 */
	public abstract void initialize(boolean isReinit);
}
