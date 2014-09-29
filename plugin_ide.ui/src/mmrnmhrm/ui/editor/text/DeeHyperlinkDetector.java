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
import mmrnmhrm.ui.actions.OpenDefinitionOperation;

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeHyperlinkDetector extends LangHyperlinkDetector {
	
	@Override
	protected AbstractLangElementHyperlink createHyperlink(IRegion region, ITextEditor textEditor, 
			IRegion elemRegion) {
		return new DeeElementHyperlink(region.getOffset(), region, textEditor);
	}
	
	public class DeeElementHyperlink extends AbstractLangElementHyperlink {
		
		public DeeElementHyperlink(int offset, IRegion region, ITextEditor textEditor) {
			super(offset, region, textEditor);
		}
		
		@Override
		public void open() {
			new OpenDefinitionOperation(textEditor, OpenNewEditorMode.TRY_REUSING_EXISTING_EDITORS, offset)
				.executeAndHandle();
		}
		
		@Override
		public String getHyperlinkText() {
			return "Go to D element definition";
		}
	}
	
}