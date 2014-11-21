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
package melnorme.lang.tooling.engine;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.AbstractElement2;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;

//TODO merge this and AbstractElement2
public abstract class AbstractElement extends AbstractElement2 implements ISemanticElement {
	
	public AbstractElement() {
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return false; /*FIXME: BUG here*/
	}
	
	@Override
	public Path getModulePath() {
		return null; /*FIXME: BUG here*/
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		/* FIXME: TODO*/
	}
	
}