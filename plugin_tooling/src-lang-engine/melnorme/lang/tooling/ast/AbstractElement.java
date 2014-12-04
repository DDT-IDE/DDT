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
package melnorme.lang.tooling.ast;

import java.nio.file.Path;


public abstract class AbstractElement extends CommonSemanticElement {
	
	protected final ISemanticElement parent;
	
	public AbstractElement(ISemanticElement parent) {
		this.parent = parent;
	}
	
	@Override
	public ISemanticElement getParent() {
		return parent;
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return parent == null ? true : parent.isLanguageIntrinsic();
	}
	
	@Override
	public Path getModulePath() {
		return parent == null ? null : parent.getModulePath();
	}
	
}