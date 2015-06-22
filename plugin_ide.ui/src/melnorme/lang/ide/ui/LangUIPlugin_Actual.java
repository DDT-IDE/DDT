package melnorme.lang.ide.ui;

import java.util.List;

import melnorme.lang.ide.ui.editor.hover.ILangEditorTextHover;
import melnorme.lang.ide.ui.views.StructureElementLabelProvider;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.editor.hover.DeeDocTextHover;
import mmrnmhrm.ui.editor.text.DeeAutoEditStrategy;

import org.eclipse.jface.text.source.ISourceViewer;

import _org.eclipse.jdt.internal.ui.text.java.hover.AnnotationHover;
import _org.eclipse.jdt.internal.ui.text.java.hover.ProblemHover;

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
	
	protected static void initTextHovers(List<Class<? extends ILangEditorTextHover<?>>> textHoverSpecifications) {
		textHoverSpecifications.add(ProblemHover.class);
		textHoverSpecifications.add(DeeDocTextHover.class);
		textHoverSpecifications.add(AnnotationHover.class);
	}
	
	public static DeeAutoEditStrategy createAutoEditStrategy(ISourceViewer sourceViewer, String contentType) {
		return new DeeAutoEditStrategy(contentType, sourceViewer);
	}
	
	public static StructureElementLabelProvider getStructureElementLabelProvider() {
		return new StructureElementLabelProvider() {
		};
	}
	
	/* ----------------- UI messages:  ----------------- */
	
	public static final String DAEMON_TOOL_Name = "lang_daemon";
	public static final String DAEMON_TOOL_ConsoleName = "lang_daemon log";
	
}