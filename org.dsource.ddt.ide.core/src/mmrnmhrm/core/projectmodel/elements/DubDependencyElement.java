/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.projectmodel.elements;

import static melnorme.utilbox.core.CoreUtil.arrayFrom;

import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.dltk.core.IScriptProject;

import dtool.dub.DubBundle;

public class DubDependencyElement extends CommonDubElement<DubDependenciesContainer> {
	
	protected final DubBundle dubBundle;
	protected final DubDepSourceFolderElement[] children;
	
	public DubDependencyElement(DubDependenciesContainer parent, DubBundle dubBundle) {
		super(parent);
		this.dubBundle = dubBundle;
		this.children = createChildren();
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_RESOLVED_DEP;
	}
	
	public String getBundleName() {
		return dubBundle.name;
	}
	
	@Override
	public String getElementName() {
		return getBundleName();
	}
	
	@Override
	public String getPathString() {
		return getParent().getPathString() + "/["+getBundleName()+"]";
	}
	
	public DubBundle getDubBundle() {
		return dubBundle;
	}
	
	protected DubDepSourceFolderElement[] createChildren() {
		ArrayList<DubDepSourceFolderElement> sourceContainers = new ArrayList<>();
		IScriptProject scriptProject = getParent().getScriptProject();
		
		for (Path localPath : dubBundle.getEffectiveSourceFolders()) {
			sourceContainers.add(new DubDepSourceFolderElement(this, localPath, scriptProject));
		}
		return arrayFrom(sourceContainers, DubDepSourceFolderElement.class);
	}
	
	@Override
	public boolean hasChildren() {
		return children.length > 0;
	}
	
	@Override
	public Object[] getChildren() {
		return children;
	}
	
}