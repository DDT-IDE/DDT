/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.views;

import melnorme.lang.tooling.ElementAttributes;
import melnorme.util.swt.jface.resources.LangElementImageDescriptor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

public class DeeDecoratedImageDescriptor extends LangElementImageDescriptor {
	
	public DeeDecoratedImageDescriptor(ImageDescriptor baseImageDescriptor, ElementAttributes elementAttributes) {
		super(baseImageDescriptor, elementAttributes);
	}
	
	public DeeDecoratedImageDescriptor(Point size, ImageDescriptor baseImage, ElementAttributes elementAttributes) {
		super(size, baseImage, elementAttributes);
	}
	
}