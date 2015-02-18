package melnorme.lang.ide.ui;

import java.util.List;

import melnorme.lang.ide.ui.editor.ILangEditorTextHover;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.editor.hover.AnnotationHover_Adapter;
import mmrnmhrm.ui.editor.hover.DeeDocTextHover;
import mmrnmhrm.ui.editor.text.DeeAutoEditStrategy;

import org.eclipse.jface.text.source.ISourceViewer;

/**
 * Actual/concrete IDE constants and other bindings, for Lang UI code. 
 */
public final class LangUIPlugin_Actual {
	
	public static final String PLUGIN_ID = DeeUIPlugin.PLUGIN_ID;
	
	public static final String RULER_CONTEXT = "#DeeRulerContext";
	public static final String EDITOR_CONTEXT = "#DeeEditorContext";
	
	
	// ID to start the debug plugin automatically, if present
	protected static final String DEBUG_PLUGIN_ID = "org.dsource.ddt.ide.debug";
	
	protected static final Class<?> PLUGIN_IMAGES_CLASS = DeeImages.class;
	
	protected static void initTextHovers(List<Class<? extends ILangEditorTextHover<?>>> textHoverSpecifications) {
		textHoverSpecifications.add(AnnotationHover_Adapter.ProblemHover_Adapter.class);
		textHoverSpecifications.add(DeeDocTextHover.class);
		textHoverSpecifications.add(AnnotationHover_Adapter.class);
	}
	
	public static DeeAutoEditStrategy createAutoEditStrategy(ISourceViewer sourceViewer, String contentType) {
		return new DeeAutoEditStrategy(contentType, sourceViewer);
	}
	
	/* ----------------- UI messages:  ----------------- */
	
	public static final String LANGUAGE_NAME = "D";
	public static final String DAEMON_TOOL_Name = "lang_daemon";
	public static final String DAEMON_TOOL_ConsoleName = "lang_daemon log";
	
}