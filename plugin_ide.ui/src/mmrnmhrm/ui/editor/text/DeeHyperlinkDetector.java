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
package mmrnmhrm.ui.editor.text;

import java.nio.file.Path;

import melnorme.lang.ide.ui.editor.EditorUtils;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.references.NamedReference;

public class DeeHyperlinkDetector extends AbstractHyperlinkDetector {
	
	public static final String DEE_EDITOR_TARGET = DeeUIPlugin.PLUGIN_ID + ".texteditor.deeCodeTarget";
	
	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null)
			return null;
		
		ITextEditor textEditor= (ITextEditor) getAdapter(ITextEditor.class);
		Path filePath = EditorUtils.getFilePathFromEditorInput(textEditor.getEditorInput());
		
		ASTNode module = DToolClient.getDefaultModuleCache().getExistingParsedModuleNode(filePath);
		ASTNode selNode = ASTNodeFinder.findElement(module, region.getOffset(), false);
		if(!(selNode instanceof NamedReference))
			return null;
		
		IRegion elemRegion = new Region(selNode.getOffset(), selNode.getLength());

		return new IHyperlink[] { new DeeElementHyperlink(region.getOffset(), elemRegion, textEditor) };
	}
	
}