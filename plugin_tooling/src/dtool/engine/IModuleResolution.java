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
package dtool.engine;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.IElementSemantics;


public interface IModuleResolution {
	
	Path getModulePath();
	
	ISemanticContext getBundleResolution();
	
	IElementSemantics getElementSemantics(ISemanticElement element);
	
	IElementSemantics putElementSemantics(ISemanticElement astNode, IElementSemantics createElementSemantics);
	
}