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
package melnorme.lang.ide.ui;

import melnorme.lang.ide.ui.views.AbstractLangImageProvider;
import melnorme.lang.tooling.LANG_SPECIFIC;
import melnorme.util.swt.jface.IManagedImage;
import mmrnmhrm.ui.DeeImages;

@LANG_SPECIFIC
public class LangImageProvider extends AbstractLangImageProvider {
	
	@Override
	public IManagedImage visitModule() {
		return DeeImages.ELEM_MODULE;
	}
	
	@Override
	public IManagedImage visitEnum() {
		return super.visitEnum();
	}
	
	@Override
	public IManagedImage visitUnion() {
		return DeeImages.ENT_UNION;
	}
	
	@Override
	public IManagedImage visitTemplate() {
		return DeeImages.ENT_TEMPLATE;
	}
	
	@Override
	public IManagedImage visitMixin() {
		return DeeImages.ENT_MIXIN;
	}
	
	@Override
	public IManagedImage visitPackage() {
		return DeeImages.ELEM_PACKAGE;
	}
	
	@Override
	public IManagedImage visitTuple() {
		return DeeImages.ENT_TUPLE;
	}
	
	@Override
	public IManagedImage visitType() {
		return DeeImages.ENT_TYPE_PARAMETER;
	}
	
}