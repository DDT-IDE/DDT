/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.codeassist;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import mmrnmhrm.core.parser.DeeModelElement_Test;

public class DeeSelectionEngine_Test extends DeeModelElement_Test {
	
	
	@Override
	protected void checkElementExists(ISourceModule sourceModule, IMember element, String code) throws ModelException {
		String source = sourceModule.getSource();
		
		IModelElement selectedElement = sourceModule.getElementAt(source.indexOf(code));
		assertEquals(selectedElement, element);
	}
	
}
