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
package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

public class ModelDeltaVisitor implements IElementChangedListener {
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		int eventType = event.getType();
		if(eventType != ElementChangedEvent.POST_CHANGE)
			return;
		
		IModelElementDelta delta = event.getDelta();
		
		IModelElementDelta[] affectedProjects = delta.getAffectedChildren();
		for (IModelElementDelta projectDelta : affectedProjects) {
			assertTrue(projectDelta.getElement() instanceof IScriptProject);
			
			for (IModelElementDelta fragmentDelta : projectDelta.getAffectedChildren()) {
				assertTrue(fragmentDelta.getElement() instanceof IProjectFragment);
				
				for (IModelElementDelta folderDelta : fragmentDelta.getAffectedChildren()) {
					assertTrue(folderDelta.getElement() instanceof IScriptFolder);
					
					for (IModelElementDelta moduleDelta : folderDelta.getAffectedChildren()) {
						IModelElement element = moduleDelta.getElement();
						assertTrue(element instanceof ISourceModule);
						ISourceModule sourceModule = (ISourceModule) element;
						
						visitModule(moduleDelta, sourceModule);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	protected void visitModule(IModelElementDelta moduleDelta, ISourceModule sourceModule) {
	}
	
}