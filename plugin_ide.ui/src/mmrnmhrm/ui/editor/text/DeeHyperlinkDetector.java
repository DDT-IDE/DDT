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

import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.ide.ui.editor.LangHyperlinkDetector;
import mmrnmhrm.ui.actions.DeeOpenDefinitionOperation;

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeHyperlinkDetector extends LangHyperlinkDetector {
	
	@Override
	protected AbstractLangElementHyperlink createHyperlink(IRegion requestedRegion, ITextEditor textEditor,
			IRegion wordRegion) {
		return new DeeElementHyperlink(wordRegion, textEditor);
	}
	
	public class DeeElementHyperlink extends AbstractLangElementHyperlink {
		
		public DeeElementHyperlink(IRegion region, ITextEditor textEditor) {
			super(region, textEditor);
		}
		
		@Override
		public String getHyperlinkText() {
			return "Go to D element definition";
		}
		
		@Override
		public void open() {
			new DeeOpenDefinitionOperation(textEditor, OpenNewEditorMode.TRY_REUSING_EXISTING, getOffset())
				.executeAndHandleResult();
		}
		
	}
	
}