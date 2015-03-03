/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;

import melnorme.lang.ide.ui.editor.EditorUtils;
import mmrnmhrm.ui.DeeImages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.ProblemsLabelDecorator;
import org.eclipse.dltk.ui.viewsupport.IProblemChangedListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;

/**
 * The <code>JavaEditorErrorTickUpdater</code> will register as a IProblemChangedListener
 * to listen on problem changes of the editor's input. It updates the title images when the annotation
 * model changed.
 */
public class ScriptEditorErrorTickUpdater2 implements IProblemChangedListener {
	
	private ScriptEditor fScriptEditor;
	
	protected ProblemsLabelDecorator problemsDecorator = new ProblemsLabelDecorator(null);
	
	public ScriptEditorErrorTickUpdater2(ScriptEditor editor) {
		Assert.isNotNull(editor);
		fScriptEditor = editor;
		DLTKUIPlugin.getDefault().getProblemMarkerManager().addListener(this);
	}
	
	public void dispose() {
		DLTKUIPlugin.getDefault().getProblemMarkerManager().removeListener(this);
		problemsDecorator.dispose();
	}
	
	@Override
	public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
		if (!isMarkerChange)
			return;
		
		IEditorInput input= fScriptEditor.getEditorInput();
		if (input != null) { // might run async, tests needed
			IFile file = EditorUtils.getAssociatedFile(input);
			for (int i = 0; i < changedResources.length; i++) {
				if (changedResources[i].equals(file)) {
					updateEditorImage(file);
				}
			}
		}
	}
	
	public void updateEditorImage(IFile editorFile) {
		Image titleImage= fScriptEditor.getTitleImage();
		if (titleImage == null) {
			return;
		}
		Image baseImage = DeeImages.ELEM_FILE.getImage();
		Image newImage = problemsDecorator.decorateImage(baseImage, editorFile);
		
		if (newImage != null && titleImage != newImage) {
			postImageChange(newImage);
		}
	}
	
	protected void postImageChange(final Image newImage) {
		Shell shell= fScriptEditor.getEditorSite().getShell();
		if (shell != null && !shell.isDisposed()) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					fScriptEditor.updatedTitleImage(newImage);
				}
			});
		}
	}
	
}