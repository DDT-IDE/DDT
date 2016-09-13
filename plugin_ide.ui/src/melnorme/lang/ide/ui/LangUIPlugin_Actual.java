package melnorme.lang.ide.ui;

import java.util.List;

import org.eclipse.ui.texteditor.ITextEditor;

import LANG_PROJECT_ID.ide.core_text.DeeDocumentSetupParticipant;
import LANG_PROJECT_ID.ide.core_text.LangDocumentPartitionerSetup;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.text.format.ILastKeyInfoProvider;
import melnorme.lang.ide.ui.editor.hover.ILangEditorTextHover;
import melnorme.lang.ide.ui.editor.text.LangAutoEditsPreferencesAccess;
import melnorme.lang.ide.ui.views.StructureElementLabelProvider;
import mmrnmhrm.core.text.DeeAutoEditStrategy;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.editor.DeeFmtOperation;
import mmrnmhrm.ui.editor.hover.DeeDocTextHover;

/**
 * Actual/concrete IDE constants and other bindings, for Lang UI code. 
 */
public final class LangUIPlugin_Actual {
	
	public static final String PLUGIN_ID = DeeUIPlugin.PLUGIN_ID;
	
	public static final String ROOT_PREF_PAGE_ID = PLUGIN_ID + ".PreferencePages.Root";
	
	public static final String RULER_CONTEXT = "#DeeRulerContext";
	public static final String EDITOR_CONTEXT = "#DeeEditorContext";
	
	
	// ID to start the debug plugin automatically, if present
	protected static final String DEBUG_PLUGIN_ID = "org.dsource.ddt.ide.debug";
	
	protected static final Class<?> PLUGIN_IMAGES_CLASS = DeeImages.class;
	
	protected static void initTextHovers_afterProblemHover(List<Class<? extends ILangEditorTextHover<?>>> textHoverSpecifications) {
		textHoverSpecifications.add(DeeDocTextHover.class);
	}
	
	public static DeeAutoEditStrategy createAutoEditStrategy(String contentType, 
		ILastKeyInfoProvider lastKeyInfoProvider) {
		return new DeeAutoEditStrategy(contentType, new LangAutoEditsPreferencesAccess(), lastKeyInfoProvider);
	}
	
	public static LangDocumentPartitionerSetup createDocumentSetupHelper() {
		return new DeeDocumentSetupParticipant();
	}
	
	public static StructureElementLabelProvider getStructureElementLabelProvider() {
		return new StructureElementLabelProvider() {
		};
	}
	
	/* ----------------- UI messages:  ----------------- */
	
	public static final String BUILD_ConsoleName = LangCore_Actual.NAME_OF_LANGUAGE + " Build";
	public static final String ENGINE_TOOLS_ConsoleName = LangCore_Actual.NAME_OF_LANGUAGE + " Tools Log";
	
	
	/* -----------------  ----------------- */
	
	public static DeeFmtOperation getFormatOperation(ITextEditor editor) {
		return new DeeFmtOperation(editor);
	}
	
}